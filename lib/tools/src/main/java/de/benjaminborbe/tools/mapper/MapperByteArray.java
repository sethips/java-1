package de.benjaminborbe.tools.mapper;

import de.benjaminborbe.tools.util.Base64Util;

import javax.inject.Inject;

public class MapperByteArray implements Mapper<byte[]> {

	private final Base64Util base64Util;

	@Inject
	public MapperByteArray(final Base64Util base64Util) {
		this.base64Util = base64Util;
	}

	@Override
	public String toString(final byte[] value) {
		if (value == null) {
			return null;
		}
		return base64Util.encode(value);
	}

	@Override
	public byte[] fromString(final String value) {
		if (value == null) {
			return null;
		}
		return base64Util.decode(value);
	}
}
