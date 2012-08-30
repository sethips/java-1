package de.benjaminborbe.wiki.service;

import java.util.Collection;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.api.ValidationErrorSimple;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.tools.validation.ValidationResultImpl;
import de.benjaminborbe.wiki.api.WikiPage;
import de.benjaminborbe.wiki.api.WikiPageCreateException;
import de.benjaminborbe.wiki.api.WikiPageIdentifier;
import de.benjaminborbe.wiki.api.WikiService;
import de.benjaminborbe.wiki.api.WikiServiceException;
import de.benjaminborbe.wiki.api.WikiSpaceCreateException;
import de.benjaminborbe.wiki.api.WikiSpaceIdentifier;
import de.benjaminborbe.wiki.api.WikiSpaceNotFoundException;
import de.benjaminborbe.wiki.dao.WikiPageBean;
import de.benjaminborbe.wiki.dao.WikiPageDao;
import de.benjaminborbe.wiki.dao.WikiSpaceBean;
import de.benjaminborbe.wiki.dao.WikiSpaceDao;

@Singleton
public class WikiServiceImpl implements WikiService {

	private final Logger logger;

	private final WikiSpaceDao wikiSpaceDao;

	private final WikiPageDao wikiPageDao;

	@Inject
	public WikiServiceImpl(final Logger logger, final WikiSpaceDao wikiSpaceDao, final WikiPageDao wikiPageDao) {
		this.logger = logger;
		this.wikiSpaceDao = wikiSpaceDao;
		this.wikiPageDao = wikiPageDao;
	}

	@Override
	public Collection<WikiSpaceIdentifier> getSpaceIdentifiers() throws WikiServiceException {
		logger.trace("getSpaceIdentifiers");
		try {
			return wikiSpaceDao.getIdentifiers();
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public Collection<WikiPageIdentifier> getPageIdentifiers(final WikiSpaceIdentifier wikiSpaceIdentifier) throws WikiServiceException {
		logger.trace("getPageIdentifiers");
		try {
			return wikiPageDao.getIdentifiers();
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public WikiPage getPage(final WikiPageIdentifier wikiPageIdentifier) throws WikiServiceException {
		try {
			logger.trace("getPage");
			return wikiPageDao.load(wikiPageIdentifier);
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public WikiSpaceIdentifier getSpaceByName(final String spaceName) throws WikiServiceException {
		logger.trace("getSpaceByName");
		try {
			if (wikiSpaceDao.existsSpaceWithName(spaceName)) {
				return new WikiSpaceIdentifier(spaceName);
			}
			else {
				throw new WikiSpaceNotFoundException("space with name " + spaceName + " does not exists");
			}
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public WikiPage getPageByName(final WikiSpaceIdentifier wikiSpaceIdentifier, final String pageName) throws WikiServiceException {
		logger.trace("getPageByName");
		final WikiPageIdentifier wikiPageIdentifier = getPageIdentifierByName(wikiSpaceIdentifier, pageName);
		return getPage(wikiPageIdentifier);
	}

	@Override
	public WikiPageIdentifier getPageIdentifierByName(final WikiSpaceIdentifier wikiSpaceIdentifier, final String pageName) {
		logger.trace("getPageIdentifierByName");
		return new WikiPageIdentifier(wikiSpaceIdentifier.getId() + "-" + pageName);
	}

	@Override
	public WikiPageIdentifier createPage(final WikiSpaceIdentifier wikiSpaceIdentifier, final String pageTitle, final String pageContent) throws WikiPageCreateException,
			WikiServiceException {
		try {
			logger.debug("createPage");

			if (!wikiSpaceDao.exists(wikiSpaceIdentifier)) {
				final ValidationResultImpl validationResult = new ValidationResultImpl(new ValidationErrorSimple("space " + wikiSpaceIdentifier + " not found"));
				throw new WikiPageCreateException(validationResult);
			}

			final WikiPageIdentifier wikiPageIdentifier = getPageIdentifierByName(wikiSpaceIdentifier, pageTitle);

			if (wikiPageDao.exists(wikiPageIdentifier)) {
				final ValidationResultImpl validationResult = new ValidationResultImpl(new ValidationErrorSimple("page " + wikiPageIdentifier + " already exists"));
				throw new WikiPageCreateException(validationResult);
			}

			final WikiPageBean wikiPage = wikiPageDao.create();
			wikiPage.setId(wikiPageIdentifier);
			wikiPage.setTitle(pageTitle);
			wikiPage.setContent(pageContent);
			wikiPage.setSpace(wikiSpaceIdentifier);
			wikiPageDao.save(wikiPage);

			return wikiPageIdentifier;
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public boolean updatePage(final WikiPageIdentifier wikiPageIdentifier, final String pageTitle, final String pageContent) {
		logger.trace("updatePage");
		return false;
	}

	@Override
	public boolean deletePage(final WikiPageIdentifier wikiPageIdentifier) {
		logger.trace("deletePage");
		return false;
	}

	@Override
	public WikiSpaceIdentifier createSpace(final String spaceIdentifier, final String spaceTitle) throws WikiServiceException, WikiSpaceCreateException {
		try {
			logger.debug("createSpace");

			if (wikiSpaceDao.existsSpaceWithName(spaceIdentifier)) {
				final ValidationResultImpl validationResult = new ValidationResultImpl(new ValidationErrorSimple("space " + spaceIdentifier + " already exists"));
				throw new WikiSpaceCreateException(validationResult);
			}

			final WikiSpaceIdentifier id = new WikiSpaceIdentifier(spaceIdentifier);
			final WikiSpaceBean wikiSpace = wikiSpaceDao.create();
			wikiSpace.setId(id);
			wikiSpace.setName(spaceTitle);
			wikiSpaceDao.save(wikiSpace);

			return id;
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public boolean deleteSpace(final WikiSpaceIdentifier wikiSpaceIdentifier) throws WikiServiceException {
		try {
			logger.trace("deleteSpace");
			wikiSpaceDao.delete(wikiSpaceIdentifier);
			return true;
		}
		catch (final StorageException e) {
			throw new WikiServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public String renderPage(final WikiPageIdentifier wikiPageIdentifier) throws WikiServiceException {
		return null;
	}
}
