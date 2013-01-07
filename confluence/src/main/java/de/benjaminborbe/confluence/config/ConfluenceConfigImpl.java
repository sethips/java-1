package de.benjaminborbe.confluence.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.google.inject.Inject;

import de.benjaminborbe.configuration.api.ConfigurationDescription;
import de.benjaminborbe.configuration.api.ConfigurationService;
import de.benjaminborbe.configuration.tools.ConfigurationBase;
import de.benjaminborbe.configuration.tools.ConfigurationDescriptionBoolean;
import de.benjaminborbe.configuration.tools.ConfigurationDescriptionInteger;
import de.benjaminborbe.tools.util.ParseUtil;

public class ConfluenceConfigImpl extends ConfigurationBase implements ConfluenceConfig {

	private final ConfigurationDescriptionInteger refreshLimit = new ConfigurationDescriptionInteger(null, "ConfluenceRefreshLimit", "Confluence Refresh Limit");

	private final ConfigurationDescriptionBoolean cronEnabled = new ConfigurationDescriptionBoolean(false, "ConfluenceCronEnabled", "Confluence Cron Enabled");

	@Inject
	public ConfluenceConfigImpl(final Logger logger, final ConfigurationService configurationService, final ParseUtil parseUtil) {
		super(logger, configurationService, parseUtil);
	}

	@Override
	public Collection<ConfigurationDescription> getConfigurations() {
		final Set<ConfigurationDescription> result = new HashSet<ConfigurationDescription>();
		result.add(refreshLimit);
		result.add(cronEnabled);
		return result;
	}

	@Override
	public Integer getRefreshLimit() {
		return getValueInteger(refreshLimit);
	}

	@Override
	public Boolean getCronEnabled() {
		return getValueBoolean(cronEnabled);
	}

}