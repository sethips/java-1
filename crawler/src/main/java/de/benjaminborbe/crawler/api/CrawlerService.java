package de.benjaminborbe.crawler.api;

public interface CrawlerService {

	void processCrawlerInstruction(CrawlerInstruction crawlerInstruction) throws CrawlerException;
}