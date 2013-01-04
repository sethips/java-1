package de.benjaminborbe.microblog.config;

import java.util.Collection;

import de.benjaminborbe.configuration.api.ConfigurationDescription;

public interface MicroblogConfig {

	boolean isXmppEnabled();

	boolean isMailEnabled();

	Collection<ConfigurationDescription> getConfigurations();
}
