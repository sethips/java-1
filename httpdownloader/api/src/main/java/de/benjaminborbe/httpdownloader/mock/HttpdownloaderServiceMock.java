package de.benjaminborbe.httpdownloader.mock;

import de.benjaminborbe.httpdownloader.api.HttpRequest;
import de.benjaminborbe.httpdownloader.api.HttpResponse;
import de.benjaminborbe.httpdownloader.api.HttpdownloaderService;
import de.benjaminborbe.httpdownloader.api.HttpdownloaderServiceException;

public class HttpdownloaderServiceMock implements HttpdownloaderService {

	public HttpdownloaderServiceMock() {
	}

	@Override
	public HttpResponse getSecure(final HttpRequest httpRequest) throws HttpdownloaderServiceException {
		return null;
	}

	@Override
	public HttpResponse getUnsecure(final HttpRequest httpRequest) throws HttpdownloaderServiceException {
		return null;
	}
}
