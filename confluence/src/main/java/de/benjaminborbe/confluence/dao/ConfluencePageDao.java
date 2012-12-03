package de.benjaminborbe.confluence.dao;

import de.benjaminborbe.confluence.api.ConfluenceInstanceIdentifier;
import de.benjaminborbe.confluence.api.ConfluencePageIdentifier;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.tools.Dao;

public interface ConfluencePageDao extends Dao<ConfluencePageBean, ConfluencePageIdentifier> {

	ConfluencePageBean findOrCreate(ConfluenceInstanceIdentifier id, String pageId) throws StorageException;

}