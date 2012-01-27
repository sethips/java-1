package de.benjaminborbe.configuration.service;

import java.util.Collection;
import java.util.HashSet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.configuration.api.Configuration;
import de.benjaminborbe.configuration.api.ConfigurationService;

@Singleton
public class ConfigurationServiceMock implements ConfigurationService {

	@Inject
	public ConfigurationServiceMock() {
	}

	@Override
	public Collection<Configuration<?>> listConfigurations() {
		return new HashSet<Configuration<?>>();
	}

	@Override
	public <T> T getConfigurationValue(final Configuration<T> configuration) {
		return configuration.getDefaultValue();
	}

}