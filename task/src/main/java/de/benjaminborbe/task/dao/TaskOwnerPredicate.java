package de.benjaminborbe.task.dao;

import com.google.common.base.Predicate;

import de.benjaminborbe.authentication.api.UserIdentifier;

public class TaskOwnerPredicate implements Predicate<TaskBean> {

	private final UserIdentifier userIdentifier;

	public TaskOwnerPredicate(final UserIdentifier userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	@Override
	public boolean apply(final TaskBean task) {
		final Object username = userIdentifier.getId();
		return username.equals(task.getOwner());
	}
}
