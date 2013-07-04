package de.benjaminborbe.wiki.dao;

import com.google.inject.Injector;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;
import de.benjaminborbe.wiki.guice.WikiModulesMock;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WikiPageDaoIntegrationTest {

	@Test
	public void testInject() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new WikiModulesMock());
		assertNotNull(injector.getInstance(WikiPageDao.class));
		assertEquals(WikiPageDaoImpl.class, injector.getInstance(WikiPageDao.class).getClass());
	}
}
