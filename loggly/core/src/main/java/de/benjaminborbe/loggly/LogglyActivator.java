package de.benjaminborbe.loggly;

import com.google.inject.Provider;
import de.benjaminborbe.loggly.api.LogglyService;
import de.benjaminborbe.loggly.guice.LogglyModules;
import de.benjaminborbe.loggly.util.LogglyConnector;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.BaseBundleActivator;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LogglyActivator extends BaseBundleActivator {

	@Inject
	private LogglyService logglyService;

	@Inject
	private Provider<LogglyConnector> logglyConnectorProvider;

	@Override
	protected Modules getModules(final BundleContext context) {
		return new LogglyModules(context);
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(LogglyService.class, logglyService));
		return result;
	}

	@Override
	protected void onStopped() throws Exception {
		try {
			logglyConnectorProvider.get().close();
		} catch (final Exception e) {
			// nop
		}
		super.onStopped();
	}

}
