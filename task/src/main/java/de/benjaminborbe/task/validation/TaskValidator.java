package de.benjaminborbe.task.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.api.ValidationErrorSimple;
import de.benjaminborbe.task.dao.TaskBean;
import de.benjaminborbe.tools.validation.Validator;

public class TaskValidator implements Validator<TaskBean> {

	@Override
	public Class<TaskBean> getType() {
		return TaskBean.class;
	}

	@Override
	public Collection<ValidationError> validate(final Object object) {
		final TaskBean bean = (TaskBean) object;
		final Set<ValidationError> result = new HashSet<ValidationError>();

		// validate name
		final String name = bean.getName();
		{
			if (name == null || name.length() == 0) {
				result.add(new ValidationErrorSimple("name missing"));
			}
		}

		return result;
	}

}
