package de.benjaminborbe.message.gui.servlet;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.cache.api.CacheService;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.JavascriptResource;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.message.api.Message;
import de.benjaminborbe.message.api.MessageService;
import de.benjaminborbe.message.api.MessageServiceException;
import de.benjaminborbe.message.gui.util.MessageGuiLinkFactory;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.tools.util.StringUtil;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.WebsiteHtmlServlet;
import de.benjaminborbe.website.table.TableHeadWidget;
import de.benjaminborbe.website.table.TableRowWidget;
import de.benjaminborbe.website.table.TableWidget;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.H2Widget;
import de.benjaminborbe.website.util.JavascriptResourceImpl;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.util.UlWidget;
import de.benjaminborbe.website.widget.BrWidget;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageGuiMessageListServlet extends WebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Messages";

	private final MessageService messageService;

	private final AuthenticationService authenticationService;

	private final Logger logger;

	private final CalendarUtil calendarUtil;

	private final MessageGuiLinkFactory messageGuiLinkFactory;

	private final StringUtil stringUtil;

	@Inject
	public MessageGuiMessageListServlet(
		final Logger logger,
		final CalendarUtil calendarUtil,
		final TimeZoneUtil timeZoneUtil,
		final ParseUtil parseUtil,
		final AuthenticationService authenticationService,
		final NavigationWidget navigationWidget,
		final Provider<HttpContext> httpContextProvider,
		final UrlUtil urlUtil,
		final AuthorizationService authorizationService,
		final CacheService cacheService,
		final MessageService messageService,
		final MessageGuiLinkFactory messageGuiLinkFactory,
		final StringUtil stringUtil
	) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, authorizationService, httpContextProvider, urlUtil, cacheService);
		this.messageService = messageService;
		this.authenticationService = authenticationService;
		this.logger = logger;
		this.calendarUtil = calendarUtil;
		this.messageGuiLinkFactory = messageGuiLinkFactory;
		this.stringUtil = stringUtil;
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
			final SessionIdentifier sessionIdentifier = authenticationService.createSessionIdentifier(request);
			final List<Message> messages = Lists.newArrayList(messageService.getMessages(sessionIdentifier));

			widgets.add("LockName: ");
			widgets.add(messageService.getLockName(sessionIdentifier));
			widgets.add(new BrWidget());

			if (messages.isEmpty()) {
				widgets.add("no message found");
			} else {

				final TableWidget table = new TableWidget();
				table.addClass("sortable");
				final TableHeadWidget head = new TableHeadWidget();
				head.addCell("Type").addCell("Id").addCell("LockName").addCell("LockTime").addCell("Created").addCell("RetryCounter").addCell("MaxRetryCounter").addCell("");
				table.setHead(head);
				for (final Message message : messages) {
					final TableRowWidget row = new TableRowWidget();
					row.addCell(asString(message.getType()));
					row.addCell(messageGuiLinkFactory.messageView(request, message.getId(), stringUtil.shortenDots(asString(message.getId()), 150)));
					row.addCell(asString(message.getLockName()));
					row.addCell(asString(calendarUtil.toDateTimeString(message.getLockTime())));
					row.addCell(asString(calendarUtil.toDateTimeString(message.getCreated())));
					row.addCell(asString(message.getRetryCounter()));
					row.addCell(asString(message.getMaxRetryCounter()));
					row.addCell(messageGuiLinkFactory.deleteMessage(request, message.getId()));
					table.addRow(row);
				}
				widgets.add(table);

				widgets.add(new H2Widget("Options"));
				final UlWidget ul = new UlWidget();
				ul.add(messageGuiLinkFactory.deleteByType(request));
				ul.add(messageGuiLinkFactory.unlockExpiredMessage(request));
				ul.add(messageGuiLinkFactory.exchangeMessages(request));
				widgets.add(ul);

			}
			return widgets;
		} catch (final AuthenticationServiceException e) {
			logger.debug(e.getClass().getName(), e);
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		} catch (final MessageServiceException e) {
			logger.debug(e.getClass().getName(), e);
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}

	private String asString(final Object object) {
		return object != null ? String.valueOf(object) : "";
	}

	@Override
	protected List<JavascriptResource> getJavascriptResources(final HttpServletRequest request, final HttpServletResponse response) {
		final String contextPath = request.getContextPath();
		final List<JavascriptResource> result = new ArrayList<JavascriptResource>();
		result.add(new JavascriptResourceImpl(contextPath + "/js/sorttable.js"));
		return result;
	}
}
