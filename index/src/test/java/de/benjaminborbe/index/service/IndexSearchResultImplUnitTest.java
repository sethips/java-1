package de.benjaminborbe.index.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.benjaminborbe.index.api.IndexSearchResult;

public class IndexSearchResultImplUnitTest {

	@Test
	public void testEquals() throws Exception {
		final IndexSearchResult a = new IndexSearchResultImpl(null, "http://example.com/a", null, null);
		final IndexSearchResult b = new IndexSearchResultImpl(null, "http://example.com/b", null, null);
		final IndexSearchResult c = new IndexSearchResultImpl(null, "http://example.com/b", null, null);
		assertThat(a.equals(b), is(false));
		assertThat(b.equals(c), is(true));
	}
}
