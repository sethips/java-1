package de.benjaminborbe.selenium.core.service;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.AuthorizationServiceException;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.selenium.api.SeleniumConfiguration;
import de.benjaminborbe.selenium.api.SeleniumConfigurationIdentifier;
import de.benjaminborbe.selenium.api.SeleniumService;
import de.benjaminborbe.selenium.api.SeleniumServiceException;
import de.benjaminborbe.selenium.core.SeleniumCoreConstatns;
import de.benjaminborbe.selenium.core.configuration.SeleniumConfigurationRegistry;
import de.benjaminborbe.selenium.core.util.SeleniumCoreExecutor;
import de.benjaminborbe.selenium.core.util.SeleniumCoreRunner;
import de.benjaminborbe.tools.synchronize.RunOnlyOnceATime;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;

@Singleton
public class SeleniumCoreServiceImpl implements SeleniumService {

	private final Logger logger;

	private final SeleniumCoreExecutor seleniumCoreExecutor;

	private final AuthorizationService authorizationService;

	private final SeleniumConfigurationRegistry seleniumConfigurationRegistry;

	private final RunOnlyOnceATime runOnlyOnceATime;

	@Inject
	public SeleniumCoreServiceImpl(
		final Logger logger,
		final SeleniumCoreExecutor seleniumCoreExecutor,
		final AuthorizationService authorizationService,
		final SeleniumConfigurationRegistry seleniumConfigurationRegistry,
		final RunOnlyOnceATime runOnlyOnceATime
	) {
		this.logger = logger;
		this.seleniumCoreExecutor = seleniumCoreExecutor;
		this.authorizationService = authorizationService;
		this.seleniumConfigurationRegistry = seleniumConfigurationRegistry;
		this.runOnlyOnceATime = runOnlyOnceATime;
	}

	@Override
	public void expectPermission(final SessionIdentifier sessionIdentifier) throws PermissionDeniedException, LoginRequiredException, SeleniumServiceException {
		try {
			authorizationService.expectPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(SeleniumCoreConstatns.PERMISSION));
		} catch (final AuthorizationServiceException e) {
			throw new SeleniumServiceException(e);
		}
	}

	@Override
	public boolean hasPermission(final SessionIdentifier sessionIdentifier) throws SeleniumServiceException {
		try {
			return authorizationService.hasPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(SeleniumCoreConstatns.PERMISSION));
		} catch (final AuthorizationServiceException e) {
			throw new SeleniumServiceException(e);
		}
	}

	@Override
	public SeleniumConfigurationIdentifier createSeleniumConfigurationIdentifier(final String id) throws SeleniumServiceException {
		if (id != null && !id.trim().isEmpty()) {
			return new SeleniumConfigurationIdentifier(id.trim());
		}
		return null;
	}

	@Override
	public boolean execute(
		final SessionIdentifier sessionIdentifier, final SeleniumConfigurationIdentifier seleniumConfigurationIdentifier
	) throws SeleniumServiceException, LoginRequiredException, PermissionDeniedException {
		if (runOnlyOnceATime.run(new SeleniumCoreRunner(seleniumCoreExecutor, seleniumConfigurationIdentifier))) {
			logger.trace("execute " + seleniumConfigurationIdentifier + " started");
			return true;
		} else {
			logger.trace("execute " + seleniumConfigurationIdentifier + " skipped");
			return false;
		}
	}

	@Override
	public Collection<SeleniumConfiguration> getSeleniumConfigurations(final SessionIdentifier sessionIdentifier) throws SeleniumServiceException, LoginRequiredException, PermissionDeniedException {
		return new ArrayList<SeleniumConfiguration>(seleniumConfigurationRegistry.getAll());
	}
}
