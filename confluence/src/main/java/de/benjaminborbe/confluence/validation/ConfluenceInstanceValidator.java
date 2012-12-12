package de.benjaminborbe.confluence.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;

import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.confluence.dao.ConfluenceInstanceBean;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.validation.ValidationConstraintValidator;
import de.benjaminborbe.tools.validation.Validator;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraint;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintGreaterThan;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintStringMaxLength;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintStringMinLength;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintNotNull;
import de.benjaminborbe.tools.validation.constraint.ValidationConstraintUrl;

public class ConfluenceInstanceValidator implements Validator<ConfluenceInstanceBean> {

	private final UrlUtil urlUtil;

	private final ValidationConstraintValidator validationConstraintValidator;

	@Inject
	public ConfluenceInstanceValidator(final UrlUtil urlUtil, final ValidationConstraintValidator validationConstraintValidator) {
		this.urlUtil = urlUtil;
		this.validationConstraintValidator = validationConstraintValidator;
	}

	@Override
	public Class<ConfluenceInstanceBean> getType() {
		return ConfluenceInstanceBean.class;
	}

	@Override
	public Collection<ValidationError> validate(final ConfluenceInstanceBean bean) {
		final Set<ValidationError> result = new HashSet<ValidationError>();

		// validate name
		final String url = bean.getUrl();
		{
			final List<ValidationConstraint<String>> constraints = new ArrayList<ValidationConstraint<String>>();
			constraints.add(new ValidationConstraintNotNull<String>());
			constraints.add(new ValidationConstraintStringMinLength(1));
			constraints.add(new ValidationConstraintStringMaxLength(255));
			constraints.add(new ValidationConstraintUrl(urlUtil));
			result.addAll(validationConstraintValidator.validate("url", url, constraints));
		}

		final Integer expire = bean.getExpire();
		{
			final List<ValidationConstraint<Integer>> constraints = new ArrayList<ValidationConstraint<Integer>>();
			constraints.add(new ValidationConstraintNotNull<Integer>());
			constraints.add(new ValidationConstraintGreaterThan(0));
			result.addAll(validationConstraintValidator.validate("expire", expire, constraints));
		}

		return result;
	}

	@Override
	public Collection<ValidationError> validateObject(final Object object) {
		return validate((ConfluenceInstanceBean) object);
	}

}
