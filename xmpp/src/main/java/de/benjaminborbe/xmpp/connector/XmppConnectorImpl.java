package de.benjaminborbe.xmpp.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.xmpp.config.XmppConfig;

@Singleton
public class XmppConnectorImpl implements XmppConnector {

	private final XmppConfig xmppConfig;

	private final Logger logger;

	private XMPPConnection connection;

	private final XmppChatManagerListener xmppChatManagerListener;

	@Inject
	public XmppConnectorImpl(final Logger logger, final XmppConfig xmppConfig, final XmppChatManagerListener xmppChatManagerListener) {
		this.logger = logger;
		this.xmppConfig = xmppConfig;
		this.xmppChatManagerListener = xmppChatManagerListener;

	}

	@Override
	public synchronized void connect() throws XmppConnectorException {
		try {
			if (connection != null) {
				logger.warn("already connected");
				return;
			}
			final ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(xmppConfig.getServerHost(), xmppConfig.getServerPort(), "gmail.com");
			// connectionConfiguration.setCompressionEnabled(false);
			// connectionConfiguration.setSASLAuthenticationEnabled(true);
			// connectionConfiguration.setSecurityMode(SecurityMode.disabled);

			final XMPPConnection connection = new XMPPConnection(connectionConfiguration);
			connection.connect();

			connection.addConnectionListener(new MyConnectionListener());

			logger.debug("isConnected: " + connection.isConnected());
			connection.login(xmppConfig.getUsername(), xmppConfig.getPassword());
			logger.debug("isAuthenticated: " + connection.isAuthenticated());

			// set status
			final Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);

			final ChatManager chatManager = connection.getChatManager();
			chatManager.addChatListener(xmppChatManagerListener);

			final Roster roster = connection.getRoster();
			roster.addRosterListener(new MyRosterListener());

			this.connection = connection;
		}
		catch (final XMPPException e) {
			throw new XmppConnectorException(e);
		}
	}

	@Override
	public XmppUser getMe() {

		return new XmppUser(connection.getUser());
	}

	@Override
	public synchronized void disconnect() {
		if (connection != null) {
			connection.disconnect();
			connection = null;
		}
	}

	// @Override
	// public void sendMessage(final XmppUser user, final String message) throws
	// XmppConnectorException {
	// try {
	// final ChatManager chatManager = connection.getChatManager();
	// final Chat chat = chatManager.createChat(user.getUid(), messageListener);
	// chat.sendMessage(message);
	// }
	// catch (final XMPPException e) {
	// throw new XmppConnectorException(e);
	// }
	// }

	public XmppUser getUserByName(final String name) {
		final List<XmppUser> users = getUsers();
		for (final XmppUser user : users) {
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	@Override
	public List<XmppUser> getUsers() {
		final List<XmppUser> users = new ArrayList<XmppUser>();
		final Roster roster = connection.getRoster();
		final Collection<RosterEntry> entries = roster.getEntries();
		for (final RosterEntry entry : entries) {
			users.add(new XmppUser(entry));
			logger.debug(String.format("Buddy: " + entry.getName() + " Status: " + entry.getStatus() + " id: " + entry.getUser()));
		}
		return users;
	}

	private final class MyRosterListener implements RosterListener {

		@Override
		public void presenceChanged(final Presence presence) {
			logger.debug("presenceChanged");
		}

		@Override
		public void entriesUpdated(final Collection<String> addresses) {
			logger.debug("entriesUpdated");
		}

		@Override
		public void entriesDeleted(final Collection<String> addresses) {
			logger.debug("entriesDeleted");
		}

		@Override
		public void entriesAdded(final Collection<String> addresses) {
			logger.debug("entriesAdded");
		}
	}

	private final class MyConnectionListener implements ConnectionListener {

		@Override
		public void reconnectionSuccessful() {
			logger.debug("reconnectionSuccessful");
		}

		@Override
		public void reconnectionFailed(final Exception e) {
			logger.debug("reconnectionFailed");
		}

		@Override
		public void reconnectingIn(final int seconds) {
			logger.debug("reconnectingIn");
		}

		@Override
		public void connectionClosedOnError(final Exception e) {
			logger.debug("connectionClosedOnError");
		}

		@Override
		public void connectionClosed() {
			logger.debug("connectionClosed");
		}
	}

}