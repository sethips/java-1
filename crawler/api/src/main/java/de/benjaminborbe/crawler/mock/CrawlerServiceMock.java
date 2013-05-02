package de.benjaminborbe.crawler.mock;

import de.benjaminborbe.crawler.api.CrawlerException;
import de.benjaminborbe.crawler.api.CrawlerInstruction;
import de.benjaminborbe.crawler.api.CrawlerService;
import de.benjaminborbe.httpdownloader.api.HttpResponse;

public class CrawlerServiceMock implements CrawlerService {

	@Override
	public void processCrawlerInstruction(final CrawlerInstruction crawlerInstruction) throws CrawlerException {
	}

	@Override
	public void notify(final HttpResponse httpResponse) throws CrawlerException {
	}

}
