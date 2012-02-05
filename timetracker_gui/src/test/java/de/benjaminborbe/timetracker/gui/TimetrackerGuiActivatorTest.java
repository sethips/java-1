package de.benjaminborbe.timetracker.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.servlet.Filter;

import org.junit.Test;

import com.google.inject.Injector;

import de.benjaminborbe.timetracker.gui.guice.TimetrackerGuiModulesMock;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;
import de.benjaminborbe.tools.osgi.BaseGuiceFilter;
import de.benjaminborbe.tools.osgi.mock.ExtHttpServiceMock;
import de.benjaminborbe.tools.osgi.test.BundleActivatorTestUtil;

public class TimetrackerGuiActivatorTest {

	@Test
	public void testInject() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new TimetrackerGuiModulesMock());
		final TimetrackerGuiActivator o = injector.getInstance(TimetrackerGuiActivator.class);
		assertNotNull(o);
	}

	@Test
	public void testServlets() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new TimetrackerGuiModulesMock());
		final TimetrackerGuiActivator o = new TimetrackerGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}

		};
		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(o);
		assertEquals(1, extHttpServiceMock.getRegisterServletCallCounter());
		for (final String path : Arrays.asList("/timetracker")) {
			assertTrue("no servlet for path " + path + " registered", extHttpServiceMock.hasServletPath(path));
		}
	}

	@Test
	public void testFilters() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new TimetrackerGuiModulesMock());
		final TimetrackerGuiActivator o = new TimetrackerGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}

		};

		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(o);
		assertEquals(1, extHttpServiceMock.getRegisterFilterCallCounter());

		for (final String path : Arrays.asList("/timetracker.*")) {
			assertTrue("no filter for path " + path + " registered", extHttpServiceMock.hasFilterPath(path));
		}

		final BaseGuiceFilter guiceFilter = injector.getInstance(BaseGuiceFilter.class);
		for (final Filter filter : Arrays.asList(guiceFilter)) {
			assertTrue("no filter " + filter + " registered", extHttpServiceMock.hasFilter(filter));
		}
	}

	@Test
	public void testResources() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new TimetrackerGuiModulesMock());
		final TimetrackerGuiActivator o = new TimetrackerGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}

		};
		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(o);
		assertEquals(0, extHttpServiceMock.getRegisterResourceCallCounter());
		// for (final String path : Arrays.asList("/", "/robots.txt")) {
		// assertTrue("no servlet for path " + path + " registered",
		// extHttpServiceMock.hasServletPath(path));
		// }
	}

}