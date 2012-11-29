package de.benjaminborbe.websearch.api;

import java.net.URL;
import java.util.Collection;

import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;

public interface WebsearchService {

	Collection<Page> getPages(final SessionIdentifier sessionIdentifier) throws WebsearchServiceException, PermissionDeniedException;

	void refreshSearchIndex(final SessionIdentifier sessionIdentifier) throws WebsearchServiceException, PermissionDeniedException;

	void refreshPage(final SessionIdentifier sessionIdentifier, PageIdentifier page) throws WebsearchServiceException, PermissionDeniedException;

	void expirePage(final SessionIdentifier sessionIdentifier, PageIdentifier page) throws WebsearchServiceException, PermissionDeniedException;

	void clearIndex(final SessionIdentifier sessionIdentifier) throws WebsearchServiceException, PermissionDeniedException;

	PageIdentifier createPageIdentifier(URL id) throws WebsearchServiceException;

}
