package de.benjaminborbe.websearch.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.index.api.IndexSearchResult;
import de.benjaminborbe.index.api.IndexSearcherService;
import de.benjaminborbe.search.api.SearchResult;
import de.benjaminborbe.search.api.SearchResultImpl;
import de.benjaminborbe.search.api.SearchServiceComponent;
import de.benjaminborbe.websearch.WebsearchActivator;

@Singleton
public class WebsearchSearchServiceComponent implements SearchServiceComponent {

	private final Logger logger;

	private final IndexSearcherService indexSearcherService;

	@Inject
	public WebsearchSearchServiceComponent(final Logger logger, final IndexSearcherService indexSearcherService) {
		this.logger = logger;
		this.indexSearcherService = indexSearcherService;
	}

	@Override
	public List<SearchResult> search(final String[] words, final int maxResults) {
		logger.debug("search");
		final List<SearchResult> result = new ArrayList<SearchResult>();
		final List<IndexSearchResult> indexResults = indexSearcherService.search(WebsearchActivator.INDEX, StringUtils.join(words, " "));
		for (final IndexSearchResult indexResult : indexResults) {
			result.add(map(indexResult));
		}
		return result;
	}

	protected SearchResult map(final IndexSearchResult indexResult) {
		final String type = "web";
		final String title = indexResult.getTitle();
		final URL url = indexResult.getURL();
		final String description = buildDescription(indexResult.getContent());
		return new SearchResultImpl(type, title, url, description);
	}

	protected String buildDescription(final String content) {
		return "-";
	}
}