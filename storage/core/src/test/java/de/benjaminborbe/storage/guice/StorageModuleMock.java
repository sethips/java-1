package de.benjaminborbe.storage.guice;

import com.google.inject.AbstractModule;
import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.storage.config.StorageConfig;
import de.benjaminborbe.storage.config.StorageConfigImpl;
import de.benjaminborbe.storage.service.StorageServiceImpl;
import de.benjaminborbe.storage.util.StorageDaoUtil;
import de.benjaminborbe.storage.util.StorageDaoUtilImpl;

import javax.inject.Singleton;

public class StorageModuleMock extends AbstractModule {

	@Override
	protected void configure() {
		bind(StorageDaoUtil.class).to(StorageDaoUtilImpl.class).in(Singleton.class);
		bind(StorageConfig.class).to(StorageConfigImpl.class).in(Singleton.class);
		bind(StorageService.class).to(StorageServiceImpl.class).in(Singleton.class);
	}
}
