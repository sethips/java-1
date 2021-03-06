package de.benjaminborbe.lunch.gui.servlet;

import com.google.inject.Provider;
import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.lunch.api.LunchService;
import de.benjaminborbe.lunch.api.LunchServiceException;
import de.benjaminborbe.lunch.gui.LunchGuiConstants;
import de.benjaminborbe.lunch.gui.config.LunchGuiConfig;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.json.JSONObject;
import de.benjaminborbe.tools.json.JSONObjectSimple;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.website.servlet.WebsiteJsonServlet;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LunchGuiNotificationDeactivateJsonServlet extends WebsiteJsonServlet {

	private static final long serialVersionUID = 1885838810460233686L;

	private final Logger logger;

	private final LunchGuiConfig lunchGuiConfig;

	private final LunchService lunchService;

	@Inject
	public LunchGuiNotificationDeactivateJsonServlet(
		final Logger logger,
		final UrlUtil urlUtil,
		final AuthenticationService authenticationService,
		final AuthorizationService authorizationService,
		final CalendarUtil calendarUtil,
		final TimeZoneUtil timeZoneUtil,
		final Provider<HttpContext> httpContextProvider,
		final LunchGuiConfig lunchGuiConfig,
		final LunchService lunchService
	) {
		super(logger, urlUtil, authenticationService, authorizationService, calendarUtil, timeZoneUtil, httpContextProvider);
		this.logger = logger;
		this.lunchGuiConfig = lunchGuiConfig;
		this.lunchService = lunchService;
	}

	@Override
	protected void doService(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final HttpContext context
	) throws ServletException, IOException,
		PermissionDeniedException, LoginRequiredException {
		try {
			final String token = request.getParameter(LunchGuiConstants.PARAEMTER_NOTIFICATION_TOKEN);
			if (token == null || token.isEmpty() || !token.equals(lunchGuiConfig.getAuthToken())) {
				printError(response, "parameter " + LunchGuiConstants.PARAEMTER_NOTIFICATION_TOKEN + " missing or invalid");
				return;
			}

			final String login = request.getParameter(LunchGuiConstants.PARAEMTER_NOTIFICATION_LOGIN);
			if (login == null || login.isEmpty()) {
				printError(response, "parameter " + LunchGuiConstants.PARAEMTER_NOTIFICATION_LOGIN + " missing");
				return;
			}

			final JSONObject jsonObject = new JSONObjectSimple();
			boolean result;
			try {
				logger.debug("deactivate notification for user: " + login);
				lunchService.deactivateNotification(new UserIdentifier(login));
				result = true;
			} catch (final ValidationException e) {
				final List<String> messages = new ArrayList<String>();
				for (final ValidationError error : e.getErrors()) {
					messages.add(error.getMessage());
				}
				jsonObject.put("messages", messages);
				result = false;
			}

			jsonObject.put("result", result ? "success" : "failure");
			printJson(response, jsonObject);

		} catch (final LunchServiceException e) {
			logger.warn(e.getClass().getName(), e);
			printException(response, e);
		}
	}

	@Override
	public boolean isLoginRequired() {
		return false;
	}

	@Override
	public boolean isAdminRequired() {
		return false;
	}
}
