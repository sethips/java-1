package de.benjaminborbe.util.guice;

import java.util.Arrays;
import java.util.Collection;

import com.google.inject.Module;

import de.benjaminborbe.util.guice.UtilModule;
import de.benjaminborbe.tools.guice.Modules;
import de.benjaminborbe.tools.guice.ToolModule;
import de.benjaminborbe.tools.osgi.mock.PeaberryModuleMock;
import de.benjaminborbe.tools.osgi.mock.ServletModuleMock;

public class UtilModulesMock implements Modules {

	@Override
	public Collection<Module> getModules() {
		return Arrays.asList(new PeaberryModuleMock(), new ServletModuleMock(), new UtilOsgiModuleMock(),
				new UtilModule(), new ToolModule());
	}
}