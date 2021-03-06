package de.benjaminborbe.message.service;

import de.benjaminborbe.cron.api.CronJob;
import de.benjaminborbe.message.util.MessageUnlock;
import org.slf4j.Logger;

import javax.inject.Inject;

public class MessageUnlockCronJob implements CronJob {

	/* s m h d m dw y */
	private static final String SCHEDULE_EXPRESSION = "0 * * * * ?";

	private final MessageUnlock messageUnlock;

	private final Logger logger;

	@Inject
	public MessageUnlockCronJob(final Logger logger, final MessageUnlock messageUnlock) {
		this.logger = logger;
		this.messageUnlock = messageUnlock;
	}

	@Override
	public String getScheduleExpression() {
		return SCHEDULE_EXPRESSION;
	}

	@Override
	public void execute() {
		logger.trace("execute");
		messageUnlock.execute();
	}

	@Override
	public boolean disallowConcurrentExecution() {
		return false;
	}

}
