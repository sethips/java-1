package de.benjaminborbe.microblog.util;

import de.benjaminborbe.microblog.api.MicroblogConversationIdentifier;
import de.benjaminborbe.microblog.api.MicroblogPost;
import de.benjaminborbe.microblog.api.MicroblogPostListener;
import de.benjaminborbe.microblog.config.MicroblogConfig;
import de.benjaminborbe.microblog.connector.MicroblogConnectorException;
import de.benjaminborbe.microblog.conversation.MicroblogConversationFinder;
import de.benjaminborbe.microblog.conversation.MicroblogConversationNotifier;
import de.benjaminborbe.microblog.conversation.MicroblogConversationNotifierException;
import de.benjaminborbe.microblog.post.MicroblogPostNotifier;
import de.benjaminborbe.microblog.post.MicroblogPostNotifierException;
import de.benjaminborbe.tools.util.ParseException;
import org.slf4j.Logger;

import javax.inject.Inject;

public class MicroblogPostListenerMailer implements MicroblogPostListener {

	private final Logger logger;

	private final MicroblogPostNotifier microblogPostMailer;

	private final MicroblogConversationFinder microblogConversationFinder;

	private final MicroblogConversationNotifier microblogConversationMailer;

	private final MicroblogConfig microblogConfig;

	@Inject
	public MicroblogPostListenerMailer(
		final Logger logger,
		final MicroblogConfig microblogConfig,
		final MicroblogPostNotifier microblogPostMailer,
		final MicroblogConversationFinder microblogConversationFinder,
		final MicroblogConversationNotifier microblogConversationMailer
	) {
		this.logger = logger;
		this.microblogConfig = microblogConfig;
		this.microblogPostMailer = microblogPostMailer;
		this.microblogConversationFinder = microblogConversationFinder;
		this.microblogConversationMailer = microblogConversationMailer;
	}

	@Override
	public void onNewPost(final MicroblogPost microblogPost) {
		try {
			if (microblogConfig.isMailEnabled()) {
				final MicroblogConversationIdentifier microblogConversationIdentifier = microblogConversationFinder.findIdentifier(microblogPost.getId());
				logger.trace("found microblogConversationIdentifier = " + microblogConversationIdentifier);
				if (microblogConversationIdentifier != null) {
					logger.trace("mailConversation: " + microblogConversationIdentifier);
					microblogConversationMailer.mailConversation(microblogConversationIdentifier);
				} else {
					logger.trace("mailPost: " + microblogPost.getId());
					microblogPostMailer.mailPost(microblogPost);
				}
			}
		} catch (final MicroblogPostNotifierException e) {
			logger.debug(e.getClass().getName(), e);
		} catch (MicroblogConnectorException e) {
			logger.debug(e.getClass().getName(), e);
		} catch (MicroblogConversationNotifierException e) {
			logger.debug(e.getClass().getName(), e);
		} catch (ParseException e) {
			logger.debug(e.getClass().getName(), e);
		}
	}
}
