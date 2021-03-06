package de.benjaminborbe.dhl.util;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.notification.api.NotificationDto;
import de.benjaminborbe.notification.api.NotificationService;
import de.benjaminborbe.notification.api.NotificationServiceException;
import de.benjaminborbe.notification.api.NotificationTypeIdentifier;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.util.ParseException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;

@Singleton
public class DhlStatusNotifierImpl implements DhlStatusNotifier {

	private static final NotificationTypeIdentifier TYPE = new NotificationTypeIdentifier("dhl");

	private final Logger logger;

	private final CalendarUtil calendarUtil;

	private final DhlUrlBuilder dhlUrlBuilder;

	private final NotificationService notificationService;

	@Inject
	public DhlStatusNotifierImpl(
		final Logger logger,
		final CalendarUtil calendarUtil,
		final DhlUrlBuilder dhlUrlBuilder,
		final NotificationService notificationService
	) {
		this.logger = logger;
		this.calendarUtil = calendarUtil;
		this.dhlUrlBuilder = dhlUrlBuilder;
		this.notificationService = notificationService;
	}

	@Override
	public void notify(final UserIdentifier userIdentifier, final DhlStatus status) throws DhlStatusNotifierException {
		try {
			if (status == null) {
				throw new NullPointerException("parameter DhlStatus is null");
			}
			logger.info("new status " + status);

			final NotificationDto notification = new NotificationDto();
			notification.setTo(userIdentifier);
			notification.setType(TYPE);
			notification.setSubject(buildSubject(status));
			notification.setMessage(buildContent(status));
			notificationService.notify(notification);
		} catch (NotificationServiceException e) {
			throw new DhlStatusNotifierException(e);
		} catch (ValidationException e) {
			throw new DhlStatusNotifierException(e);
		}
	}

	protected String buildSubject(final DhlStatus status) {
		return "DHL-Status: " + status.getDhl().getTrackingNumber();
	}

	protected String buildContent(final DhlStatus status) {
		final StringBuilder content = new StringBuilder();
		{
			content.append("Date: ");
			content.append(calendarUtil.toDateTimeString(status.getCalendar()));
			content.append("\n");
		}
		{
			content.append("Place: ");
			content.append(status.getPlace());
			content.append("\n");
		}
		{
			content.append("Message: ");
			content.append(status.getMessage());
			content.append("\n");
		}
		{
			try {
				final URL url = dhlUrlBuilder.buildUrl(status.getDhl());
				content.append("Link: ");
				content.append(url);
				content.append("\n");
			} catch (final ParseException e) {
				logger.debug("build dhl-link failed!", e);
			}
		}
		return content.toString();
	}
}
