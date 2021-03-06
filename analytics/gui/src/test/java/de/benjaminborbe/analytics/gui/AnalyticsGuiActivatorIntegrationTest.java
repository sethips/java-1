package de.benjaminborbe.analytics.gui;

import com.google.inject.Injector;
import de.benjaminborbe.analytics.gui.guice.AnalyticsGuiModulesMock;
import de.benjaminborbe.configuration.api.ConfigurationDescription;
import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;
import de.benjaminborbe.tools.osgi.BaseGuiceFilter;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.mock.ExtHttpServiceMock;
import de.benjaminborbe.tools.osgi.test.BundleActivatorTestUtil;
import org.junit.Test;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AnalyticsGuiActivatorIntegrationTest {

	@Test
	public void testInject() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AnalyticsGuiModulesMock());
		final AnalyticsGuiActivator activator = injector.getInstance(AnalyticsGuiActivator.class);
		assertNotNull(activator);
	}

	@Test
	public void testServlets() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AnalyticsGuiModulesMock());
		final AnalyticsGuiActivator activator = new AnalyticsGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}

		};
		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(activator);
		final List<String> paths = new ArrayList<String>();
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_LIST);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_VIEW);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_CREATE);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_DELETE);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_ADD_DATA);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_ADD_VALUE);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_AGGREGATE);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_LOG_WITHOUT_REPORT);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_REBUILD);
		paths.add("/" + AnalyticsGuiConstants.NAME + AnalyticsGuiConstants.URL_REPORT_REBUILD_ALL);
		assertEquals(paths.size(), extHttpServiceMock.getRegisterServletCallCounter());
		for (final String path : paths) {
			assertTrue("no servlet for path " + path + " registered", extHttpServiceMock.hasServletPath(path));
		}
	}

	@Test
	public void testFilters() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AnalyticsGuiModulesMock());
		final AnalyticsGuiActivator activator = new AnalyticsGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}
		};
		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(activator);
		final List<String> paths = Arrays.asList("/analytics.*");
		assertEquals(paths.size(), extHttpServiceMock.getRegisterFilterCallCounter());
		for (final String path : paths) {
			assertTrue("no filter for path " + path + " registered", extHttpServiceMock.hasFilterPath(path));
		}
		final BaseGuiceFilter guiceFilter = injector.getInstance(BaseGuiceFilter.class);
		for (final Filter filter : Arrays.asList(guiceFilter)) {
			assertTrue("no filter " + filter + " registered", extHttpServiceMock.hasFilter(filter));
		}
	}

	@Test
	public void testResources() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AnalyticsGuiModulesMock());
		final AnalyticsGuiActivator activator = new AnalyticsGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}
		};
		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		final ExtHttpServiceMock extHttpServiceMock = bundleActivatorTestUtil.startBundle(activator);
		final List<String> paths = new ArrayList<String>();
		paths.add("/" + AnalyticsGuiConstants.NAME + "/js");
		paths.add("/" + AnalyticsGuiConstants.NAME + "/css");
		assertEquals(paths.size(), extHttpServiceMock.getRegisterResourceCallCounter());
		for (final String path : paths) {
			assertTrue("no resource for path " + path + " registered", extHttpServiceMock.hasResource(path));
		}
	}

	@Test
	public void testServices() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new AnalyticsGuiModulesMock());
		final AnalyticsGuiActivator activator = new AnalyticsGuiActivator() {

			@Override
			public Injector getInjector() {
				return injector;
			}
		};

		final BundleActivatorTestUtil bundleActivatorTestUtil = new BundleActivatorTestUtil();
		bundleActivatorTestUtil.startBundle(activator);

		final Collection<ServiceInfo> serviceInfos = activator.getServiceInfos();
		final List<String> names = new ArrayList<String>();
		names.add(NavigationEntry.class.getName());
		names.add(ConfigurationDescription.class.getName());
		assertEquals(names.size(), serviceInfos.size());
		for (final String name : names) {
			boolean match = false;
			for (final ServiceInfo serviceInfo : serviceInfos) {
				if (name.equals(serviceInfo.getName())) {
					match = true;
				}
			}
			assertTrue("no service with name: " + name + " found", match);
		}
	}
}
