package de.benjaminborbe.task.gui.servlet;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.navigation.api.NavigationWidget;
import de.benjaminborbe.task.api.Task;
import de.benjaminborbe.task.api.TaskServiceException;
import de.benjaminborbe.task.gui.TaskGuiConstants;
import de.benjaminborbe.task.gui.util.TaskGuiLinkFactory;
import de.benjaminborbe.task.gui.util.TaskGuiUtil;
import de.benjaminborbe.task.gui.util.TaskGuiWidgetFactory;
import de.benjaminborbe.task.gui.widget.TaskGuiSwitchWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.website.servlet.RedirectException;
import de.benjaminborbe.website.servlet.RedirectUtil;
import de.benjaminborbe.website.util.ExceptionWidget;
import de.benjaminborbe.website.util.H1Widget;
import de.benjaminborbe.website.util.ListWidget;

@Singleton
public class TaskGuiTasksUncompletedServlet extends TaskGuiWebsiteHtmlServlet {

	private static final long serialVersionUID = 1328676176772634649L;

	private static final String TITLE = "Tasks";

	private final Logger logger;

	private final AuthenticationService authenticationService;

	private final TaskGuiLinkFactory taskGuiLinkFactory;

	private final TaskGuiWidgetFactory taskGuiWidgetFactory;

	private final TaskGuiUtil taskGuiUtil;

	private final TaskGuiSwitchWidget taskGuiSwitchWidget;

	@Inject
	public TaskGuiTasksUncompletedServlet(
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
			final TaskGuiLinkFactory taskGuiLinkFactory,
			final TaskGuiWidgetFactory taskGuiWidgetFactory,
			final TaskGuiUtil taskGuiUtil,
			final TaskGuiSwitchWidget taskGuiSwitchWidget) {
		super(logger, calendarUtil, timeZoneUtil, parseUtil, navigationWidget, authenticationService, authorizationService, httpContextProvider, urlUtil);
		this.logger = logger;
		this.authenticationService = authenticationService;
		this.taskGuiLinkFactory = taskGuiLinkFactory;
		this.taskGuiWidgetFactory = taskGuiWidgetFactory;
		this.taskGuiUtil = taskGuiUtil;
		this.taskGuiSwitchWidget = taskGuiSwitchWidget;
	}

	@Override
	protected String getTitle() {
		return TITLE;
	}

	@Override
	protected Widget createTaskContentWidget(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException,
			PermissionDeniedException, RedirectException, LoginRequiredException {
		try {
			logger.trace("printContent");
			final ListWidget widgets = new ListWidget();
			widgets.add(new H1Widget(getTitle()));

			widgets.add(taskGuiSwitchWidget);

			final SessionIdentifier sessionIdentifier = authenticationService.createSessionIdentifier(request);
			final String[] taskContextIds = request.getParameterValues(TaskGuiConstants.PARAMETER_SELECTED_TASKCONTEXT_ID);

			final List<Task> tasks = taskGuiUtil.getTasksNotCompleted(sessionIdentifier, taskContextIds);
			final TimeZone timeZone = authenticationService.getTimeZone(sessionIdentifier);
			logger.trace("found " + tasks.size() + " tasks");
			widgets.add(taskGuiWidgetFactory.taskListWithChilds(sessionIdentifier, tasks, null, request, timeZone));

			final ListWidget links = new ListWidget();
			links.add(taskGuiLinkFactory.tasksNext(request));
			links.add(" ");
			links.add(taskGuiLinkFactory.taskCreate(request));
			links.add(" ");
			links.add(taskGuiLinkFactory.tasksCompleted(request));
			links.add(" ");
			links.add(taskGuiLinkFactory.taskContextList(request));
			widgets.add(links);
			return widgets;
		}
		catch (final AuthenticationServiceException e) {
			logger.trace(e.getClass().getName(), e);
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
		catch (final TaskServiceException e) {
			logger.trace(e.getClass().getName(), e);
			final ExceptionWidget widget = new ExceptionWidget(e);
			return widget;
		}
	}

}
