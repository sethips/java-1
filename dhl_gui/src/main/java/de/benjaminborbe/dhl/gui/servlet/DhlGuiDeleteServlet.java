package de.benjaminborbe.dhl.gui.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.dhl.api.DhlIdentifier;
import de.benjaminborbe.dhl.api.DhlService;
import de.benjaminborbe.dhl.api.DhlServiceException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseException;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;

@Singleton
public class DhlGuiDeleteServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 4956434804365230995L;

	private static final String TITLE = "Dhl - Remove Tracking";

	private static final String PARAMETER_ID = "id";

	private static final String PARAMETER_ZIP = "zip";

	private final DhlService dhlService;

	@Inject
	public DhlGuiDeleteServlet(
			final Logger logger,
			final CalendarUtil calendarUtil,
			final TimeZoneUtil timeZoneUtil,
			final ParseUtil parseUtil,
			final NavigationWidget navigationWidget,
			final AuthenticationService authenticationService,
			final Provider<HttpContext> httpContextProvider,
			final DhlService dhlService,
			final RedirectUtil redirectUtil,
			final UrlUtil urlUtil) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, httpContextProvider, redirectUtil, urlUtil);
		this.dhlService = dhlService;
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected Widget createContentWidget(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException,
			PermissionDeniedException, RedirectException {
		try {
			logger.trace("printContent");
			final ListWidget widgets = new ListWidget();
			try {
				final long id = parseUtil.parseLong(request.getParameter(PARAMETER_ID));
				final long zip = parseUtil.parseLong(request.getParameter(PARAMETER_ZIP));
				final SessionIdentifier sessionIdentifier = authenticationService.createSessionIdentifier(request);
				final DhlIdentifier dhlIdentifier = dhlService.createDhlIdentifier(sessionIdentifier, id, zip);
				if (dhlService.removeTracking(sessionIdentifier, dhlIdentifier)) {
					throw new RedirectException("/dhl/list");
				}
			}
			catch (final ParseException e) {
			}
			widgets.add(new H1Widget(getTitle()));
			widgets.add("delete tracking failed");
			return widgets;
		}
		catch (final DhlServiceException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
		catch (final AuthenticationServiceException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}
}