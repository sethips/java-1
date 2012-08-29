package de.benjaminborbe.bookmark.mock;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.bookmark.api.Bookmark;
import de.benjaminborbe.bookmark.api.BookmarkCreationException;
import de.benjaminborbe.bookmark.api.BookmarkDeletionException;
import de.benjaminborbe.bookmark.api.BookmarkIdentifier;
import de.benjaminborbe.bookmark.api.BookmarkService;
import de.benjaminborbe.bookmark.api.BookmarkServiceException;
import de.benjaminborbe.bookmark.api.BookmarkUpdateException;

@Singleton
public class BookmarkServiceMock implements BookmarkService {

	@Inject
	public BookmarkServiceMock() {
	}

	@Override
	public void createBookmark(final SessionIdentifier sessionIdentifier, final String url, final String name, final String description, final List<String> keywords, final boolean favorite)
			throws BookmarkServiceException, LoginRequiredException, BookmarkCreationException {
	}

	@Override
	public void updateBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier, final String url, final String name, final String description, final List<String> keywords,
			final boolean favorite) throws BookmarkServiceException, LoginRequiredException, BookmarkUpdateException {
	}

	@Override
	public void deleteBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier) throws BookmarkServiceException, BookmarkDeletionException {
	}

	@Override
	public List<Bookmark> getBookmarks(final SessionIdentifier sessionIdentifier) throws BookmarkServiceException {
		return null;
	}

	@Override
	public Bookmark getBookmark(final SessionIdentifier sessionIdentifier, final BookmarkIdentifier bookmarkIdentifier) throws BookmarkServiceException {
		return null;
	}

	@Override
	public List<Bookmark> searchBookmarks(final SessionIdentifier sessionIdentifier, final String[] words) throws BookmarkServiceException {
		return null;
	}

	@Override
	public List<Bookmark> getBookmarkFavorite(final SessionIdentifier sessionIdentifier) throws BookmarkServiceException {
		return null;
	}

	@Override
	public BookmarkIdentifier createBookmarkIdentifier(final SessionIdentifier sessionIdentifier, final String url) throws BookmarkServiceException {
		return null;
	}

}
