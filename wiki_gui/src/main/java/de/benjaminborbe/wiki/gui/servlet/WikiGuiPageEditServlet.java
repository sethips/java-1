package de.benjaminborbe.wiki.gui.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.form.FormInputHiddenWidget;
import de.benjaminborbe.website.form.FormInputSubmitWidget;
import de.benjaminborbe.website.form.FormInputTextWidget;
import de.benjaminborbe.website.form.FormInputTextareaWidget;
import de.benjaminborbe.website.form.FormWidget;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.util.UlWidget;
import de.benjaminborbe.wiki.api.WikiPage;
import de.benjaminborbe.wiki.api.WikiPageIdentifier;
import de.benjaminborbe.wiki.api.WikiPageNotFoundException;
import de.benjaminborbe.wiki.api.WikiPageUpdateException;
import de.benjaminborbe.wiki.api.WikiService;
import de.benjaminborbe.wiki.api.WikiServiceException;
import de.benjaminborbe.wiki.gui.WikiGuiConstants;

@Singleton
public class WikiGuiPageEditServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Wiki - Edit";

	private final WikiService wikiService;

	private final Logger logger;

	@Inject
	public WikiGuiPageEditServlet(
			final Logger logger,
			final CalendarUtil calendarUtil,
			final TimeZoneUtil timeZoneUtil,
			final ParseUtil parseUtil,
			final AuthenticationService authenticationService,
			final NavigationWidget navigationWidget,
			final Provider<HttpContext> httpContextProvider,
			final RedirectUtil redirectUtil,
			final UrlUtil urlUtil,
			final WikiService wikiService) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, httpContextProvider, urlUtil);
		this.logger = logger;
		this.wikiService = wikiService;
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected Widget createContentWidget(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException,
			PermissionDeniedException, RedirectException {

		try {
			logger.debug("render " + getClass().getSimpleName());
			final ListWidget widgets = new ListWidget();
			widgets.add(new H1Widget(getTitle()));
			final String id = request.getParameter(WikiGuiConstants.PARAMETER_PAGE_ID);
			final String title = request.getParameter(WikiGuiConstants.PARAMETER_PAGE_TITLE);
			final String content = request.getParameter(WikiGuiConstants.PARAMETER_PAGE_CONTENT);
			final WikiPageIdentifier wikiPageIdentifier = wikiService.createPageIdentifier(id);
			if (id != null && title != null && content != null) {
				try {
					wikiService.updatePage(wikiPageIdentifier, title, content);
					throw new RedirectException(request.getContextPath() + "/wiki/page/show?" + WikiGuiConstants.PARAMETER_PAGE_ID + "=" + wikiPageIdentifier);
				}
				catch (final WikiPageUpdateException e) {
					widgets.add("update page failed!");
					final UlWidget ul = new UlWidget();
					for (final ValidationError validationError : e.getErrors()) {
						ul.add(validationError.getMessage());
					}
					widgets.add(ul);
				}
			}

			final WikiPage page = wikiService.getPage(wikiPageIdentifier);
			final FormWidget form = new FormWidget();
			form.addFormInputWidget(new FormInputHiddenWidget(WikiGuiConstants.PARAMETER_PAGE_ID).addValue(String.valueOf(page.getId())));
			form.addFormInputWidget(new FormInputTextWidget(WikiGuiConstants.PARAMETER_PAGE_TITLE).addLabel("Title").addPlaceholder("Title ...").addDefaultValue(page.getTitle()));
			form.addFormInputWidget(new FormInputTextareaWidget(WikiGuiConstants.PARAMETER_PAGE_CONTENT).addLabel("Content").addPlaceholder("Content ...")
					.addDefaultValue(page.getContent()));
			form.addFormInputWidget(new FormInputSubmitWidget("update"));
			widgets.add(form);

			return widgets;
		}
		catch (final WikiServiceException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
		catch (final WikiPageNotFoundException e) {
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}
}