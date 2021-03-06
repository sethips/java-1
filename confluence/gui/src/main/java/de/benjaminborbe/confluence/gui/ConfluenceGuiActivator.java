package de.benjaminborbe.confluence.gui;

import de.benjaminborbe.confluence.gui.guice.ConfluenceGuiModules;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiExpireAllServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiInstanceCreateServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiInstanceDeleteServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiInstanceListServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiInstanceUpdateServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiRefreshPageServlet;
import de.benjaminborbe.confluence.gui.servlet.ConfluenceGuiRefreshPagesServlet;
import de.benjaminborbe.confluence.gui.util.ConfluenceGuiNavigationEntry;
import de.benjaminborbe.navigation.api.NavigationEntry;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.tools.osgi.ServletInfo;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConfluenceGuiActivator extends HttpBundleActivator {

	@Inject
	private ConfluenceGuiRefreshPageServlet confluenceGuiRefreshPageServlet;

	@Inject
	private ConfluenceGuiExpireAllServlet confluenceGuiExpireAllServlet;

	@Inject
	private ConfluenceGuiRefreshPagesServlet confluenceGuiRefreshPagesServlet;

	@Inject
	private ConfluenceGuiInstanceCreateServlet confluenceGuiInstanceCreateServlet;

	@Inject
	private ConfluenceGuiInstanceDeleteServlet confluenceGuiInstanceDeleteServlet;

	@Inject
	private ConfluenceGuiInstanceListServlet confluenceGuiInstanceListServlet;

	@Inject
	private ConfluenceGuiInstanceUpdateServlet confluenceGuiInstanceUpdateServlet;

	@Inject
	private ConfluenceGuiNavigationEntry confluenceGuiNavigationEntry;

	public ConfluenceGuiActivator() {
		super(ConfluenceGuiConstants.NAME);
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new ConfluenceGuiModules(context);
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(confluenceGuiInstanceCreateServlet, ConfluenceGuiConstants.URL_INSTANCE_CREATE));
		result.add(new ServletInfo(confluenceGuiInstanceListServlet, ConfluenceGuiConstants.URL_INSTANCE_LIST));
		result.add(new ServletInfo(confluenceGuiInstanceDeleteServlet, ConfluenceGuiConstants.URL_INSTANCE_DELETE));
		result.add(new ServletInfo(confluenceGuiInstanceUpdateServlet, ConfluenceGuiConstants.URL_INSTANCE_UPDATE));
		result.add(new ServletInfo(confluenceGuiRefreshPagesServlet, ConfluenceGuiConstants.URL_REFRESH_PAGES));
		result.add(new ServletInfo(confluenceGuiRefreshPageServlet, ConfluenceGuiConstants.URL_REFRESH_PAGE));
		result.add(new ServletInfo(confluenceGuiExpireAllServlet, ConfluenceGuiConstants.URL_EXPIRE_ALL));
		return result;
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(NavigationEntry.class, confluenceGuiNavigationEntry));
		return result;
	}

}
