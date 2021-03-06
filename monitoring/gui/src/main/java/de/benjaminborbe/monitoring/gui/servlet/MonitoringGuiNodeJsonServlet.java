package de.benjaminborbe.monitoring.gui.servlet;

import com.google.inject.Provider;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.monitoring.api.MonitoringNode;
import de.benjaminborbe.monitoring.api.MonitoringNodeIdentifier;
import de.benjaminborbe.monitoring.api.MonitoringService;
import de.benjaminborbe.monitoring.api.MonitoringServiceException;
import de.benjaminborbe.monitoring.gui.MonitoringGuiConstants;
import de.benjaminborbe.monitoring.tools.MapperJsonObjectMonitoringNode;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.json.JSONObject;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.website.servlet.WebsiteJsonServlet;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class MonitoringGuiNodeJsonServlet extends WebsiteJsonServlet {

	private static final long serialVersionUID = 1844470197045483190L;

	private final Logger logger;

	private final MonitoringService monitoringService;

	private final MapperJsonObjectMonitoringNode mapperJsonObjectMonitoringNode;

	@Inject
	public MonitoringGuiNodeJsonServlet(
		final Logger logger,
		final UrlUtil urlUtil,
		final AuthenticationService authenticationService,
		final AuthorizationService authorizationService,
		final CalendarUtil calendarUtil,
		final TimeZoneUtil timeZoneUtil,
		final Provider<HttpContext> httpContextProvider,
		final MonitoringService monitoringService,
		final MapperJsonObjectMonitoringNode mapperJsonObjectMonitoringNode
	) {
		super(logger, urlUtil, authenticationService, authorizationService, calendarUtil, timeZoneUtil, httpContextProvider);
		this.logger = logger;
		this.monitoringService = monitoringService;
		this.mapperJsonObjectMonitoringNode = mapperJsonObjectMonitoringNode;
	}

	@Override
	protected void doService(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final HttpContext context
	) throws ServletException, IOException,
		PermissionDeniedException, LoginRequiredException {
		try {
			logger.debug("doService");
			final String token = request.getParameter(MonitoringGuiConstants.PARAMETER_AUTH_TOKEN);
			final MonitoringNodeIdentifier monitoringNodeIdentifier = monitoringService.createNodeIdentifier(request.getParameter(MonitoringGuiConstants.PARAMETER_NODE_ID));
			final MonitoringNode node = monitoringService.getNode(token, monitoringNodeIdentifier);
			final JSONObject object = mapperJsonObjectMonitoringNode.map(node);
			printJson(response, object);
		} catch (final MonitoringServiceException e) {
			printException(response, e);
		}
	}

	@Override
	protected void doCheckPermission(final HttpServletRequest request) throws ServletException, IOException,
		PermissionDeniedException {
		final String token = request.getParameter(MonitoringGuiConstants.PARAMETER_AUTH_TOKEN);
		logger.debug("doCheckPermission");
		try {
			monitoringService.expectAuthToken(token);
		} catch (final MonitoringServiceException e) {
			throw new PermissionDeniedException(e);
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
