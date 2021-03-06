package de.benjaminborbe.vnc.core.guice;

import com.glavsoft.viewer.Viewer;
import com.glavsoft.viewer.ViewerHeadless;
import com.google.inject.AbstractModule;
import de.benjaminborbe.tools.log.LoggerSlf4Provider;
import de.benjaminborbe.vnc.api.VncKeyParser;
import de.benjaminborbe.vnc.api.VncScreenContent;
import de.benjaminborbe.vnc.api.VncService;
import de.benjaminborbe.vnc.core.service.VncServiceImpl;
import de.benjaminborbe.vnc.core.util.VncKeyParserImpl;
import de.benjaminborbe.vnc.core.util.VncScreenContentImpl;
import org.slf4j.Logger;

import javax.inject.Singleton;

public class VncModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(VncKeyParser.class).to(VncKeyParserImpl.class).in(Singleton.class);
		bind(Viewer.class).to(ViewerHeadless.class);
		bind(VncScreenContent.class).to(VncScreenContentImpl.class).in(Singleton.class);
		bind(VncService.class).to(VncServiceImpl.class).in(Singleton.class);
		bind(Logger.class).toProvider(LoggerSlf4Provider.class).in(Singleton.class);
	}
}
