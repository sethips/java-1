package de.benjaminborbe.authentication.gui.servlet;

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
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.form.FormInputHiddenWidget;
import de.benjaminborbe.website.form.FormInputPasswordWidget;
import de.benjaminborbe.website.form.FormInputSubmitWidget;
import de.benjaminborbe.website.form.FormInputTextWidget;
import de.benjaminborbe.website.form.FormMethod;
import de.benjaminborbe.website.form.FormWidget;
import de.benjaminborbe.website.link.LinkRelativWidget;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;

@Singleton
public class AuthenticationGuiLoginServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Authentication - Login";

	private static final String PARAMETER_PASSWORD = "password";

	private static final String PARAMETER_USERNAME = "username";

	private static final String PARAMETER_REFERER = "referer";

	@Inject
	public AuthenticationGuiLoginServlet(
			final Logger logger,
			final CalendarUtil calendarUtil,
			final TimeZoneUtil timeZoneUtil,
			final ParseUtil parseUtil,
			final NavigationWidget navigationWidget,
			final Provider<HttpContext> httpContextProvider,
			final AuthenticationService authenticationService,
			final RedirectUtil redirectUtil,
			final UrlUtil urlUtil) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, httpContextProvider, redirectUtil, urlUtil);
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
			widgets.add(new H1Widget(getTitle()));
			final String username = request.getParameter(PARAMETER_USERNAME);
			final String password = request.getParameter(PARAMETER_PASSWORD);
			if (username != null && password != null) {
				final SessionIdentifier sessionIdentifier = authenticationService.createSessionIdentifier(request);
				final UserIdentifier userIdentifier = authenticationService.createUserIdentifier(username);
				if (authenticationService.login(sessionIdentifier, userIdentifier, password)) {
					final String referer = request.getParameter(PARAMETER_REFERER) != null ? request.getParameter(PARAMETER_REFERER) : request.getContextPath() + "/dashboard";
					// necessary ?
					request.getSession().setAttribute("login", "true");
					logger.debug("send redirect to: " + referer);
					throw new RedirectException(referer);
				}
				else {
					widgets.add("login => failed");
					logger.info("login failed for user " + username);
				}
			}
			final String action = request.getContextPath() + "/authentication/login";
			final FormWidget form = new FormWidget(action).addMethod(FormMethod.POST);
			form.addFormInputWidget(new FormInputTextWidget(PARAMETER_USERNAME).addLabel("Username").addPlaceholder("Username ..."));
			form.addFormInputWidget(new FormInputPasswordWidget(PARAMETER_PASSWORD).addLabel("Password").addPlaceholder("Password ..."));
			form.addFormInputWidget(new FormInputHiddenWidget(PARAMETER_REFERER));
			form.addFormInputWidget(new FormInputSubmitWidget("login"));
			widgets.add(form);
			widgets.add(new LinkRelativWidget(request, "/authentication/register", "no user? register here!"));
			return widgets;
		}
		catch (final AuthenticationServiceException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}

	@Override
	protected boolean isLoginRequired() {
		return false;
	}

}
