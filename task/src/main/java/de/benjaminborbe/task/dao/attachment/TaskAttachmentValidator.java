package de.benjaminborbe.task.dao.attachment;

import com.google.inject.Inject;
import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.task.api.TaskAttachmentIdentifier;
import de.benjaminborbe.task.api.TaskIdentifier;
import de.benjaminborbe.tools.validation.ValidationConstraintValidator;
import de.benjaminborbe.tools.validation.ValidatorBase;
import de.benjaminborbe.tools.validation.ValidatorRule;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraint;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintIdentifier;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintNotNull;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintStringMaxLength;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintStringMinLength;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintStringNot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskAttachmentValidator extends ValidatorBase<TaskAttachmentBean> {

	private final class ValidationConstrainAllowedCharacters implements ValidationConstraint<String> {

		@Override
		public boolean precondition(final String object) {
			return object != null;
		}

		@Override
		public boolean validate(final String object) {
			for (final char c : object.toCharArray()) {
				if (!Character.isLetter(c) && c != '-') {
					return false;
				}
			}
			return true;
		}
	}

	private final ValidationConstraintValidator validationConstraintValidator;

	@Inject
	public TaskAttachmentValidator(final ValidationConstraintValidator validationConstraintValidator) {
		this.validationConstraintValidator = validationConstraintValidator;
	}

	@Override
	public Class<TaskAttachmentBean> getType() {
		return TaskAttachmentBean.class;
	}

	@Override
	protected Map<String, ValidatorRule<TaskAttachmentBean>> buildRules() {
		final Map<String, ValidatorRule<TaskAttachmentBean>> result = new HashMap<>();

		// id
		{
			final String field = "id";
			result.put(field, new ValidatorRule<TaskAttachmentBean>() {

				@Override
				public Collection<ValidationError> validate(final TaskAttachmentBean bean) {
					final TaskAttachmentIdentifier value = bean.getId();
					final List<ValidationConstraint<TaskAttachmentIdentifier>> constraints = new ArrayList<>();
					constraints.add(new ValidationConstraintNotNull<TaskAttachmentIdentifier>());
					constraints.add(new ValidationConstraintIdentifier<TaskAttachmentIdentifier>());
					return validationConstraintValidator.validate(field, value, constraints);
				}
			});
		}

		// task
		{
			final String field = "task";
			result.put(field, new ValidatorRule<TaskAttachmentBean>() {

				@Override
				public Collection<ValidationError> validate(final TaskAttachmentBean bean) {
					final TaskIdentifier value = bean.getTask();
					final List<ValidationConstraint<TaskIdentifier>> constraints = new ArrayList<>();
					constraints.add(new ValidationConstraintNotNull<TaskIdentifier>());
					constraints.add(new ValidationConstraintIdentifier<TaskIdentifier>());
					return validationConstraintValidator.validate(field, value, constraints);
				}
			});
		}

		// name
		{
			final String field = "name";
			result.put(field, new ValidatorRule<TaskAttachmentBean>() {

				@Override
				public Collection<ValidationError> validate(final TaskAttachmentBean bean) {
					final String value = bean.getName();
					final List<ValidationConstraint<String>> constraints = new ArrayList<>();
					constraints.add(new ValidationConstraintNotNull<String>());
					constraints.add(new ValidationConstraintStringMinLength(1));
					constraints.add(new ValidationConstraintStringMaxLength(255));
					constraints.add(new ValidationConstrainAllowedCharacters());
					constraints.add(new ValidationConstraintStringNot("all", "none"));
					return validationConstraintValidator.validate(field, value, constraints);
				}
			});
		}

		return result;
	}
}