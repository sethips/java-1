package de.benjaminborbe.poker.gui.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.cache.api.CacheService;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.poker.api.PokerService;
import de.benjaminborbe.poker.api.PokerServiceException;
import de.benjaminborbe.poker.gui.PokerGuiConstants;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.form.FormInputSubmitWidget;
import de.benjaminborbe.website.form.FormInputTextWidget;
import de.benjaminborbe.website.form.FormWidget;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.widget.ValidationExceptionWidget;

@Singleton
public class PokerGuiGameCreateServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Poker - Game - Create";

	private final PokerService pokerService;

	@Inject
	public PokerGuiGameCreateServlet(
			final Logger logger,
			final CalendarUtil calendarUtil,
			final TimeZoneUtil timeZoneUtil,
			final ParseUtil parseUtil,
			final AuthenticationService authenticationService,
			final NavigationWidget navigationWidget,
			final Provider<HttpContext> httpContextProvider,
			final RedirectUtil redirectUtil,
			final UrlUtil urlUtil,
			final AuthorizationService authorizationService,
			final CacheService cacheService,
			final PokerService pokerService) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, authorizationService, httpContextProvider, urlUtil, cacheService);
		this.pokerService = pokerService;
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected Widget createContentWidget(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException,
			PermissionDeniedException, RedirectException, LoginRequiredException {
		try {
			final ListWidget widgets = new ListWidget();
			widgets.add(new H1Widget(getTitle()));

			final String name = request.getParameter(PokerGuiConstants.PARAMETER_GAME_NAME);
			if (name != null) {
				try {
					pokerService.createGame(name, PokerGuiConstants.DEFAULT_BLIND);
					widgets.add("game created");
				}
				catch (final ValidationException e) {
					widgets.add("create game failed!");
					widgets.add(new ValidationExceptionWidget(e));
				}
			}

			final FormWidget form = new FormWidget();
			form.addFormInputWidget(new FormInputTextWidget(PokerGuiConstants.PARAMETER_GAME_NAME).addLabel("Name:"));
			form.addFormInputWidget(new FormInputSubmitWidget("create"));
			widgets.add(form);

			return widgets;
		}
		catch (final PokerServiceException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}
}