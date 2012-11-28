package de.benjaminborbe.bookmark.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import de.benjaminborbe.bookmark.api.Bookmark;
import de.benjaminborbe.bookmark.api.BookmarkDeletionException;
import de.benjaminborbe.bookmark.api.BookmarkIdentifier;
import de.benjaminborbe.bookmark.api.BookmarkService;
import de.benjaminborbe.bookmark.api.BookmarkServiceException;
import de.benjaminborbe.bookmark.dao.BookmarkBean;
import de.benjaminborbe.bookmark.dao.BookmarkDao;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;
import de.benjaminborbe.tools.util.ComparatorBase;
import de.benjaminborbe.tools.validation.ValidationExecutor;

@Singleton
public class BookmarkServiceImpl implements BookmarkService {

	private final class MatchComparator implements Comparator<Match> {

		@Override
		public int compare(final Match a, final Match b) {
			if (a.getCounter() > b.getCounter()) {
				return -1;
			}
			if (a.getCounter() < b.getCounter()) {
				return 1;
			}
			return 0;
		}
	}

	private final class BookmarkByNameComparator extends ComparatorBase<Bookmark, String> {

		@Override
		public String getValue(final Bookmark o) {
			return o.getName();
		}
	}

	private final Comparator<Bookmark> bookmarkComparator = new BookmarkByNameComparator();

	private final Logger logger;

	private final BookmarkDao bookmarkDao;

	private final AuthenticationService authenticationService;

	private final ValidationExecutor validationExecutor;

	private final AuthorizationService authorizationService;

	@Inject
	public BookmarkServiceImpl(
			final Logger logger,
			final AuthenticationService authenticationService,
			final AuthorizationService authorizationService,
			final BookmarkDao bookmarkDao,
			final ValidationExecutor validationExecutor) {
		this.logger = logger;
		this.authenticationService = authenticationService;
		this.authorizationService = authorizationService;
		this.bookmarkDao = bookmarkDao;
		this.validationExecutor = validationExecutor;
	}

	@Override
	public List<Bookmark> getBookmarks(final SessionIdentifier sessionIdentifier) throws BookmarkServiceException, LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);
			logger.trace("getBookmarks");
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);
			final EntityIterator<BookmarkBean> i = bookmarkDao.getByUsername(userIdentifier);
			final List<Bookmark> bookmarks = new ArrayList<Bookmark>();
			while (i.hasNext()) {
				bookmarks.add(i.next());
			}
			Collections.sort(bookmarks, bookmarkComparator);
			return bookmarks;
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public List<Bookmark> getBookmarkFavorite(final SessionIdentifier sessionIdentifier) throws BookmarkServiceException, LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);
			logger.trace("getBookmarkFavorite");
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);
			final EntityIterator<BookmarkBean> i = bookmarkDao.getFavorites(userIdentifier);
			final List<Bookmark> bookmarks = new ArrayList<Bookmark>();
			while (i.hasNext()) {
				bookmarks.add(i.next());
			}
			Collections.sort(bookmarks, bookmarkComparator);
			return bookmarks;
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final EntityIteratorException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public List<Bookmark> searchBookmarks(final SessionIdentifier sessionIdentifier, final String[] parts) throws BookmarkServiceException, LoginRequiredException {
		final List<Match> matches = new ArrayList<Match>();
		for (final Bookmark bookmark : getBookmarks(sessionIdentifier)) {
			final int counter = match(bookmark, parts);
			if (counter > 0) {
				final Match match = new Match(bookmark, counter);
				matches.add(match);
			}
		}
		Collections.sort(matches, new MatchComparator());
		final List<Bookmark> result = new ArrayList<Bookmark>();
		for (final Match match : matches) {
			result.add(match.getBookmark());
		}
		return result;
	}

	protected int match(final Bookmark bookmark, final String... searchTerms) {
		int counter = 0;
		for (final String searchTerm : searchTerms) {
			if (searchTerm != null && searchTerm.length() > 0 && match(bookmark, searchTerm)) {
				counter++;
			}
		}
		return counter;
	}

	protected boolean match(final Bookmark bookmark, final String search) {
		for (final String value : getSearchValues(bookmark)) {
			if (value.toLowerCase().indexOf(search.toLowerCase()) != -1) {
				return true;
			}
		}
		return false;
	}

	protected Collection<String> getSearchValues(final Bookmark bookmark) {
		final Set<String> values = new HashSet<String>();
		values.add(bookmark.getUrl());
		values.add(bookmark.getName());
		values.add(bookmark.getDescription());
		values.addAll(bookmark.getKeywords());
		return values;
	}

	private final class Match {

		private final Bookmark bookmark;

		private final int counter;

		public Match(final Bookmark bookmark, final int counter) {
			this.bookmark = bookmark;
			this.counter = counter;
		}

		public Bookmark getBookmark() {
			return bookmark;
		}

		public int getCounter() {
			return counter;
		}
	}

	@Override
	public void createBookmark(final SessionIdentifier sessionIdentifier, final String url, final String name, final String description, final List<String> keywords,
			final boolean favorite) throws BookmarkServiceException, LoginRequiredException, ValidationException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);

			final BookmarkBean bookmark = bookmarkDao.create();
			bookmark.setId(createBookmarkIdentifier(sessionIdentifier, url));
			bookmark.setUrl(url);
			bookmark.setName(name);
			bookmark.setDescription(description);
			bookmark.setKeywords(keywords);
			bookmark.setOwner(userIdentifier);

			final ValidationResult errors = validationExecutor.validate(bookmark);
			if (errors.hasErrors()) {
				logger.warn("Bookmark " + errors.toString());
				throw new ValidationException(errors);
			}
			bookmarkDao.save(bookmark);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public BookmarkIdentifier createBookmarkIdentifier(final SessionIdentifier sessionIdentifier, final String url) throws BookmarkServiceException {
		try {
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);
			return new BookmarkIdentifier(userIdentifier.getId() + "-" + url);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public void deleteBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier) throws BookmarkServiceException, BookmarkDeletionException,
			PermissionDeniedException, LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);

			final BookmarkBean bookmark = bookmarkDao.load(bookmarkIdentifier);
			if (bookmark == null) {
				logger.info("delete bookmark failed, not found");
				throw new BookmarkDeletionException("bookmark not found");
			}
			authorizationService.expectUser(sessionIdentifier, bookmark.getOwner());
			bookmarkDao.delete(bookmark);
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthorizationServiceException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public void updateBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier, final String url, final String name, final String description,
			final List<String> keywords, final boolean favorite) throws BookmarkServiceException, LoginRequiredException, PermissionDeniedException, ValidationException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);

			logger.info("updateBookmark");

			final BookmarkBean bookmark = bookmarkDao.load(bookmarkIdentifier);
			authorizationService.expectUser(sessionIdentifier, bookmark.getOwner());

			deleteBookmark(sessionIdentifier, bookmarkIdentifier);
			createBookmark(sessionIdentifier, url, name, description, keywords, favorite);
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthorizationServiceException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final BookmarkDeletionException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
	}

	@Override
	public Bookmark getBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier) throws BookmarkServiceException, PermissionDeniedException,
			LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);

			logger.info("updateBookmark");
			final BookmarkBean bookmark = bookmarkDao.load(bookmarkIdentifier);
			authorizationService.expectUser(sessionIdentifier, bookmark.getOwner());
			return bookmark;
		}
		catch (final StorageException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthorizationServiceException e) {
			throw new BookmarkServiceException(e);
		}
		catch (final AuthenticationServiceException e) {
			throw new BookmarkServiceException(e);
		}
	}
}
