package de.benjaminborbe.index.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

import de.benjaminborbe.index.gui.guice.IndexGuiModules;
import de.benjaminborbe.index.gui.servlet.IndexGuiServlet;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.HttpBundleActivator;
import de.benjaminborbe.tools.osgi.ServletInfo;

public class IndexGuiActivator extends HttpBundleActivator {

	@Inject
	private IndexGuiServlet indexGuiServlet;

	public IndexGuiActivator() {
		super(IndexGuiConstants.NAME);
	}

	@Override
	protected Modules getModules(final BundleContext context) {
		return new IndexGuiModules(context);
	}

	@Override
	protected Collection<ServletInfo> getServletInfos() {
		final Set<ServletInfo> result = new HashSet<ServletInfo>(super.getServletInfos());
		result.add(new ServletInfo(indexGuiServlet, "/"));
		return result;
	}

}
