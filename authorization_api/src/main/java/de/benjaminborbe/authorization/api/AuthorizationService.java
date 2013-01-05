package de.benjaminborbe.authorization.api;

import java.util.Collection;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authentication.api.UserIdentifier;

public interface AuthorizationService {

	boolean addPermissionRole(SessionIdentifier sessionIdentifier, PermissionIdentifier permissionIdentifier, RoleIdentifier roleIdentifier) throws PermissionDeniedException,
			AuthorizationServiceException, LoginRequiredException;

	boolean addUserRole(SessionIdentifier sessionIdentifier, UserIdentifier userIdentifier, RoleIdentifier roleIdentifier) throws PermissionDeniedException,
			AuthorizationServiceException, LoginRequiredException;

	PermissionIdentifier createPermissionIdentifier(String permissionName);

	boolean createRole(SessionIdentifier sessionIdentifier, RoleIdentifier roleIdentifier) throws PermissionDeniedException, AuthorizationServiceException;

	RoleIdentifier createRoleIdentifier(String roleName);

	boolean existsPermission(PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException;

	boolean existsRole(RoleIdentifier roleIdentifier) throws AuthorizationServiceException;

	void expectAdminRole(SessionIdentifier sessionIdentifier) throws AuthorizationServiceException, PermissionDeniedException, LoginRequiredException;

	void expectPermission(SessionIdentifier sessionIdentifier, PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException, PermissionDeniedException;

	void expectRole(SessionIdentifier sessionIdentifier, RoleIdentifier roleIdentifier) throws AuthorizationServiceException, PermissionDeniedException, LoginRequiredException;

	void expectUser(SessionIdentifier sessionIdentifier, UserIdentifier userIdentifier) throws AuthorizationServiceException, PermissionDeniedException, LoginRequiredException;

	Collection<UserIdentifier> getUserWithRole(SessionIdentifier sessionIdentifier, RoleIdentifier roleIdentifier) throws AuthorizationServiceException;

	boolean hasAdminRole(SessionIdentifier sessionIdentifier) throws AuthorizationServiceException;

	boolean hasPermission(SessionIdentifier sessionIdentifier, PermissionIdentifier permissionIdentifier) throws AuthorizationServiceException;

	boolean hasRole(SessionIdentifier sessionIdentifier, RoleIdentifier roleIdentifier) throws AuthorizationServiceException;

	boolean hasRole(UserIdentifier userIdentifier, RoleIdentifier roleIdentifier) throws AuthorizationServiceException;

	Collection<PermissionIdentifier> permissionList() throws AuthorizationServiceException;

	Collection<PermissionIdentifier> permissionList(RoleIdentifier roleIdentifier) throws AuthorizationServiceException;

	boolean removePermissionRole(SessionIdentifier sessionIdentifier, PermissionIdentifier permissionIdentifier, RoleIdentifier roleIdentifier) throws PermissionDeniedException,
			AuthorizationServiceException, LoginRequiredException;

	boolean removeUserRole(SessionIdentifier sessionIdentifier, UserIdentifier userIdentifier, RoleIdentifier roleIdentifier) throws PermissionDeniedException,
			AuthorizationServiceException, LoginRequiredException;

	Collection<RoleIdentifier> roleList() throws AuthorizationServiceException;

	void expectUser(UserIdentifier currentUser, UserIdentifier userIdentifier) throws AuthorizationServiceException, PermissionDeniedException;

	void expectUser(SessionIdentifier sessionIdentifier, Collection<UserIdentifier> userIdentifiers) throws AuthorizationServiceException, PermissionDeniedException,
			LoginRequiredException;

	void expectUser(UserIdentifier currentUser, Collection<UserIdentifier> userIdentifiers) throws AuthorizationServiceException, PermissionDeniedException;

}
