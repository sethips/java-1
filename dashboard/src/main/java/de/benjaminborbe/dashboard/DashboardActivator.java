package de.benjaminborbe.dashboard;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.inject.Inject;

import de.benjaminborbe.dashboard.api.DashboardContentWidget;
import de.benjaminborbe.dashboard.api.DashboardWidget;
import de.benjaminborbe.dashboard.guice.DashboardModules;
import de.benjaminborbe.dashboard.service.DashboardWidgetRegistry;
import de.benjaminborbe.dashboard.service.DashboardWidgetServiceTracker;
import de.benjaminborbe.dashboard.servlet.DashboardServlet;
import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.navigation.api.NavigationEntryImpl;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ResourceInfo;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.ServletInfo;

public class DashboardActivator extends HttpBundleActivator {

	@Inject
	private DashboardWidget dashboardWidget;

	@Inject
	private DashboardWidgetRegistry dashboardWidgetRegistry;

	@Inject
	private DashboardServlet dashboardServlet;

	public DashboardActivator() {
		super("dashboard");
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new DashboardModules(context);
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(dashboardServlet, "/"));
		return result;
	}

	@Override
	protected Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(DashboardWidget.class, dashboardWidget));
		result.add(new ServiceInfo(NavigationEntry.class, new NavigationEntryImpl("Dashboard", "/bb/dashboard")));
		return result;
	}

	@Override
	protected Collection<ServiceTracker> getServiceTrackers(final BundleContext context) {
		final Set<ServiceTracker> serviceTrackers = new HashSet<ServiceTracker>(super.getServiceTrackers(context));
		serviceTrackers.add(new DashboardWidgetServiceTracker(dashboardWidgetRegistry, context, DashboardContentWidget.class));
		return serviceTrackers;
	}

	@Override
	protected Collection<ResourceInfo> getResouceInfos() {
		final Set<ResourceInfo> result = new HashSet<ResourceInfo>(super.getResouceInfos());
		result.add(new ResourceInfo("/css", "css"));
		result.add(new ResourceInfo("/js", "js"));
		return result;
	}
}
