package de.benjaminborbe.search.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.search.api.SearchResult;
import de.benjaminborbe.search.api.SearchService;
import de.benjaminborbe.search.api.SearchServiceComponent;
import de.benjaminborbe.search.util.SearchServiceComponentRegistry;

@Singleton
public class SearchServiceImpl implements SearchService {

	private final Logger logger;

	private final SearchServiceComponentRegistry searchServiceComponentRegistry;

	@Inject
	public SearchServiceImpl(final Logger logger, final SearchServiceComponentRegistry searchServiceComponentRegistry) {
		this.logger = logger;
		this.searchServiceComponentRegistry = searchServiceComponentRegistry;
	}

	@Override
	public List<SearchResult> search(final String queryString, final int maxResults) {
		logger.debug("search queryString: " + queryString);

		final List<SearchResult> result = new ArrayList<SearchResult>();
		final Collection<SearchServiceComponent> searchServiceComponents = searchServiceComponentRegistry.getAll();
		logger.debug("searchServiceComponents " + searchServiceComponents.size());
		for (final SearchServiceComponent searchServiceComponent : searchServiceComponents) {
			logger.debug("search in searchServiceComponent: " + searchServiceComponent.getClass().getSimpleName());
			result.addAll(searchServiceComponent.search(queryString, maxResults));
		}
		logger.debug("found " + result.size() + " results");
		return result;
	}
}
