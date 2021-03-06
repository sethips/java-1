package de.benjaminborbe.gallery.api;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;

import java.util.Collection;

public interface GalleryServiceGroup {

	GalleryGroupIdentifier getGroupByName(
		SessionIdentifier sessionIdentifier,
		String groupName
	) throws GalleryServiceException, LoginRequiredException, PermissionDeniedException;

	GalleryGroupIdentifier getGroupByNameShared(String groupName) throws GalleryServiceException;

	void deleteGroup(
		final SessionIdentifier sessionIdentifier,
		GalleryGroupIdentifier galleryGroupIdentifier
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException;

	GalleryGroupIdentifier createGroup(
		final SessionIdentifier sessionIdentifier,
		GalleryGroup galleryGroup
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException, ValidationException;

	void updateGroup(
		final SessionIdentifier sessionIdentifier,
		GalleryGroup galleryGroup
	) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException, ValidationException;

	Collection<GalleryGroupIdentifier> getGroupIdentifiers(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException;

	Collection<GalleryGroup> getGroups(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException, PermissionDeniedException;

	GalleryGroupIdentifier createGroupIdentifier(String id) throws GalleryServiceException;

	GalleryGroup getGroup(
		final SessionIdentifier sessionIdentifier,
		GalleryGroupIdentifier galleryGroupIdentifier
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException;
}
