package de.benjaminborbe.navigation.service;

import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.navigation.api.NavigationService;
import de.benjaminborbe.navigation.util.NavigationEntryRegistry;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class NavigationServiceImpl implements NavigationService {

	private final Logger logger;

	private final NavigationEntryRegistry navigationEntryRegistry;

	@Inject
	public NavigationServiceImpl(final Logger logger, final NavigationEntryRegistry navigationEntryRegistry) {
		this.logger = logger;
		this.navigationEntryRegistry = navigationEntryRegistry;
	}

	@Override
	public Collection<NavigationEntry> getNavigationEntries() {
		logger.trace("getNavigationEntries");
		return navigationEntryRegistry.getAll();
	}
}
