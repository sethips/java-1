package de.benjaminborbe.website.form;

public interface FormInputWidget<E extends FormElementWidget> extends FormElementWidget {

	E addLabel(final String label);

	E addDefaultValue(final Object defaultValue);

	E addValue(final Object value);

	E addPlaceholder(final String placeholder);

	E addId(final String id);

	String getName();
}
