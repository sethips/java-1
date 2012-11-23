package de.benjaminborbe.confluence.dao;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.benjaminborbe.confluence.api.ConfluenceInstanceIdentifier;
import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.storage.tools.DaoStorage;
import de.benjaminborbe.tools.date.CalendarUtil;

@Singleton
public class ConfluenceInstanceDaoStorage extends DaoStorage<ConfluenceInstanceBean, ConfluenceInstanceIdentifier> implements ConfluenceInstanceDao {

	@Inject
	public ConfluenceInstanceDaoStorage(
			final Logger logger,
			final StorageService storageService,
			final Provider<ConfluenceInstanceBean> beanProvider,
			final ConfluenceInstanceBeanMapper mapper,
			final ConfluenceInstanceIdentifierBuilder identifierBuilder,
			final CalendarUtil calendarUtil) {
		super(logger, storageService, beanProvider, mapper, identifierBuilder, calendarUtil);
	}

	private static final String COLUMN_FAMILY = "confluence_instance";

	@Override
	protected String getColumnFamily() {
		return COLUMN_FAMILY;
	}

}
