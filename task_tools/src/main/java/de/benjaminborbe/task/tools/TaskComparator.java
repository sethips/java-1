package de.benjaminborbe.task.tools;

import com.google.inject.Inject;

import de.benjaminborbe.task.api.Task;
import de.benjaminborbe.tools.util.ComparatorChain;

public class TaskComparator extends ComparatorChain<Task> {

	@SuppressWarnings("unchecked")
	@Inject
	public TaskComparator(final TaskNameComparator name, final TaskPrioComparator prio) {
		super(prio, name);
	}

}