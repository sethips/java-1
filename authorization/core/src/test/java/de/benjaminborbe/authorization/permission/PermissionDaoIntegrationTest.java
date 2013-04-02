package de.benjaminborbe.authorization.permission;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.inject.Injector;

import de.benjaminborbe.authorization.dao.PermissionDao;
import de.benjaminborbe.authorization.guice.AuthorizationModulesMock;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;

public class PermissionDaoIntegrationTest {

	@Test
	public void testInject() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AuthorizationModulesMock());
		assertNotNull(injector.getInstance(PermissionDao.class));
	}
}