package de.benjaminborbe.vaadin.gui.servlet;

import com.google.inject.Provider;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import de.benjaminborbe.vaadin.gui.VaadinGuiApplication;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Singleton
public class VaadinGuiApplicationServlet extends AbstractApplicationServlet {

	private static final long serialVersionUID = 3196753149929364590L;

	private final Logger logger;

	private final Provider<VaadinGuiApplication> vaadinGuiApplicationProvider;

	@Inject
	public VaadinGuiApplicationServlet(final Logger logger, final Provider<VaadinGuiApplication> myApplicationProvider) {
		this.logger = logger;
		this.vaadinGuiApplicationProvider = myApplicationProvider;
	}

	@Override
	protected Application getNewApplication(final HttpServletRequest request) throws ServletException {
		logger.trace("getNewApplication");
		return vaadinGuiApplicationProvider.get();
	}

	@Override
	protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
		logger.trace("getApplicationClass");
		return VaadinGuiApplication.class;
	}

	@Override
	public boolean isProductionMode() {
		return true;
	}

}
