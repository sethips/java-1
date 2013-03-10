package de.benjaminborbe.confluence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.api.ValidationResult;
import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.AuthorizationServiceException;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.confluence.api.ConfluenceInstance;
import de.benjaminborbe.confluence.api.ConfluenceInstanceIdentifier;
import de.benjaminborbe.confluence.api.ConfluenceService;
import de.benjaminborbe.confluence.api.ConfluenceServiceException;
import de.benjaminborbe.confluence.dao.ConfluenceInstanceBean;
import de.benjaminborbe.confluence.dao.ConfluenceInstanceDao;
import de.benjaminborbe.confluence.dao.ConfluencePageBean;
import de.benjaminborbe.confluence.dao.ConfluencePageDao;
import de.benjaminborbe.confluence.util.ConfluenceIndexUtil;
import de.benjaminborbe.confluence.util.ConfluenceRefresher;
import de.benjaminborbe.index.api.IndexService;
import de.benjaminborbe.index.api.IndexerServiceException;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;
import de.benjaminborbe.storage.tools.IdentifierIterator;
import de.benjaminborbe.storage.tools.IdentifierIteratorException;
import de.benjaminborbe.tools.util.Duration;
import de.benjaminborbe.tools.util.DurationUtil;
import de.benjaminborbe.tools.util.IdGeneratorUUID;
import de.benjaminborbe.tools.validation.ValidationExecutor;

@Singleton
public class ConfluenceServiceImpl implements ConfluenceService {

	private final Logger logger;

	private final ConfluenceInstanceDao confluenceInstanceDao;

	private final DurationUtil durationUtil;

	private final ValidationExecutor validationExecutor;

	private final IdGeneratorUUID idGeneratorUUID;

	private final ConfluenceRefresher confluenceRefresher;

	private final AuthorizationService authorizationService;

	private final ConfluencePageDao confluencePageDao;

	private final AuthenticationService authenticationService;

	private final IndexService indexService;

	private final ConfluenceIndexUtil confluenceIndexUtil;

	@Inject
	public ConfluenceServiceImpl(
			final Logger logger,
			final AuthenticationService authenticationService,
			final AuthorizationService authorizationService,
			final ConfluenceInstanceDao confluenceInstanceDao,
			final ConfluencePageDao confluencePageDao,
			final DurationUtil durationUtil,
			final IdGeneratorUUID idGeneratorUUID,
			final ValidationExecutor validationExecutor,
			final ConfluenceRefresher confluenceRefresher,
			final IndexService indexService,
			final ConfluenceIndexUtil confluenceIndexUtil) {
		this.logger = logger;
		this.authenticationService = authenticationService;
		this.confluenceInstanceDao = confluenceInstanceDao;
		this.confluencePageDao = confluencePageDao;
		this.durationUtil = durationUtil;
		this.idGeneratorUUID = idGeneratorUUID;
		this.validationExecutor = validationExecutor;
		this.confluenceRefresher = confluenceRefresher;
		this.authorizationService = authorizationService;
		this.indexService = indexService;
		this.confluenceIndexUtil = confluenceIndexUtil;
	}

	@Override
	public ConfluenceInstanceIdentifier createConfluenceIntance(final SessionIdentifier sessionIdentifier, final String url, final String username, final String password,
			final int expire, final boolean shared, final long delay, final boolean activated, final String owner) throws ConfluenceServiceException, LoginRequiredException,
			PermissionDeniedException, ValidationException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("createConfluenceIntance");
			expectPermission(sessionIdentifier);

			final ConfluenceInstanceIdentifier confluenceInstanceIdentifier = createConfluenceInstanceIdentifier(sessionIdentifier, idGeneratorUUID.nextId());
			final ConfluenceInstanceBean confluenceInstance = confluenceInstanceDao.create();
			confluenceInstance.setId(confluenceInstanceIdentifier);
			confluenceInstance.setUrl(url);
			confluenceInstance.setUsername(username);
			confluenceInstance.setPassword(password);
			confluenceInstance.setExpire(expire);
			confluenceInstance.setShared(shared);
			confluenceInstance.setDelay(delay);
			confluenceInstance.setActivated(activated);

			setOwner(sessionIdentifier, owner, confluenceInstance);

			final ValidationResult errors = validationExecutor.validate(confluenceInstance);
			if (errors.hasErrors()) {
				logger.warn("ConfluenceInstanceBean " + errors.toString());
				throw new ValidationException(errors);
			}
			confluenceInstanceDao.save(confluenceInstance);

			return confluenceInstanceIdentifier;
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public void updateConfluenceIntance(final SessionIdentifier sessionIdentifier, final ConfluenceInstanceIdentifier confluenceInstanceIdentifier, final String url,
			final String username, final String password, final int expire, final boolean shared, final long delay, final boolean activated, final String owner)
			throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException, ValidationException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("updateConfluenceIntance");
			expectPermission(sessionIdentifier);

			final ConfluenceInstanceBean confluenceInstance = confluenceInstanceDao.load(confluenceInstanceIdentifier);
			confluenceInstance.setUrl(url);
			confluenceInstance.setUsername(username);
			confluenceInstance.setExpire(expire);
			confluenceInstance.setDelay(delay);
			confluenceInstance.setActivated(activated);

			// remove content shared change
			if (Boolean.TRUE.equals(confluenceInstance.getShared()) && !shared || !Boolean.TRUE.equals(confluenceInstance.getShared()) && shared) {
				logger.debug("shared changed => remove content");
				removeContent(confluenceInstanceIdentifier);
				confluenceInstance.setShared(shared);
			}
			else {
				logger.debug("shared not changed => nop");
				confluenceInstance.setShared(shared);
			}

			setOwner(sessionIdentifier, owner, confluenceInstance);

			if (password != null && password.length() > 0) {
				confluenceInstance.setPassword(password);
			}

			final ValidationResult errors = validationExecutor.validate(confluenceInstance);
			if (errors.hasErrors()) {
				logger.warn("ConfluenceInstanceBean " + errors.toString());
				throw new ValidationException(errors);
			}
			confluenceInstanceDao.save(confluenceInstance);
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final IndexerServiceException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	private void setOwner(final SessionIdentifier sessionIdentifier, final String owner, final ConfluenceInstanceBean confluenceInstance) throws AuthenticationServiceException {
		logger.debug("setOwner " + owner);
		if (owner != null && !owner.isEmpty()) {
			final UserIdentifier userIdentifier = authenticationService.createUserIdentifier(owner);
			if (authenticationService.existsUser(userIdentifier)) {
				confluenceInstance.setOwner(userIdentifier);
			}
			else {
				logger.debug("user " + owner + " not found");
			}
		}

		if (confluenceInstance.getOwner() == null) {
			confluenceInstance.setOwner(authenticationService.getCurrentUser(sessionIdentifier));
		}
	}

	@Override
	public void deleteConfluenceInstance(final SessionIdentifier sessionIdentifier, final ConfluenceInstanceIdentifier confluenceInstanceIdentifier)
			throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("deleteConfluenceInstance");
			expectPermission(sessionIdentifier);

			removeContent(confluenceInstanceIdentifier);

			confluenceInstanceDao.delete(confluenceInstanceIdentifier);
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final IndexerServiceException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	private void removeContent(final ConfluenceInstanceIdentifier confluenceInstanceIdentifier) throws StorageException, EntityIteratorException, IndexerServiceException {
		final ConfluenceInstanceBean confluenceInstanceBean = confluenceInstanceDao.load(confluenceInstanceIdentifier);
		final String indexName = confluenceIndexUtil.getIndex(confluenceInstanceBean);

		final EntityIterator<ConfluencePageBean> i = confluencePageDao.getPagesOfInstance(confluenceInstanceIdentifier);
		while (i.hasNext()) {
			final ConfluencePageBean page = i.next();
			indexService.removeFromIndex(indexName, page.getUrl());
			confluencePageDao.delete(page);
		}
	}

	@Override
	public Collection<ConfluenceInstanceIdentifier> getConfluenceInstanceIdentifiers(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException,
			LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getConfluenceInstanceIdentifiers");
			expectPermission(sessionIdentifier);

			final IdentifierIterator<ConfluenceInstanceIdentifier> i = confluenceInstanceDao.getIdentifierIterator();
			final List<ConfluenceInstanceIdentifier> result = new ArrayList<ConfluenceInstanceIdentifier>();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		}
		catch (final IdentifierIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public ConfluenceInstanceIdentifier createConfluenceInstanceIdentifier(final SessionIdentifier sessionIdentifier, final String id) throws ConfluenceServiceException,
			LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getConfluenceInstances");
			expectPermission(sessionIdentifier);

			return new ConfluenceInstanceIdentifier(id);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<ConfluenceInstance> getConfluenceInstances(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException, LoginRequiredException,
			PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getConfluenceInstances");
			expectPermission(sessionIdentifier);

			final EntityIterator<ConfluenceInstanceBean> i = confluenceInstanceDao.getEntityIterator();
			final List<ConfluenceInstance> result = new ArrayList<ConfluenceInstance>();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public ConfluenceInstance getConfluenceInstance(final SessionIdentifier sessionIdentifier, final ConfluenceInstanceIdentifier confluenceInstanceIdentifier)
			throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getConfluenceInstances - id: " + confluenceInstanceIdentifier);
			expectPermission(sessionIdentifier);

			final ConfluenceInstance confluenceInstance = confluenceInstanceDao.load(confluenceInstanceIdentifier);
			if (confluenceInstance == null) {
				logger.info("confluenceInstance not found with id " + confluenceInstanceIdentifier);
				return null;
			}
			return confluenceInstance;
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public void refreshSearchIndex(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("refreshSearchIndex");
			expectPermission(sessionIdentifier);

			confluenceRefresher.refresh();
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public void expireAll(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("expireAll");
			expectPermission(sessionIdentifier);

			final EntityIterator<ConfluencePageBean> i = confluencePageDao.getEntityIterator();
			while (i.hasNext()) {
				final ConfluencePageBean bean = i.next();
				logger.debug("expire: " + bean.getId());
				bean.setLastVisit(null);
				confluencePageDao.save(bean);
			}
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public long countPages(final SessionIdentifier sessionIdentifier, final ConfluenceInstanceIdentifier confluenceInstanceIdentifier) throws ConfluenceServiceException,
			LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("countPages");
			expectPermission(sessionIdentifier);

			return confluencePageDao.countPagesOfInstance(confluenceInstanceIdentifier);
		}
		catch (final StorageException e) {
			throw new ConfluenceServiceException(e);
		}
		catch (final IdentifierIteratorException e) {
			throw new ConfluenceServiceException(e);
		}
		finally {
			logger.trace("duration " + duration.getTime());
		}
	}

	@Override
	public void expectPermission(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException, LoginRequiredException, PermissionDeniedException {
		try {
			authorizationService.expectPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(PERMISSION));
		}
		catch (final AuthorizationServiceException e) {
			throw new ConfluenceServiceException(e);
		}
	}

	@Override
	public boolean hasPermission(final SessionIdentifier sessionIdentifier) throws ConfluenceServiceException {
		try {
			return authorizationService.hasPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(PERMISSION));
		}
		catch (final AuthorizationServiceException e) {
			throw new ConfluenceServiceException(e);
		}
	}
}
