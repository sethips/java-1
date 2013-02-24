package de.benjaminborbe.poker.guice;

import static org.ops4j.peaberry.Peaberry.service;

import org.apache.felix.http.api.ExtHttpService;
import org.osgi.service.log.LogService;

import com.google.inject.AbstractModule;

import de.benjaminborbe.analytics.api.AnalyticsService;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.configuration.api.ConfigurationService;
import de.benjaminborbe.storage.api.StorageService;

public class PokerOsgiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AnalyticsService.class).toProvider(service(AnalyticsService.class).single());
		bind(ConfigurationService.class).toProvider(service(ConfigurationService.class).single());
		bind(AuthorizationService.class).toProvider(service(AuthorizationService.class).single());
		bind(StorageService.class).toProvider(service(StorageService.class).single());
		bind(LogService.class).toProvider(service(LogService.class).single());
		bind(ExtHttpService.class).toProvider(service(ExtHttpService.class).single());
	}
}
