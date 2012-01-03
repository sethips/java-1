package de.benjaminborbe.worktime.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;

import com.google.inject.Inject;

public class InOfficeCheck {

	private static final int TIMEOUT = 5 * 1000;

	private static final String TIMETRACKER_HOSTNAME = "timetracker.rp.seibert-media.net";

	private static final int TIMETRACKER_PORT = 443;

	private static final String PROJECTILE_HOSTNAME = "projectile.rp.seibert-media.net";

	private static final int PROJECTILE_PORT = 443;

	private final Logger logger;

	@Inject
	public InOfficeCheck(final Logger logger) {
		this.logger = logger;
	}

	protected boolean check(final String hostname, final int port) {
		Socket socket = null;
		try {
			socket = new Socket();
			final SocketAddress endpoint = new InetSocketAddress(hostname, port);
			socket.connect(endpoint, TIMEOUT);
			if (socket.isConnected()) {
				final String msg = "connected successful to " + hostname + ":" + port;
				logger.debug(msg);
				return true;
			}
			else {
				final String msg = "connecting failed to " + hostname + ":" + port;
				logger.warn(msg);
				return false;
			}
		}
		catch (final Exception e) {
			logger.warn("check tcp-connect to " + hostname + ":" + port + " failed");
			return false;
		}
		finally {
			try {
				if (socket != null)
					socket.close();
			}
			catch (final IOException e) {
				logger.debug("IOException while close socket", e);
			}
		}
	}

	public boolean check() {
		return check(PROJECTILE_HOSTNAME, PROJECTILE_PORT) && check(TIMETRACKER_HOSTNAME, TIMETRACKER_PORT);
	}
}
