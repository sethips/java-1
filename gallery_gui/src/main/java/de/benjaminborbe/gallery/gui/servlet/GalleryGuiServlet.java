package de.benjaminborbe.gallery.gui.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.gallery.api.Gallery;
import de.benjaminborbe.gallery.api.GalleryIdentifier;
import de.benjaminborbe.gallery.api.GalleryService;
import de.benjaminborbe.gallery.api.GalleryServiceException;
import de.benjaminborbe.gallery.gui.GalleryGuiConstants;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.map.MapChain;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.link.LinkRelativWidget;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.util.UlWidget;

@Singleton
public class GalleryGuiServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Gallery";

	private final GalleryService galleryService;

	private final Logger logger;

	private final UrlUtil urlUtil;

	@Inject
	public GalleryGuiServlet(
			final Logger logger,
			final CalendarUtil calendarUtil,
			final TimeZoneUtil timeZoneUtil,
			final ParseUtil parseUtil,
			final AuthenticationService authenticationService,
			final NavigationWidget navigationWidget,
			final Provider<HttpContext> httpContextProvider,
			final RedirectUtil redirectUtil,
			final UrlUtil urlUtil,
			final GalleryService galleryService) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, httpContextProvider, urlUtil);
		this.galleryService = galleryService;
		this.logger = logger;
		this.urlUtil = urlUtil;
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
			widgets.add(new H1Widget(TITLE));
			final UlWidget ul = new UlWidget();
			for (final GalleryIdentifier galleryIdentifier : galleryService.getGalleries()) {
				final Gallery gallery = galleryService.getGallery(galleryIdentifier);
				ul.add(new LinkRelativWidget(urlUtil, request, "/" + GalleryGuiConstants.NAME + GalleryGuiConstants.URL_IMAGE_LIST, new MapChain<String, String>().add(
						GalleryGuiConstants.PARAMETER_GALLERY_ID, String.valueOf(galleryIdentifier)), gallery.getName()));
			}
			widgets.add(ul);
			widgets.add(new LinkRelativWidget(request, "/" + GalleryGuiConstants.NAME + GalleryGuiConstants.URL_CREATE, "create gallery"));
			return widgets;
		}
		catch (final GalleryServiceException e) {
			logger.warn(e.getClass().getSimpleName(), e);
			return new ExceptionWidget(e);
		}
	}
}
