package de.benjaminborbe.authentication.verifycredential;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.authentication.config.AuthenticationConfig;
import de.benjaminborbe.authentication.ldap.LdapConnector;
import de.benjaminborbe.authentication.ldap.LdapException;

@Singleton
public class AuthenticationVerifyCredentialLdap implements AuthenticationVerifyCredential {

	private final LdapConnector ldapConnector;

	private final Logger logger;

	private final AuthenticationConfig authenticationConfig;

	@Inject
	public AuthenticationVerifyCredentialLdap(final Logger logger, final LdapConnector ldapConnector, final AuthenticationConfig authenticationConfig) {
		this.logger = logger;
		this.ldapConnector = ldapConnector;
		this.authenticationConfig = authenticationConfig;
	}

	@Override
	public boolean verifyCredential(final UserIdentifier userIdentifier, final String password) throws AuthenticationServiceException {
		try {
			final boolean result = ldapConnector.verify(userIdentifier.getId(), password);
			logger.debug("verifyCredential for user " + userIdentifier + " => " + result);
			return result;
		}
		catch (final LdapException e) {
			logger.debug(e.getClass().getName(), e);
			throw new AuthenticationServiceException(e);
		}
	}

	@Override
	public String getFullname(final UserIdentifier userIdentifier) throws AuthenticationServiceException {
		try {
			return ldapConnector.getFullname(userIdentifier.getId());
		}
		catch (final LdapException e) {
			logger.debug(e.getClass().getName(), e);
			throw new AuthenticationServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public boolean existsUser(final UserIdentifier userIdentifier) throws AuthenticationServiceException {
		try {
			return ldapConnector.getFullname(userIdentifier.getId()) != null;
		}
		catch (final LdapException e) {
			throw new AuthenticationServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public boolean isActive() {
		return authenticationConfig.isLdapEnabled();
	}

}
