package de.benjaminborbe.storage.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import de.benjaminborbe.configuration.api.ConfigurationDescription;
import de.benjaminborbe.configuration.tools.ConfigurationDescriptionInt;
import de.benjaminborbe.configuration.tools.ConfigurationDescriptionString;

public class StorageConfigImpl implements StorageConfig {

	private final ConfigurationDescriptionString hostname = new ConfigurationDescriptionString("localhost", "CassandraHost", "Hostname of CassandraServer");

	private final ConfigurationDescriptionInt port = new ConfigurationDescriptionInt(9160, "CassandraPort", "Port of CassandraServer");

	private final ConfigurationDescriptionString keyspace = new ConfigurationDescriptionString("bb", "CassandraKeyspace", "Keyspace of CassandraServer");

	private final ConfigurationDescriptionString encoding = new ConfigurationDescriptionString("UTF8", "CassandraEncoding", "Encoding of CassandraServer");

	private final ConfigurationDescriptionInt readLimit = new ConfigurationDescriptionInt(10000, "CassandraReadLimit", "ReadLimit of CassandraServer");

	@Inject
	public StorageConfigImpl() {
	}

	@Override
	public String getHost() {
		return getValue(hostname);
	}

	@Override
	public int getPort() {
		return getValue(port);
	}

	@Override
	public String getKeySpace() {
		return getValue(keyspace);
	}

	@Override
	public String getEncoding() {
		return getValue(encoding);
	}

	@Override
	public int getReadLimit() {
		return getValue(readLimit);
	}

	private int getValue(final ConfigurationDescriptionInt configuration) {
		return configuration.getDefaultValue();
	}

	private String getValue(final ConfigurationDescriptionString configuration) {
		return configuration.getDefaultValue();
	}

	@Override
	public Collection<ConfigurationDescription> getConfigurations() {
		final Set<ConfigurationDescription> result = new HashSet<ConfigurationDescription>();
		result.add(hostname);
		result.add(port);
		result.add(keyspace);
		result.add(encoding);
		return result;
	}

}
