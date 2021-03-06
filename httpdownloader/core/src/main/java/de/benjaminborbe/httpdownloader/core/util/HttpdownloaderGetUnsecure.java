package de.benjaminborbe.httpdownloader.core.util;

import de.benjaminborbe.httpdownloader.api.HttpRequest;

import javax.inject.Inject;

public class HttpdownloaderGetUnsecure implements HttpdownloaderGet {

	private final HttpDownloader httpDownloader;

	@Inject
	public HttpdownloaderGetUnsecure(final HttpDownloader httpDownloader) {
		this.httpDownloader = httpDownloader;
	}

	@Override
	public HttpDownloadResult fetch(final HttpRequest httpRequest) throws HttpDownloaderException {
		return httpDownloader.getUrlUnsecure(httpRequest.getUrl(), httpRequest.getTimeout(), httpRequest.getUsername(), httpRequest.getPassword(), httpRequest.getHeader());

	}
}
