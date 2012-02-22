package de.benjaminborbe.microblog.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

import de.benjaminborbe.dashboard.api.DashboardContentWidget;
import de.benjaminborbe.microblog.gui.guice.MicroblogGuiModules;
import de.benjaminborbe.microblog.gui.service.MicroblogGuiDashboardWidget;
import de.benjaminborbe.microblog.gui.servlet.MicroblogGuiSendServlet;
import de.benjaminborbe.microblog.gui.servlet.MicroblogGuiServlet;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.ServletInfo;

public class MicroblogGuiActivator extends HttpBundleActivator {

	@Inject
	private MicroblogGuiServlet microblogServlet;

	@Inject
	private MicroblogGuiDashboardWidget microblogDashboardWidget;

	@Inject
	private MicroblogGuiSendServlet microblogSendServlet;

	public MicroblogGuiActivator() {
		super("microblog");
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new MicroblogGuiModules(context);
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(microblogServlet, "/"));
		result.add(new ServletInfo(microblogSendServlet, "/send"));
		return result;
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(DashboardContentWidget.class, microblogDashboardWidget, microblogDashboardWidget.getClass().getName()));
		return result;
	}

}
