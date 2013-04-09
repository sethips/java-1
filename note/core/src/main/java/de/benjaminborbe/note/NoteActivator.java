package de.benjaminborbe.note;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;

import com.google.inject.Inject;

import de.benjaminborbe.note.api.NoteService;
import de.benjaminborbe.note.guice.NoteModules;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.osgi.BaseBundleActivator;
import de.benjaminborbe.tools.osgi.ServiceInfo;

public class NoteActivator extends BaseBundleActivator {

	@Inject
	private NoteService noteService;

	@Override
	protected Modules getModules(final BundleContext context) {
		return new NoteModules(context);
	}

	@Override
	public Collection<ServiceInfo> getServiceInfos() {
		final Set<ServiceInfo> result = new HashSet<ServiceInfo>(super.getServiceInfos());
		result.add(new ServiceInfo(NoteService.class, noteService));
		return result;
	}

}