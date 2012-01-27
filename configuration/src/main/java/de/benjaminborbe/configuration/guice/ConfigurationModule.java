package de.benjaminborbe.configuration.guice;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.benjaminborbe.configuration.api.ConfigurationService;
import de.benjaminborbe.configuration.service.ConfigurationServiceImpl;
import de.benjaminborbe.tools.log.LoggerSlf4Provider;

public class ConfigurationModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ConfigurationService.class).to(ConfigurationServiceImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);
	}
}