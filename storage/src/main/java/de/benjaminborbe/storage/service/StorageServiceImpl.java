package de.benjaminborbe.storage.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.NotFoundException;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.util.StorageConfig;
import de.benjaminborbe.storage.util.StorageConnection;
import de.benjaminborbe.storage.util.StorageDaoUtil;

@Singleton
public class StorageServiceImpl implements StorageService {

	private final StorageConfig config;

	private final StorageDaoUtil storageDaoUtil;

	private final Logger logger;

	private final StorageConnection storageConnection;

	@Inject
	public StorageServiceImpl(final Logger logger, final StorageConfig config, final StorageDaoUtil storageDaoUtil, final StorageConnection storageConnection) {
		this.logger = logger;
		this.config = config;
		this.storageDaoUtil = storageDaoUtil;
		this.storageConnection = storageConnection;
	}

	@Override
	public String get(final String columnFamily, final String id, final String key) {
		try {
			storageConnection.open();

			return storageDaoUtil.read(config.getKeySpace(), columnFamily, id, key);
		}
		catch (final NotFoundException e) {
			return null;
		}
		catch (final Exception e) {
			logger.debug("Exception", e);
			return null;
		}
		finally {
			storageConnection.close();
		}
	}

	@Override
	public void delete(final String columnFamily, final String id, final String key) {
		try {
			storageConnection.open();

			storageDaoUtil.delete(config.getKeySpace(), columnFamily, id, key);
		}
		catch (final Exception e) {
			logger.debug("Exception", e);
		}
		finally {
			storageConnection.close();
		}
	}

	/**
	 * columnFamily <=> tabellenname
	 * id <=> primarykey
	 * key <=> fieldname
	 * value <=> fieldvalue
	 * 
	 * @throws StorageException
	 */
	@Override
	public void set(final String columnFamily, final String id, final String key, final String value) throws StorageException {
		final Map<String, String> data = new HashMap<String, String>();
		data.put(key, value);
		set(columnFamily, id, data);
	}

	/**
	 * columnFamily <=> tabellenname
	 * id <=> primarykey
	 * data: multible
	 * - key <=> fieldname
	 * - value <=> fieldvalue
	 * 
	 * @throws StorageException
	 */
	@Override
	public void set(final String columnFamily, final String id, final Map<String, String> data) throws StorageException {
		try {
			storageConnection.open();

			storageDaoUtil.insert(config.getKeySpace(), columnFamily, id, data);
		}
		catch (final Exception e) {
			logger.debug("Exception", e);
			throw new StorageException(e);
		}
		finally {
			storageConnection.close();
		}
	}

	@Override
	public Collection<String> findByIdPrefix(final String columnFamily, final String idPrefix) throws StorageException {
		final List<String> result = new ArrayList<String>();
		try {
			storageConnection.open();

			// TODO search in database
			final List<String> idList = storageDaoUtil.list(config.getKeySpace(), columnFamily);
			for (final String id : idList) {
				if (id.startsWith(idPrefix)) {
					result.add(id);
				}
			}

		}
		catch (final Exception e) {
			logger.debug("Exception", e);
			throw new StorageException(e);
		}
		finally {
			storageConnection.close();
		}
		return result;
	}

	@Override
	public List<String> list(final String columnFamily) throws StorageException {
		final List<String> result = new ArrayList<String>();
		try {
			storageConnection.open();

			result.addAll(storageDaoUtil.list(config.getKeySpace(), columnFamily));

		}
		catch (final Exception e) {
			logger.debug("Exception", e);
			throw new StorageException(e);
		}
		finally {
			storageConnection.close();
		}
		return result;
	}

}