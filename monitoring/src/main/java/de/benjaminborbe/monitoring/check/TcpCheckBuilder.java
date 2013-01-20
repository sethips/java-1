package de.benjaminborbe.monitoring.check;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.monitoring.api.MonitoringCheck;

@Singleton
public class TcpCheckBuilder {

	private final Logger logger;

	@Inject
	public TcpCheckBuilder(final Logger logger) {
		this.logger = logger;
	}

	public MonitoringCheck buildCheck(final String name, final String hostname, final int port) {
		return new TcpCheck(logger, name, hostname, port);
	}

}
