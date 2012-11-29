package de.benjaminborbe.tools.mapper;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SingleMapStringList<T> extends SingleMapBase<T, List<String>> {

	public SingleMapStringList(final String name) {
		super(name);
	}

	@Override
	public String toString(final List<String> value) {
		if (value == null) {
			return null;
		}
		return StringUtils.join(value, ",");
	}

	@Override
	public List<String> fromString(final String value) {
		if (value == null) {
			return null;
		}
		return Arrays.asList(value.split(","));
	}
}
