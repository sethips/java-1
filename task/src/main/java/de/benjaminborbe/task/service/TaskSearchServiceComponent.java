package de.benjaminborbe.task.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.task.api.Task;
import de.benjaminborbe.task.api.TaskService;
import de.benjaminborbe.task.api.TaskServiceException;
import de.benjaminborbe.search.api.SearchResult;
import de.benjaminborbe.search.api.SearchResultImpl;
import de.benjaminborbe.search.api.SearchServiceComponent;

@Singleton
public class TaskSearchServiceComponent implements SearchServiceComponent {

	private static final String SEARCH_TYPE = "Task";

	private final Logger logger;

	private final TaskService taskService;

	@Inject
	public TaskSearchServiceComponent(final Logger logger, final TaskService taskService) {
		this.logger = logger;
		this.taskService = taskService;
	}

	@Override
	public List<SearchResult> search(final SessionIdentifier sessionIdentifier, final String query, final String[] words, final int maxResults) {
		logger.trace("search: queryString: " + StringUtils.join(words, ",") + " maxResults: " + maxResults);
		final List<SearchResult> results = new ArrayList<SearchResult>();
		try {
			final List<Task> tasks = taskService.searchTasks(sessionIdentifier, words);
			final int max = Math.min(maxResults, tasks.size());
			for (int i = 0; i < max; ++i) {
				try {
					results.add(mapTask(tasks.get(i)));
				}
				catch (final MalformedURLException e) {
					logger.error("MalformedURLException", e);
				}
			}
			logger.trace("search found " + results.size() + " tasks");
		}
		catch (final TaskServiceException e) {
			logger.trace(e.getClass().getName(), e);
		}
		catch (final LoginRequiredException e) {
			logger.trace(e.getClass().getName(), e);
		}
		return results;
	}

	protected SearchResult mapTask(final Task task) throws MalformedURLException {
		return new SearchResultImpl(SEARCH_TYPE, task.getName(), buildUrl(task), task.getDescription());
	}

	private String buildUrl(final Task task) {
		return "/task/task/view?task_id=" + task.getId();
	}

	@Override
	public String getName() {
		return SEARCH_TYPE;
	}
}