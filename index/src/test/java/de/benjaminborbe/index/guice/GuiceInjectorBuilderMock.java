package de.benjaminborbe.index.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceInjectorBuilderMock {

	private GuiceInjectorBuilderMock() {
	}

	public static Injector getInjector() {
		return Guice.createInjector(new PeaberryModuleMock(), new ServletModuleMock(), new IndexOsgiModuleMock(),
				new IndexModule());
	}
}
