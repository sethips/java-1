package de.benjaminborbe.task.gui.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.task.api.Task;
import de.benjaminborbe.task.api.TaskFocus;
import de.benjaminborbe.task.api.TaskIdentifier;
import de.benjaminborbe.task.api.TaskServiceException;
import de.benjaminborbe.task.gui.TaskGuiConstants;
import de.benjaminborbe.task.gui.widget.TooltipWidget;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.html.Target;
import de.benjaminborbe.website.link.LinkWidget;
import de.benjaminborbe.website.util.DivWidget;
import de.benjaminborbe.website.util.ListWidget;
import de.benjaminborbe.website.util.SpanWidget;
import de.benjaminborbe.website.util.StringWidget;
import de.benjaminborbe.website.util.UlWidget;
import de.benjaminborbe.website.widget.ImageWidget;

@Singleton
public class TaskGuiWidgetFactory {

	private final TaskGuiLinkFactory taskGuiLinkFactory;

	private final TaskGuiUtil taskGuiUtil;

	private final CalendarUtil calendarUtil;

	private final Logger logger;

	@Inject
	public TaskGuiWidgetFactory(final Logger logger, final TaskGuiLinkFactory taskGuiLinkFactory, final TaskGuiUtil taskGuiUtil, final CalendarUtil calendarUtil) {
		this.logger = logger;
		this.taskGuiLinkFactory = taskGuiLinkFactory;
		this.taskGuiUtil = taskGuiUtil;
		this.calendarUtil = calendarUtil;
	}

	public Widget taskListWithoutParents(final SessionIdentifier sessionIdentifier, final List<Task> tasks, final Collection<Task> allTasks, final HttpServletRequest request,
			final TimeZone timeZone) throws MalformedURLException, UnsupportedEncodingException, TaskServiceException, LoginRequiredException, PermissionDeniedException {
		final List<Task> groupedTasks = groupByDueState(tasks, timeZone);

		final UlWidget ul = new UlWidget();
		for (int i = 0; i < groupedTasks.size(); ++i) {
			final Task task = groupedTasks.get(i);
			final ListWidget widgets = new ListWidget();
			final Widget div = buildTaskListRow(sessionIdentifier, request, groupedTasks, i, task, allTasks, timeZone);
			widgets.add(div);
			ul.add(widgets);
		}
		ul.addClass("taskList");
		return ul;
	}

	private List<Task> groupByDueState(final List<Task> tasks, final TimeZone timeZone) {
		final TaskDueTodayPredicate taskDueTodayPredicate = new TaskDueTodayPredicate(logger, calendarUtil, timeZone);
		final TaskDueExpiredPredicate taskDueExpiredPredicate = new TaskDueExpiredPredicate(logger, calendarUtil, timeZone);
		final TaskDueNotExpiredPredicate taskDueNotExpiredPredicate = new TaskDueNotExpiredPredicate(logger, calendarUtil, timeZone);

		final List<Task> result = new ArrayList<Task>();
		result.addAll(Collections2.filter(tasks, taskDueExpiredPredicate));
		result.addAll(Collections2.filter(tasks, taskDueTodayPredicate));
		result.addAll(Collections2.filter(tasks, taskDueNotExpiredPredicate));
		return result;
	}

	public Widget taskListWithChilds(final SessionIdentifier sessionIdentifier, final List<Task> allTasks, final TaskIdentifier parentId, final HttpServletRequest request,
			final TimeZone timeZone) throws MalformedURLException, UnsupportedEncodingException, TaskServiceException, LoginRequiredException, PermissionDeniedException {
		final List<Task> tasks = taskGuiUtil.getChildTasks(allTasks, parentId);
		if (tasks.isEmpty()) {
			return null;
		}
		final UlWidget ul = new UlWidget();
		for (int i = 0; i < tasks.size(); ++i) {
			final Task task = tasks.get(i);
			final ListWidget widgets = new ListWidget();
			{
				widgets.add(buildTaskName(sessionIdentifier, request, task, allTasks));
			}
			{
				widgets.add(new DivWidget(taskListWithChilds(sessionIdentifier, allTasks, task.getId(), request, timeZone)));
			}
			ul.add(widgets);
		}
		return ul;
	}

	private Widget buildTaskListRow(final SessionIdentifier sessionIdentifier, final HttpServletRequest request, final List<Task> tasks, final int position, final Task task,
			final Collection<Task> allTasks, final TimeZone timeZone) throws MalformedURLException, UnsupportedEncodingException, TaskServiceException, LoginRequiredException,
			PermissionDeniedException {

		final ListWidget row = new ListWidget();
		if (position > 0) {
			row.add(taskGuiLinkFactory.taskPrioFirst(request, buildImage(request, "first"), task.getId()));
			row.add(" ");
			row.add(taskGuiLinkFactory.taskPrioSwap(request, buildImage(request, "up"), task.getId(), tasks.get(position - 1).getId()));
			row.add(" ");
		}
		else {
			row.add(buildImage(request, "empty"));
			row.add(" ");
			row.add(buildImage(request, "empty"));
			row.add(" ");
		}
		if (position < tasks.size() - 1) {
			row.add(taskGuiLinkFactory.taskPrioSwap(request, buildImage(request, "down"), task.getId(), tasks.get(position + 1).getId()));
			row.add(" ");
			row.add(taskGuiLinkFactory.taskPrioLast(request, buildImage(request, "last"), task.getId()));
			row.add(" ");
		}
		else {
			row.add(buildImage(request, "empty"));
			row.add(" ");
			row.add(buildImage(request, "empty"));
			row.add(" ");
		}
		row.add(taskGuiLinkFactory.taskUpdate(request, buildImage(request, "update"), task));
		row.add(" ");
		row.add(taskGuiLinkFactory.taskView(request, buildImage(request, "view"), task));
		row.add(" ");
		row.add(taskGuiLinkFactory.taskCreateSubTask(request, buildImage(request, "subtask"), task.getId()));
		row.add(" ");
		row.add(taskGuiLinkFactory.taskDelete(request, buildImage(request, "delete"), task));
		row.add(" ");
		row.add(taskGuiLinkFactory.taskComplete(request, buildImage(request, "complete"), task));
		row.add(" ");

		final ListWidget rowText = new ListWidget();

		rowText.add(buildTaskName(sessionIdentifier, request, task, allTasks));
		rowText.add(" ");

		if (task.getUrl() != null && task.getUrl().length() > 0) {
			rowText.add(new LinkWidget(task.getUrl(), "goto ").addTarget(Target.BLANK));
		}

		if (task.getRepeatDue() != null && task.getRepeatDue() > 0 || task.getRepeatStart() != null && task.getRepeatStart() > 0) {
			final List<String> parts = new ArrayList<String>();
			if (task.getRepeatDue() != null && task.getRepeatDue() > 0) {
				parts.add("due: " + task.getRepeatDue() + " day");
			}
			if (task.getRepeatStart() != null && task.getRepeatStart() > 0) {
				parts.add("start: " + task.getRepeatStart() + " day");
			}
			rowText.add(new TooltipWidget("(repeat) ").addTooltip(StringUtils.join(parts, " ")));
		}
		row.add(new SpanWidget(rowText).addClass("taskText"));

		final ListWidget options = new ListWidget();

		options.add(taskGuiLinkFactory.taskStartLater(request, task.getId()));
		options.add(" ");
		options.add(taskGuiLinkFactory.taskStartTomorrow(request, task.getId()));
		options.add(" ");
		options.add(buildImage(request, "empty"));
		options.add(" ");
		options.add(taskGuiLinkFactory.taskUpdateFocus(request, task.getId(), TaskFocus.INBOX, "inbox"));
		options.add(" ");
		options.add(taskGuiLinkFactory.taskUpdateFocus(request, task.getId(), TaskFocus.TODAY, "today"));
		options.add(" ");
		options.add(taskGuiLinkFactory.taskUpdateFocus(request, task.getId(), TaskFocus.NEXT, "next"));
		options.add(" ");
		options.add(taskGuiLinkFactory.taskUpdateFocus(request, task.getId(), TaskFocus.SOMEDAY, "someday"));

		row.add(new SpanWidget(options).addAttribute("class", "taskOptions"));
		final DivWidget div = new DivWidget(row).addClass("taskEntry");
		if (task.getDue() != null) {
			final TaskDueTodayPredicate taskDueTodayPredicate = new TaskDueTodayPredicate(logger, calendarUtil, timeZone);
			final TaskDueExpiredPredicate taskDueExpiredPredicate = new TaskDueExpiredPredicate(logger, calendarUtil, timeZone);
			if (taskDueTodayPredicate.apply(task)) {
				div.addClass("dueToday");
			}
			else if (taskDueExpiredPredicate.apply(task)) {
				div.addClass("dueExpired");
			}
		}
		if (task.getStart() != null) {
			final TaskStartReadyPredicate taskStartReadyPredicate = new TaskStartReadyPredicate(logger, calendarUtil, timeZone);
			if (!taskStartReadyPredicate.apply(task)) {
				div.addClass("startNotReached");
			}
		}
		return div;
	}

	private Widget buildTaskName(final SessionIdentifier sessionIdentifier, final HttpServletRequest request, final Task task, final Collection<Task> allTasks)
			throws TaskServiceException, LoginRequiredException, PermissionDeniedException, MalformedURLException, UnsupportedEncodingException {
		final String taskName = taskGuiUtil.buildCompleteName(sessionIdentifier, allTasks, task, TaskGuiConstants.PARENT_NAME_LENGTH);
		return new SpanWidget(taskGuiLinkFactory.taskView(request, new StringWidget(taskName), task)).addAttribute("class", "taskTitle");
	}

	public Widget buildImage(final HttpServletRequest request, final String name) {
		return new ImageWidget(request.getContextPath() + "/" + TaskGuiConstants.NAME + TaskGuiConstants.URL_IMAGES + "/" + name + "-icon.png", 20, 20).addAlt(name).addClass("icon");
	}
}
