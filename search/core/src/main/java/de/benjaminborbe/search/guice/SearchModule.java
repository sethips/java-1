package de.benjaminborbe.search.guice;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.benjaminborbe.search.api.SearchService;
import de.benjaminborbe.search.config.SearchConfig;
import de.benjaminborbe.search.config.SearchConfigImpl;
import de.benjaminborbe.search.dao.SearchQueryHistoryDao;
import de.benjaminborbe.search.dao.SearchQueryHistoryDaoStorage;
import de.benjaminborbe.search.service.SearchServiceImpl;
import de.benjaminborbe.search.util.SearchServiceComponentRegistry;
import de.benjaminborbe.search.util.SearchServiceComponentRegistryImpl;
import de.benjaminborbe.tools.log.LoggerSlf4Provider;

public class SearchModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SearchConfig.class).to(SearchConfigImpl.class).in(Singleton.class);
		bind(SearchQueryHistoryDao.class).to(SearchQueryHistoryDaoStorage.class).in(Singleton.class);
		bind(SearchServiceComponentRegistry.class).to(SearchServiceComponentRegistryImpl.class).in(Singleton.class);
		bind(SearchService.class).to(SearchServiceImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);
	}
}