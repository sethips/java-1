package de.benjaminborbe.vnc.core;

import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.BaseBundleActivator;
import de.benjaminborbe.tools.osgi.ServiceInfo;
import de.benjaminborbe.vnc.api.VncService;
import de.benjaminborbe.vnc.core.guice.VncModules;
import de.benjaminborbe.vnc.core.xmpp.VncXmppCommandRegistry;
import de.benjaminborbe.xmpp.api.XmppCommand;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VncActivator extends BaseBundleActivator {

	@Inject
	private VncService vncService;

	@Inject
	private VncXmppCommandRegistry vncXmppCommandRegistry;

	@Override
	protected Modules getModules(final BundleContext context) {
		return new VncModules(context);
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(VncService.class, vncService));
		for (final XmppCommand command : vncXmppCommandRegistry.getAll()) {
			result.add(new ServiceInfo(XmppCommand.class, command, command.getName()));
		}
		return result;
	}

	@Override
	protected void onStopped() throws Exception {
		super.onStopped();
		try {
			vncService.disconnectForce();
		} catch (final Exception e) {
			// nop
		}
	}

}
