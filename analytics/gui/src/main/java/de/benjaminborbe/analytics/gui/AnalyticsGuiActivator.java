package de.benjaminborbe.analytics.gui;

import de.benjaminborbe.analytics.gui.config.AnalyticsGuiConfig;
import de.benjaminborbe.analytics.gui.guice.AnalyticsGuiModules;
import de.benjaminborbe.analytics.gui.service.AnalyticsGuiNavigationEntry;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiLogWithoutReportServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportAddDataJsonServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportAddDataServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportAggregateServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportCreateServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportDeleteServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportListServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportRebuildServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportViewServlet;
import de.benjaminborbe.analytics.gui.servlet.AnalyticsGuiReportsRebuildServlet;
import de.benjaminborbe.configuration.api.ConfigurationDescription;
import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ResourceInfo;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.ServletInfo;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AnalyticsGuiActivator extends HttpBundleActivator {

	@Inject
	private AnalyticsGuiReportsRebuildServlet analyticsGuiReportsRebuildServlet;

	@Inject
	private AnalyticsGuiReportRebuildServlet analyticsGuiReportRebuildServlet;

	@Inject
	private AnalyticsGuiLogWithoutReportServlet analyticsGuiLogWithoutReportServlet;

	@Inject
	private AnalyticsGuiReportAddDataJsonServlet analyticsGuiReportAddDataJsonServlet;

	@Inject
	private AnalyticsGuiReportDeleteServlet analyticsGuiReportDeleteServlet;

	@Inject
	private AnalyticsGuiReportCreateServlet analyticsGuiReportCreateServlet;

	@Inject
	private AnalyticsGuiNavigationEntry analyticsGuiNavigationEntry;

	@Inject
	private AnalyticsGuiReportAddDataServlet analyticsGuiAddDataServlet;

	@Inject
	private AnalyticsGuiReportViewServlet analyticsGuiTableServlet;

	@Inject
	private AnalyticsGuiReportListServlet analyticsGuiServlet;

	@Inject
	private AnalyticsGuiReportAggregateServlet analyticsGuiReportAggregateServlet;

	@Inject
	private AnalyticsGuiConfig analyticsGuiConfig;

	public AnalyticsGuiActivator() {
		super(AnalyticsGuiConstants.NAME);
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new AnalyticsGuiModules(context);
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(analyticsGuiServlet, AnalyticsGuiConstants.URL_REPORT_LIST));
		result.add(new ServletInfo(analyticsGuiTableServlet, AnalyticsGuiConstants.URL_REPORT_VIEW));
		result.add(new ServletInfo(analyticsGuiReportAddDataJsonServlet, AnalyticsGuiConstants.URL_REPORT_ADD_VALUE));
		result.add(new ServletInfo(analyticsGuiAddDataServlet, AnalyticsGuiConstants.URL_REPORT_ADD_DATA));
		result.add(new ServletInfo(analyticsGuiReportCreateServlet, AnalyticsGuiConstants.URL_REPORT_CREATE));
		result.add(new ServletInfo(analyticsGuiReportDeleteServlet, AnalyticsGuiConstants.URL_REPORT_DELETE));
		result.add(new ServletInfo(analyticsGuiReportAggregateServlet, AnalyticsGuiConstants.URL_REPORT_AGGREGATE));
		result.add(new ServletInfo(analyticsGuiLogWithoutReportServlet, AnalyticsGuiConstants.URL_LOG_WITHOUT_REPORT));
		result.add(new ServletInfo(analyticsGuiReportRebuildServlet, AnalyticsGuiConstants.URL_REPORT_REBUILD));
		result.add(new ServletInfo(analyticsGuiReportsRebuildServlet, AnalyticsGuiConstants.URL_REPORT_REBUILD_ALL));
		return result;
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(NavigationEntry.class, analyticsGuiNavigationEntry));
		for (final ConfigurationDescription configuration : analyticsGuiConfig.getConfigurations()) {
			result.add(new ServiceInfo(ConfigurationDescription.class, configuration, configuration.getName()));
		}
		return result;
	}

	@Override
	protected Collection<ResourceInfo> getResouceInfos() {
		final Set<ResourceInfo> result = new HashSet<ResourceInfo>(super.getResouceInfos());
		result.add(new ResourceInfo("/js", "js"));
		result.add(new ResourceInfo("/css", "css"));
		return result;
	}
}
