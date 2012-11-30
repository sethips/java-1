package de.benjaminborbe.search.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.inject.Inject;

import de.benjaminborbe.dashboard.api.DashboardContentWidget;
import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.navigation.api.NavigationEntryImpl;
import de.benjaminborbe.search.api.SearchSpecial;
import de.benjaminborbe.search.api.SearchWidget;
import de.benjaminborbe.search.gui.guice.SearchGuiModules;
import de.benjaminborbe.search.gui.service.SearchGuiDashboardWidget;
import de.benjaminborbe.search.gui.service.SearchGuiSpecialSearchRegistry;
import de.benjaminborbe.search.gui.service.SearchGuiSpecialSearchServiceTracker;
import de.benjaminborbe.search.gui.servlet.SearchGuiOsdServlet;
import de.benjaminborbe.search.gui.servlet.SearchGuiServiceComponentsServlet;
import de.benjaminborbe.search.gui.servlet.SearchGuiServlet;
import de.benjaminborbe.search.gui.servlet.SearchGuiSuggestServlet;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ResourceInfo;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.ServletInfo;

public class SearchGuiActivator extends HttpBundleActivator {

	@Inject
	private SearchGuiOsdServlet searchOsdServlet;

	@Inject
	private SearchGuiServlet searchServlet;

	@Inject
	private SearchGuiServiceComponentsServlet searchServiceComponentsServlet;

	@Inject
	private SearchGuiSuggestServlet searchSuggestServlet;

	@Inject
	private SearchGuiDashboardWidget searchDashboardWidget;

	@Inject
	private SearchGuiSpecialSearchRegistry searchGuiSpecialSearchRegistry;

	@Inject
	private SearchWidget searchWidget;

	public SearchGuiActivator() {
		super(SearchGuiConstants.NAME);
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new SearchGuiModules(context);
	}

	@Override
	protected Collection<ResourceInfo> getResouceInfos() {
		final Set<ResourceInfo> result = new HashSet<ResourceInfo>(super.getResouceInfos());
		result.add(new ResourceInfo("/css", "css"));
		return result;
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(searchServlet, "/"));
		result.add(new ServletInfo(searchSuggestServlet, "/suggest"));
		result.add(new ServletInfo(searchServiceComponentsServlet, "/component"));
		result.add(new ServletInfo(searchOsdServlet, "/osd.xml"));
		return result;
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(DashboardContentWidget.class, searchDashboardWidget, searchDashboardWidget.getClass().getName()));
		result.add(new ServiceInfo(SearchWidget.class, searchWidget));
		result.add(new ServiceInfo(NavigationEntry.class, new NavigationEntryImpl("Search", "/bb/" + SearchGuiConstants.NAME)));
		return result;
	}

	@Override
	public Collection<ServiceTracker> getServiceTrackers(final BundleContext context) {
		final Set<ServiceTracker> serviceTrackers = new HashSet<ServiceTracker>(super.getServiceTrackers(context));
		serviceTrackers.add(new SearchGuiSpecialSearchServiceTracker(searchGuiSpecialSearchRegistry, context, SearchSpecial.class));
		return serviceTrackers;
	}
}
