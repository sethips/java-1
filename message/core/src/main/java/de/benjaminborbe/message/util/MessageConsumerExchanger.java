package de.benjaminborbe.message.util;

import de.benjaminborbe.analytics.api.AnalyticsReportIdentifier;
import de.benjaminborbe.analytics.api.AnalyticsService;
import de.benjaminborbe.message.MessageConstants;
import de.benjaminborbe.message.api.MessageConsumer;
import de.benjaminborbe.message.api.MessageIdentifier;
import de.benjaminborbe.message.dao.MessageBean;
import de.benjaminborbe.message.dao.MessageBeanMapper;
import de.benjaminborbe.message.dao.MessageDao;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.tools.IdentifierIterator;
import de.benjaminborbe.storage.tools.IdentifierIteratorException;
import de.benjaminborbe.storage.tools.StorageValueList;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.synchronize.RunOnlyOnceATimeByType;
import de.benjaminborbe.tools.util.RandomUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Calendar;

@Singleton
public class MessageConsumerExchanger {

	private final class ExchangeMessage implements Runnable {

		private final MessageIdentifier messageIdentifier;

		private ExchangeMessage(final MessageIdentifier messageIdentifier) {
			this.messageIdentifier = messageIdentifier;
		}

		@Override
		public void run() {
			try {
				final MessageBean message = messageDao.load(messageIdentifier);
				if (message != null) {
					if (message.getType() == null) {
						logger.info("delete message without type - type: " + message.getType() + " id: " + message.getId());
						messageDao.delete(message);
						return;
					}

					exchange(message);
				}
			} catch (final StorageException e) {
				logger.warn(e.getClass().getName(), e);
			}
		}

	}

	private static final long DELAY_PER_RETRY = 1 * 60 * 1000;

	private final MessageConsumerRegistry messageConsumerRegistry;

	private final MessageDao messageDao;

	private final Logger logger;

	private final MessageLock messageLock;

	private final CalendarUtil calendarUtil;

	private final RandomUtil randomUtil;

	private final AnalyticsService analyticsService;

	private final AnalyticsReportIdentifier analyticsReportIdentifierSuccess = new AnalyticsReportIdentifier("MessageSuccess");

	private final AnalyticsReportIdentifier analyticsReportIdentifierRetry = new AnalyticsReportIdentifier("MessageRetry");

	private final AnalyticsReportIdentifier analyticsReportIdentifierMaxRetry = new AnalyticsReportIdentifier("MessageMaxRetry");

	private final TimeZoneUtil timeZoneUtil;

	private final RunOnlyOnceATimeByType runOnlyOnceATimeByType;

	@Inject
	public MessageConsumerExchanger(
		final Logger logger,
		final RunOnlyOnceATimeByType runOnlyOnceATimeByType,
		final AnalyticsService analyticsService,
		final RandomUtil randomUtil,
		final CalendarUtil calendarUtil,
		final MessageConsumerRegistry messageConsumerRegistry,
		final MessageDao messageDao,
		final TimeZoneUtil timeZoneUtil,
		final MessageLock messageLock
	) {
		this.logger = logger;
		this.runOnlyOnceATimeByType = runOnlyOnceATimeByType;
		this.analyticsService = analyticsService;
		this.randomUtil = randomUtil;
		this.calendarUtil = calendarUtil;
		this.messageConsumerRegistry = messageConsumerRegistry;
		this.messageDao = messageDao;
		this.timeZoneUtil = timeZoneUtil;
		this.messageLock = messageLock;
	}

	public boolean exchange() {
		try {
			logger.trace("exchange message - started");
			final IdentifierIterator<MessageIdentifier> i = messageDao.getIdentifierIterator();
			while (i.hasNext()) {
				final MessageIdentifier messageIdentifier = i.next();
				logger.trace("exchange message - id: " + messageIdentifier);
				runOnlyOnceATimeByType.run(String.valueOf(messageIdentifier), new ExchangeMessage(messageIdentifier));
			}
			logger.trace("exchange message - finished");
			return true;
		} catch (final StorageException e) {
			logger.warn(e.getClass().getName(), e);
		} catch (IdentifierIteratorException e) {
			logger.warn(e.getClass().getName(), e);
		}
		return false;
	}

	private void exchange(final MessageBean message, final MessageConsumer messageConsumer) throws StorageException {
		{
			final Calendar startTime = message.getStartTime();
			if (startTime != null) {
				final Calendar now = calendarUtil.now();
				if (startTime.getTimeInMillis() > now.getTimeInMillis()) {
					logger.trace("startTime not reached " + startTime.getTimeInMillis() + " > " + now.getTimeInMillis() + " => skip");
					return;
				} else {
					logger.trace("startTime reached " + startTime.getTimeInMillis() + " <= " + now.getTimeInMillis());
				}
			} else {
				logger.trace("startTime not defined");
			}
		}

		boolean result;
		try {
			if (lock(message)) {
				logger.trace("process message - type: " + message.getType() + " retryCounter: " + message.getRetryCounter());
				result = messageConsumer.process(message);
			} else {
				logger.debug("lock message failed => skip");
				return;
			}
		} catch (final Exception e) {
			logger.warn("process message failed", e);
			result = false;
		}
		final long counter = message.getRetryCounter() != null ? message.getRetryCounter() : 0;
		final long maxRetry = message.getMaxRetryCounter() != null ? message.getMaxRetryCounter() : MessageConstants.MAX_RETRY;
		if (result) {
			logger.trace("delete success processed message - type: " + message.getType() + " id: " + message.getId());
			messageDao.delete(message);
			logger.trace("result success => delete message");
			track(analyticsReportIdentifierSuccess);
		} else if (counter >= maxRetry) {
			logger.info("delete message reached maxRetryCounter - type: " + message.getType() + " id: " + message.getId());
			messageDao.delete(message);
			logger.debug("message reached maxRetryCounter => delete message");
			track(analyticsReportIdentifierMaxRetry);
		} else {
			final long increasedCounter = counter + 1;
			logger.debug("process message failed, increase retryCounter to " + increasedCounter);
			message.setRetryCounter(increasedCounter);
			message.setStartTime(calcStartTime(increasedCounter));
			message.setLockName(null);
			message.setLockTime(null);
			logger.info("unlock and increaseRetry for failed message - type: " + message.getType() + " id: " + message.getId());
			messageDao.save(
				message,
				new StorageValueList(getEncoding()).add(MessageBeanMapper.LOCK_TIME).add(MessageBeanMapper.LOCK_NAME).add(MessageBeanMapper.RETRY_COUNTER)
					.add(MessageBeanMapper.START_TIME));
			track(analyticsReportIdentifierRetry);
		}
		logger.trace("process message done");
	}

	private Calendar calcStartTime(final long retryCounter) {
		final long time = calendarUtil.getTime() + retryCounter * DELAY_PER_RETRY;
		return calendarUtil.getCalendar(timeZoneUtil.getUTCTimeZone(), time);
	}

	private void track(final AnalyticsReportIdentifier id) {
		try {
			analyticsService.addReportValue(id);
		} catch (final Exception e) {
			logger.warn("track " + id + " failed", e);
		}
	}

	private boolean lock(final MessageBean message) throws StorageException {
		if (message.getLockTime() == null) {
			final Calendar now = calendarUtil.now();
			logger.trace("try lock message - lockName: " + messageLock.getLockName() + " lockTime: " + calendarUtil.toDateTimeString(now));
			message.setLockName(messageLock.getLockName());
			message.setLockTime(now);
			logger.trace("lock message - type: " + message.getType() + " id: " + message.getId());
			messageDao.save(message, new StorageValueList(getEncoding()).add(MessageBeanMapper.LOCK_TIME).add(MessageBeanMapper.LOCK_NAME));

			try {
				Thread.sleep(randomUtil.getRandomized(10000, 50));
			} catch (final InterruptedException e) {
				// nop
			}

			messageDao.load(message, new StorageValueList(getEncoding()).add(MessageBeanMapper.LOCK_TIME));

			if (messageLock.getLockName().equals(message.getLockName())) {
				logger.trace("lock message success - id: " + message.getId());
				return true;
			} else {
				logger.debug("lock message failed - id: " + message.getId());
				return false;
			}
		} else if (messageLock.getLockName().equals(message.getLockName())) {
			final Calendar now = calendarUtil.now();
			logger.debug("update message lock - id: " + message.getId() + " lockName: " + messageLock.getLockName() + " lockTime: " + calendarUtil.toDateTimeString(now));
			message.setLockTime(now);
			logger.debug("extend message lockTime - type: " + message.getType() + " id: " + message.getId());
			messageDao.save(message, new StorageValueList(getEncoding()).add(MessageBeanMapper.LOCK_TIME).add(MessageBeanMapper.LOCK_NAME));
			return false;
		} else {
			logger.debug("lock message failed - id: " + message.getId());
			return false;
		}
	}

	private String getEncoding() {
		return messageDao.getEncoding();
	}

	private void exchange(final MessageBean message) throws StorageException {
		final MessageConsumer messageConsumer = messageConsumerRegistry.get(message.getType());
		if (messageConsumer != null) {
			logger.trace("messageConsumer found for type: " + message.getType());
			exchange(message, messageConsumer);
		} else {
			logger.warn("no messageConsumer found for type: " + message.getType());
		}
	}
}
