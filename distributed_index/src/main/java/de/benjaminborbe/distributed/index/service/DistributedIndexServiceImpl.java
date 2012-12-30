package de.benjaminborbe.distributed.index.service;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.distributed.index.api.DistributedIndexIdentifier;
import de.benjaminborbe.distributed.index.api.DistributedIndexSearchResultIterator;
import de.benjaminborbe.distributed.index.api.DistributedIndexService;
import de.benjaminborbe.distributed.index.api.DistributedIndexServiceException;
import de.benjaminborbe.distributed.index.dao.DistributedIndexBean;
import de.benjaminborbe.distributed.index.dao.DistributedIndexDao;
import de.benjaminborbe.storage.api.StorageException;

@Singleton
public class DistributedIndexServiceImpl implements DistributedIndexService {

	private final Logger logger;

	private final DistributedIndexDao distributedIndexDao;

	@Inject
	public DistributedIndexServiceImpl(final Logger logger, final DistributedIndexDao distributedIndexDao) {
		this.logger = logger;
		this.distributedIndexDao = distributedIndexDao;
	}

	@Override
	public void add(final DistributedIndexIdentifier id, final Map<String, Integer> data) throws DistributedIndexServiceException {
		try {
			logger.debug("add - id: " + id);
			final DistributedIndexBean bean = distributedIndexDao.create();
			bean.setId(id);
			bean.setData(data);
			distributedIndexDao.save(bean);
		}
		catch (final StorageException e) {
			throw new DistributedIndexServiceException(e);
		}
	}

	@Override
	public void remove(final DistributedIndexIdentifier id) throws DistributedIndexServiceException {
		try {
			logger.debug("remove - id: " + id);
			distributedIndexDao.remove(id);
		}
		catch (final StorageException e) {
			throw new DistributedIndexServiceException(e);
		}
	}

	@Override
	public DistributedIndexSearchResultIterator search(final Collection<String> words) throws DistributedIndexServiceException {
		try {
			logger.debug("search - words: " + StringUtils.join(words, ','));
			distributedIndexDao.create();
			return null;
		}
		catch (final StorageException e) {
			throw new DistributedIndexServiceException(e);
		}
	}

}
