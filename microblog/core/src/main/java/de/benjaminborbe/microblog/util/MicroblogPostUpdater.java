package de.benjaminborbe.microblog.util;

import de.benjaminborbe.microblog.api.MicroblogPost;
import de.benjaminborbe.microblog.api.MicroblogPostIdentifier;
import de.benjaminborbe.microblog.api.MicroblogPostListener;
import de.benjaminborbe.microblog.connector.MicroblogConnector;
import de.benjaminborbe.microblog.connector.MicroblogConnectorException;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;

public class MicroblogPostUpdater {

	private final MicroblogPostListenerRegistry microblogPostListenerRegistry;

	private final MicroblogConnector microblogConnector;

	private final Logger logger;

	@Inject
	public MicroblogPostUpdater(
		final Logger logger,
		final MicroblogPostListenerRegistry microblogPostListenerRegistry,
		final MicroblogConnector microblogConnector
	) {
		this.logger = logger;
		this.microblogPostListenerRegistry = microblogPostListenerRegistry;
		this.microblogConnector = microblogConnector;
	}

	public void update(final MicroblogPostIdentifier microblogPostIdentifier) throws MicroblogConnectorException {
		logger.trace("fetch post: " + microblogPostIdentifier);
		final MicroblogPost microblogPost = microblogConnector.getPost(microblogPostIdentifier);
		final Collection<MicroblogPostListener> listeners = microblogPostListenerRegistry.getAll();
		logger.trace("notify " + listeners.size() + " listners");
		for (final MicroblogPostListener listener : listeners) {
			try {
				logger.debug("onNewPost: " + listener.getClass().getName());
				listener.onNewPost(microblogPost);
			} catch (final Exception e) {
				logger.warn(e.getClass().getName(), e);
			}
		}
	}
}
