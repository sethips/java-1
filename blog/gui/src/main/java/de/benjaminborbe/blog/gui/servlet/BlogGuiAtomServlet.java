package de.benjaminborbe.blog.gui.servlet;

import com.google.inject.Provider;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.blog.gui.BlogGuiConstants;
import de.benjaminborbe.blog.gui.atom.Entry;
import de.benjaminborbe.blog.gui.atom.EntryBean;
import de.benjaminborbe.blog.gui.atom.Feed;
import de.benjaminborbe.blog.gui.atom.FeedBean;
import de.benjaminborbe.blog.gui.widget.BlogGuiAtomWidget;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.website.servlet.WebsiteWidgetServlet;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BlogGuiAtomServlet extends WebsiteWidgetServlet {

	private static final long serialVersionUID = -9150646730186060728L;

	private final UrlUtil urlUtil;

	@Inject
	public BlogGuiAtomServlet(
		final Logger logger,
		final CalendarUtil calendarUtil,
		final AuthenticationService authenticationService,
		final TimeZoneUtil timeZoneUtil,
		final Provider<HttpContext> httpContextProvider,
		final UrlUtil urlUtil,
		final AuthorizationService authorizationService
	) {
		super(logger, urlUtil, calendarUtil, timeZoneUtil, httpContextProvider, authenticationService, authorizationService);
		this.urlUtil = urlUtil;
	}

	@Override
	public Widget createWidget(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException {
		final Feed feed = createFeed(request);
		return new BlogGuiAtomWidget(feed);
	}

	private Feed createFeed(final HttpServletRequest request) {
		final AuthorBean author = new AuthorBean();
		author.setName("Benjamin Borbe");

		final FeedBean feed = new FeedBean();
		feed.setTitle(BlogGuiConstants.ATOM_TITLE);
		feed.setSubtitle("");
		feed.setLink(request.getRequestURL().toString());
		feed.setId(request.getRequestURL().toString());
		feed.setUpdated("2012-09-16T11:50:09+02:00");
		feed.setAuthor(author);
		feed.setEntries(createEntries(request));
		return feed;
	}

	private List<Entry> createEntries(final HttpServletRequest request) {
		final List<Entry> entries = new ArrayList<Entry>();
		entries.add(createEntry(request));
		return entries;
	}

	private Entry createEntry(final HttpServletRequest request) {
		final EntryBean entry = new EntryBean();
		entry.setTitle("Title");
		entry.setId("123");
		entry.setLink(urlUtil.buildBaseUrl(request) + "/" + BlogGuiConstants.NAME + "/123");
		entry.setPublished("2012-09-16T11:50:00+02:00");
		entry.setUpdated("2012-09-16T11:50:09+02:00");
		entry.setSummary("summary");
		entry.setContent("<h1>Content</h1>");
		return entry;
	}

}
