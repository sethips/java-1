package de.benjaminborbe.util.guice;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.benjaminborbe.tools.log.LoggerSlf4Provider;
import de.benjaminborbe.util.UtilPasswordGenerator;
import de.benjaminborbe.util.UtilPasswordGeneratorImpl;

public class UtilModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(UtilPasswordGenerator.class).to(UtilPasswordGeneratorImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);
	}
}