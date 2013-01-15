package de.benjaminborbe.authorization.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.authentication.api.AuthenticationService;
import de.benjaminborbe.authentication.api.AuthenticationServiceException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.AuthorizationServiceException;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.authorization.api.PermissionIdentifier;
import de.benjaminborbe.authorization.api.RoleIdentifier;
import de.benjaminborbe.authorization.dao.PermissionBean;
import de.benjaminborbe.authorization.dao.PermissionDao;
import de.benjaminborbe.authorization.dao.PermissionRoleManyToManyRelation;
import de.benjaminborbe.authorization.dao.RoleBean;
import de.benjaminborbe.authorization.dao.RoleDao;
import de.benjaminborbe.authorization.dao.UserRoleManyToManyRelation;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.api.StorageIterator;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;

@Singleton
public class AuthorizationServiceImpl implements AuthorizationService {

	private final AuthenticationService authenticationService;

	private final Logger logger;

	private final PermissionDao permissionDao;

	private final PermissionRoleManyToManyRelation permissionRoleManyToManyRelation;

	private final RoleDao roleDao;

	private final UserRoleManyToManyRelation userRoleManyToManyRelation;

	@Inject
	public AuthorizationServiceImpl(
			final Logger logger,
			final AuthenticationService authenticationService,
			final RoleDao roleDao,
			final PermissionDao permissionDao,
			final UserRoleManyToManyRelation userRoleManyToManyRelation,
			final PermissionRoleManyToManyRelation permissionRoleManyToManyRelation) {
		this.logger = logger;
		this.authenticationService = authenticationService;
		this.roleDao = roleDao;
		this.permissionDao = permissionDao;
		this.userRoleManyToManyRelation = userRoleManyToManyRelation;
		this.permissionRoleManyToManyRelation = permissionRoleManyToManyRelation;
	}

	@Override
	public boolean addPermissionRole(final SessionIdentifier sessionIdentifier, final PermissionIdentifier permissionIdentifier, final RoleIdentifier roleIdentifier)
			throws PermissionDeniedException, AuthorizationServiceException, LoginRequiredException {
		try {
			expectAdminRole(sessionIdentifier);

			if (!existsPermission(permissionIdentifier)) {
				throw new AuthorizationServiceException("permission " + permissionIdentifier + " does not exists");
			}
			if (!existsRole(roleIdentifier)) {
				throw new AuthorizationServiceException("role " + roleIdentifier + " does not exists");
			}

			logger.info("addPermissionRole " + permissionIdentifier + " " + roleIdentifier);
			permissionRoleManyToManyRelation.add(permissionIdentifier, roleIdentifier);

			return true;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean addUserRole(final SessionIdentifier sessionIdentifier, final UserIdentifier userIdentifier, final RoleIdentifier roleIdentifier) throws PermissionDeniedException,
			AuthorizationServiceException, LoginRequiredException {
		try {
			expectAdminRole(sessionIdentifier);

			if (!authenticationService.existsUser(userIdentifier)) {
				throw new AuthorizationServiceException("user " + userIdentifier + " does not exists");
			}
			if (!existsRole(roleIdentifier)) {
				throw new AuthorizationServiceException("role " + roleIdentifier + " does not exists");
			}

			logger.info("addUserRole " + userIdentifier + " " + roleIdentifier);
			userRoleManyToManyRelation.add(userIdentifier, roleIdentifier);

			return true;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		catch (final AuthenticationServiceException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public PermissionIdentifier createPermissionIdentifier(final String permissionName) {
		return new PermissionIdentifier(permissionName);
	}

	@Override
	public boolean createRole(final SessionIdentifier sessionIdentifier, final RoleIdentifier roleIdentifier) throws PermissionDeniedException, AuthorizationServiceException {
		try {
			expectPermission(sessionIdentifier, new PermissionIdentifier("createRole"));

			if (roleDao.findByRolename(roleIdentifier) != null) {
				logger.info("role " + roleIdentifier + " already exists");
				return false;
			}
			final RoleBean role = roleDao.findOrCreateByRolename(roleIdentifier);
			roleDao.save(role);
			return true;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public RoleIdentifier createRoleIdentifier(final String rolename) {
		return new RoleIdentifier(rolename);
	}

	@Override
	public boolean existsPermission(final PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException {
		try {
			return permissionDao.exists(permissionIdentifier);
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean existsRole(final RoleIdentifier roleIdentifier) throws AuthorizationServiceException {
		try {
			return roleDao.exists(roleIdentifier);
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public void expectAdminRole(final SessionIdentifier sessionIdentifier) throws AuthorizationServiceException, PermissionDeniedException, LoginRequiredException {
		expectRole(sessionIdentifier, new RoleIdentifier(ADMIN_ROLE));
	}

	@Override
	public void expectPermission(final SessionIdentifier sessionIdentifier, final PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException,
			PermissionDeniedException {
		if (!hasPermission(sessionIdentifier, permissionIdentifier)) {
			throw new PermissionDeniedException("no permission " + permissionIdentifier);
		}
	}

	@Override
	public void expectRole(final SessionIdentifier sessionIdentifier, final RoleIdentifier roleIdentifier) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		expectOneOfRoles(sessionIdentifier, roleIdentifier);
	}

	@Override
	public void expectUser(final SessionIdentifier sessionIdentifier, final Collection<UserIdentifier> userIdentifiers) throws AuthorizationServiceException,
			PermissionDeniedException, LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);
			final UserIdentifier currentUser = authenticationService.getCurrentUser(sessionIdentifier);
			expectUser(currentUser, userIdentifiers);
		}
		catch (final AuthenticationServiceException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public void expectUser(final UserIdentifier currentUser, final Collection<UserIdentifier> userIdentifiers) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		if (currentUser == null) {
			throw new LoginRequiredException("user not logged in");
		}
		boolean match = false;
		final List<String> usernames = new ArrayList<String>();
		for (final UserIdentifier userIdentifier : userIdentifiers) {
			usernames.add(userIdentifier.getId());
			if (userIdentifier.equals(currentUser)) {
				match = true;
			}
		}
		if (!match) {
			throw new PermissionDeniedException("expect user " + StringUtils.join(usernames, " or ") + " but was " + currentUser);
		}
	}

	@Override
	public void expectUser(final SessionIdentifier sessionIdentifier, final UserIdentifier userIdentifier) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		expectUser(sessionIdentifier, Arrays.asList(userIdentifier));
	}

	@Override
	public void expectUser(final UserIdentifier currentUser, final UserIdentifier userIdentifier) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		expectUser(currentUser, Arrays.asList(userIdentifier));
	}

	@Override
	public Collection<UserIdentifier> getUserWithRole(final SessionIdentifier sessionIdentifier, final RoleIdentifier roleIdentifier) throws AuthorizationServiceException {
		try {
			final StorageIterator a = userRoleManyToManyRelation.getB(roleIdentifier);
			final List<UserIdentifier> result = new ArrayList<UserIdentifier>();
			while (a.hasNext()) {
				result.add(new UserIdentifier(a.next().getString()));
			}
			return result;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		catch (final UnsupportedEncodingException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		finally {
		}
	}

	@Override
	public boolean hasAdminRole(final SessionIdentifier sessionIdentifier) throws AuthorizationServiceException {
		return hasRole(sessionIdentifier, new RoleIdentifier(ADMIN_ROLE));
	}

	@Override
	public boolean hasPermission(final SessionIdentifier sessionIdentifier, final PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException {

		try {
			if (authenticationService.isSuperAdmin(sessionIdentifier)) {
				return true;
			}
		}
		catch (final AuthenticationServiceException e) {
			logger.debug(e.getClass().getName(), e);
		}

		try {
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);
			if (userIdentifier == null) {
				return false;
			}

			for (final RoleIdentifier roleIdentifier : roleList()) {
				if (hasRole(userIdentifier, roleIdentifier)) {
					if (permissionRoleManyToManyRelation.exists(permissionIdentifier, roleIdentifier)) {
						return true;
					}
				}
			}

			return authenticationService.isSuperAdmin(sessionIdentifier);
		}
		catch (final AuthenticationServiceException e) {
			throw new AuthorizationServiceException("AuthenticationServiceException", e);
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean hasRole(final SessionIdentifier sessionIdentifier, final RoleIdentifier roleIdentifier) throws AuthorizationServiceException {
		return hasOneOfRoles(sessionIdentifier, roleIdentifier);
	}

	@Override
	public boolean hasRole(final UserIdentifier userIdentifier, final RoleIdentifier roleIdentifier) throws AuthorizationServiceException {
		try {
			final RoleBean role = roleDao.findByRolename(roleIdentifier);
			if (role != null && userRoleManyToManyRelation.exists(userIdentifier, roleIdentifier)) {
				return true;
			}
			return authenticationService.isSuperAdmin(userIdentifier);
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		catch (final AuthenticationServiceException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public Collection<PermissionIdentifier> permissionList() throws AuthorizationServiceException {
		try {
			final Set<PermissionIdentifier> result = new HashSet<PermissionIdentifier>();
			final EntityIterator<PermissionBean> i = permissionDao.getEntityIterator();
			while (i.hasNext()) {
				final PermissionBean permission = i.next();
				result.add(permission.getId());
			}
			return result;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		catch (final EntityIteratorException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public Collection<PermissionIdentifier> permissionList(final RoleIdentifier roleIdentifier) throws AuthorizationServiceException {
		try {
			logger.info("permissionList for role: " + roleIdentifier);

			final Set<PermissionIdentifier> result = new HashSet<PermissionIdentifier>();
			for (final PermissionIdentifier permissionIdentifier : permissionList()) {
				if (permissionRoleManyToManyRelation.exists(permissionIdentifier, roleIdentifier)) {
					result.add(permissionIdentifier);
				}
			}

			return result;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean removePermissionRole(final SessionIdentifier sessionIdentifier, final PermissionIdentifier permissionIdentifier, final RoleIdentifier roleIdentifier)
			throws PermissionDeniedException, AuthorizationServiceException, LoginRequiredException {
		try {
			expectAdminRole(sessionIdentifier);

			logger.info("removePermissionRole " + permissionIdentifier + " " + roleIdentifier);
			permissionRoleManyToManyRelation.remove(permissionIdentifier, roleIdentifier);

			return true;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public boolean removeUserRole(final SessionIdentifier sessionIdentifier, final UserIdentifier userIdentifier, final RoleIdentifier roleIdentifier)
			throws PermissionDeniedException, AuthorizationServiceException, LoginRequiredException {
		try {
			expectAdminRole(sessionIdentifier);

			logger.info("removeUserRole " + userIdentifier + " " + roleIdentifier);
			userRoleManyToManyRelation.remove(userIdentifier, roleIdentifier);

			return true;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public Collection<RoleIdentifier> roleList() throws AuthorizationServiceException {
		try {
			final Set<RoleIdentifier> result = new HashSet<RoleIdentifier>();
			final EntityIterator<RoleBean> i = roleDao.getEntityIterator();
			while (i.hasNext()) {
				final RoleBean role = i.next();
				result.add(role.getId());
			}
			return result;
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
		catch (final EntityIteratorException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public void deleteRole(final SessionIdentifier sessionIdentifier, final RoleIdentifier roleIdentifier) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		try {
			expectAdminRole(sessionIdentifier);
			logger.debug("deleteRole - role: " + roleIdentifier);

			userRoleManyToManyRelation.removeB(roleIdentifier);
			permissionRoleManyToManyRelation.removeB(roleIdentifier);
			roleDao.delete(roleIdentifier);
		}
		catch (final StorageException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

	@Override
	public void expectOneOfRoles(final SessionIdentifier sessionIdentifier, final RoleIdentifier... roleIdentifiers) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException {
		try {
			authenticationService.expectLoggedIn(sessionIdentifier);
			if (authenticationService.isSuperAdmin(sessionIdentifier)) {
				return;
			}
			if (!hasOneOfRoles(sessionIdentifier, roleIdentifiers)) {
				throw new PermissionDeniedException("no role " + roleIdentifiers);
			}
		}
		catch (final AuthenticationServiceException e) {
			logger.debug(e.getClass().getName(), e);
		}
	}

	@Override
	public boolean hasOneOfRoles(final SessionIdentifier sessionIdentifier, final RoleIdentifier... roleIdentifiers) throws AuthorizationServiceException {
		try {
			if (authenticationService.isSuperAdmin(sessionIdentifier)) {
				return true;
			}
			logger.trace("hasRole " + roleIdentifiers);
			final UserIdentifier userIdentifier = authenticationService.getCurrentUser(sessionIdentifier);
			if (userIdentifier == null) {
				return false;
			}
			boolean hasRole = false;
			for (final RoleIdentifier roleIdentifier : roleIdentifiers) {
				if (hasRole || hasRole(userIdentifier, roleIdentifier)) {
					hasRole = true;
				}
			}
			return hasRole;
		}
		catch (final AuthenticationServiceException e) {
			throw new AuthorizationServiceException(e.getClass().getSimpleName(), e);
		}
	}

}
