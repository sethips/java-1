package de.benjaminborbe.gallery.service;

import de.benjaminborbe.api.ValidationError;
import de.benjaminborbe.api.ValidationErrorSimple;
import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.api.ValidationResult;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.AuthorizationService;
import de.benjaminborbe.authorization.api.AuthorizationServiceException;
import de.benjaminborbe.authorization.api.PermissionDeniedException;
import de.benjaminborbe.gallery.GalleryConstants;
import de.benjaminborbe.gallery.api.GalleryCollection;
import de.benjaminborbe.gallery.api.GalleryCollectionIdentifier;
import de.benjaminborbe.gallery.api.GalleryEntry;
import de.benjaminborbe.gallery.api.GalleryEntryIdentifier;
import de.benjaminborbe.gallery.api.GalleryGroup;
import de.benjaminborbe.gallery.api.GalleryGroupIdentifier;
import de.benjaminborbe.gallery.api.GalleryImage;
import de.benjaminborbe.gallery.api.GalleryImageIdentifier;
import de.benjaminborbe.gallery.api.GalleryService;
import de.benjaminborbe.gallery.api.GalleryServiceException;
import de.benjaminborbe.gallery.dao.GalleryCollectionBean;
import de.benjaminborbe.gallery.dao.GalleryCollectionDao;
import de.benjaminborbe.gallery.dao.GalleryEntryBean;
import de.benjaminborbe.gallery.dao.GalleryEntryDao;
import de.benjaminborbe.gallery.dao.GalleryGroupBean;
import de.benjaminborbe.gallery.dao.GalleryGroupDao;
import de.benjaminborbe.gallery.dao.GalleryImageBean;
import de.benjaminborbe.gallery.dao.GalleryImageDao;
import de.benjaminborbe.gallery.util.SharedPredicate;
import de.benjaminborbe.lib.validation.ValidationExecutor;
import de.benjaminborbe.lib.validation.ValidationResultImpl;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;
import de.benjaminborbe.storage.tools.IdentifierIterator;
import de.benjaminborbe.storage.tools.IdentifierIteratorException;
import de.benjaminborbe.tools.util.Duration;
import de.benjaminborbe.tools.util.DurationUtil;
import de.benjaminborbe.tools.util.IdGeneratorUUID;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class GalleryServiceImpl implements GalleryService {

	private final GalleryCollectionDao galleryCollectionDao;

	private final GalleryEntryDao galleryEntryDao;

	private final GalleryGroupDao galleryGroupDao;

	private final GalleryImageDao galleryImageDao;

	private final IdGeneratorUUID idGeneratorUUID;

	private final Logger logger;

	private final ValidationExecutor validationExecutor;

	private final DurationUtil durationUtil;

	private final AuthorizationService authorizationService;

	@Inject
	public GalleryServiceImpl(
		final Logger logger,
		final AuthorizationService authorizationService,
		final GalleryCollectionDao galleryDao,
		final GalleryEntryDao galleryEntryDao,
		final IdGeneratorUUID idGeneratorUUID,
		final GalleryImageDao galleryImageDao,
		final GalleryGroupDao galleryGroupDao,
		final ValidationExecutor validationExecutor,
		final DurationUtil durationUtil
	) {
		this.logger = logger;
		this.authorizationService = authorizationService;
		this.galleryCollectionDao = galleryDao;
		this.galleryEntryDao = galleryEntryDao;
		this.idGeneratorUUID = idGeneratorUUID;
		this.galleryImageDao = galleryImageDao;
		this.galleryGroupDao = galleryGroupDao;
		this.validationExecutor = validationExecutor;
		this.durationUtil = durationUtil;
	}

	@Override
	public GalleryCollectionIdentifier createCollection(
		final SessionIdentifier sessionIdentifier, final GalleryGroupIdentifier galleryGroupIdentifier, final String name,
		final Long priority, final Boolean shared
	) throws GalleryServiceException, ValidationException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("createGallery name: " + name);

			expectPermission(sessionIdentifier);

			final GalleryCollectionIdentifier galleryIdentifier = createCollectionIdentifier(idGeneratorUUID.nextId());
			final GalleryCollectionBean collection = galleryCollectionDao.create();
			collection.setId(galleryIdentifier);
			collection.setGroupId(galleryGroupIdentifier);
			collection.setName(name);
			collection.setPriority(priority);
			collection.setShared(shared);

			final ValidationResult errors = validationExecutor.validate(collection);
			if (errors.hasErrors()) {
				logger.warn("createCollection " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryCollectionDao.save(collection);
			return galleryIdentifier;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryCollectionIdentifier createCollectionIdentifier(final String id) {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("createGalleryIdentifier");
			return new GalleryCollectionIdentifier(id);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryEntryIdentifier createEntry(
		final SessionIdentifier sessionIdentifier, final GalleryCollectionIdentifier galleryCollectionIdentifier, final String entryName,
		final Long priority, final String imagePreviewName, final byte[] imagePreviewContent, final String imagePreviewContentType, final String imageName,
		final byte[] imageContent, final String imageContentType, final Boolean shared
	) throws GalleryServiceException, ValidationException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			{
				final Collection<ValidationError> validationErrors = new ArrayList<ValidationError>();
				final GalleryCollectionBean collectionBean = galleryCollectionDao.load(galleryCollectionIdentifier);
				final GalleryGroupBean groupBean = galleryGroupDao.load(collectionBean.getGroupId());
				validationErrors.addAll(checkSize("preview", imagePreviewContent, groupBean.getPreviewLongSideMinLength(), groupBean.getPreviewLongSideMaxLength(), groupBean.getPreviewShortSideMinLength(), groupBean.getPreviewShortSideMaxLength()));
				validationErrors.addAll(checkSize("image", imageContent, groupBean.getLongSideMinLength(), groupBean.getLongSideMaxLength(), groupBean.getShortSideMinLength(), groupBean.getShortSideMaxLength()));
				if (!validationErrors.isEmpty()) {
					throw new ValidationException(new ValidationResultImpl(validationErrors));
				}
			}

			final GalleryImageIdentifier imageIdentifier = createImage(imageName, imageContent, imageContentType);
			final GalleryImageIdentifier previewImageIdentifier = createImage(imagePreviewName, imagePreviewContent, imagePreviewContentType);

			logger.debug("createEntry");
			final GalleryEntryBean entry = galleryEntryDao.create();
			final GalleryEntryIdentifier id = createEntryIdentifier(idGeneratorUUID.nextId());
			entry.setId(id);
			entry.setCollectionId(galleryCollectionIdentifier);
			entry.setName(entryName);
			entry.setPreviewImageIdentifier(previewImageIdentifier);
			entry.setImageIdentifier(imageIdentifier);
			entry.setPriority(priority);
			entry.setShared(shared);

			final ValidationResult errors = validationExecutor.validate(entry);
			if (errors.hasErrors()) {
				logger.warn("createEntry " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryEntryDao.save(entry);
			logger.debug("createEntry name: " + entryName);
			return id;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}

	}

	private Collection<ValidationError> checkSize(
		final String name,
		final byte[] imageContent,
		final Integer longSideMinLength,
		final Integer longSideMaxLength,
		final Integer shortSideMinLength,
		final Integer shortSideMaxLength
	) {
		final List<ValidationError> errors = new ArrayList<ValidationError>();
		try {
			final BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageContent));
			final int width = image.getWidth();
			final int height = image.getHeight();
			final int shortLength = Math.min(width, height);
			final int longLength = Math.max(width, height);

			if (longSideMinLength != null && longLength < longSideMinLength) {
				errors.add(new ValidationErrorSimple("long side of image(" + longLength + ") " + name + " to short(" + longSideMinLength + ")"));
			}
			if (longSideMaxLength != null && longLength > longSideMaxLength) {
				errors.add(new ValidationErrorSimple("long side of image(" + longLength + ") " + name + " to long(" + longSideMaxLength + ")"));
			}
			if (shortSideMinLength != null && shortLength < shortSideMinLength) {
				errors.add(new ValidationErrorSimple("short side of image(" + shortLength + ") " + name + " to short(" + shortSideMinLength + ")"));
			}
			if (shortSideMaxLength != null && shortLength > shortSideMaxLength) {
				errors.add(new ValidationErrorSimple("short side of image(" + shortLength + ") " + name + " to long(" + shortSideMaxLength + ")"));
			}

		} catch (IOException e) {
			errors.add(new ValidationErrorSimple("read image failed"));
		}
		return errors;
	}

	@Override
	public GalleryEntryIdentifier createEntryIdentifier(final String id) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("createGalleryImageIdentifier");
			return new GalleryEntryIdentifier(id);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryGroupIdentifier createGroup(final SessionIdentifier sessionIdentifier, final GalleryGroup galleryGroup) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException, ValidationException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("createGallery name: " + galleryGroup.getName());
			final GalleryGroupIdentifier galleryGroupIdentifier = createGroupIdentifier(idGeneratorUUID.nextId());
			final GalleryGroupBean group = galleryGroupDao.create();
			group.setId(galleryGroupIdentifier);
			group.setName(galleryGroup.getName());
			group.setShared(galleryGroup.getShared());

			group.setPreviewLongSideMaxLength(galleryGroup.getPreviewLongSideMaxLength());
			group.setPreviewLongSideMinLength(galleryGroup.getPreviewLongSideMinLength());
			group.setPreviewShortSideMaxLength(galleryGroup.getPreviewShortSideMaxLength());
			group.setPreviewShortSideMinLength(galleryGroup.getPreviewShortSideMinLength());

			group.setLongSideMaxLength(galleryGroup.getLongSideMaxLength());
			group.setLongSideMinLength(galleryGroup.getLongSideMinLength());
			group.setShortSideMaxLength(galleryGroup.getShortSideMaxLength());
			group.setShortSideMinLength(galleryGroup.getShortSideMinLength());

			final ValidationResult errors = validationExecutor.validate(group);
			if (errors.hasErrors()) {
				logger.warn("createGroup " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryGroupDao.save(group);
			return galleryGroupIdentifier;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryGroupIdentifier createGroupIdentifier(final String id) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("createGroupIdentifier");
			return new GalleryGroupIdentifier(id);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	private GalleryImageIdentifier createImage(
		final String imageName,
		final byte[] imageContent,
		final String imageContentType
	) throws GalleryServiceException, ValidationException {
		try {
			logger.debug("createImage");
			final GalleryImageBean image = galleryImageDao.create();
			final GalleryImageIdentifier id = createImageIdentifier(idGeneratorUUID.nextId());
			image.setId(id);
			image.setContentType(imageContentType);
			image.setContent(imageContent);
			image.setName(imageName);

			final ValidationResult errors = validationExecutor.validate(image);
			if (errors.hasErrors()) {
				logger.warn("createImage " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryImageDao.save(image);
			logger.debug("createEntry name: " + imageName);
			return id;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		}
	}

	@Override
	public GalleryImageIdentifier createImageIdentifier(final String id) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			return new GalleryImageIdentifier(id);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void deleteCollection(final SessionIdentifier sessionIdentifier, final GalleryCollectionIdentifier galleryIdentifier) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("deleteGallery");

			expectPermission(sessionIdentifier);

			// delete all images of gallery
			final List<GalleryEntryIdentifier> entries = getEntryIdentifiers(sessionIdentifier, galleryIdentifier);
			for (final GalleryEntryIdentifier entry : entries) {
				deleteEntry(sessionIdentifier, entry);
			}

			// delete gallery
			galleryCollectionDao.delete(galleryIdentifier);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void deleteEntry(final SessionIdentifier sessionIdentifier, final GalleryEntryIdentifier id) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("deleteImage - GalleryImageIdentifier " + id);

			expectPermission(sessionIdentifier);

			final GalleryEntry entry = getEntry(sessionIdentifier, id);
			deleteImage(entry.getPreviewImageIdentifier());
			deleteImage(entry.getImageIdentifier());

			galleryEntryDao.delete(id);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void deleteGroup(
		final SessionIdentifier sessionIdentifier,
		final GalleryGroupIdentifier galleryGroupIdentifier
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("deleteGroup");

			// delete collections
			for (final GalleryCollection collection : getCollectionsWithGroup(sessionIdentifier, galleryGroupIdentifier)) {
				deleteCollection(sessionIdentifier, collection.getId());
			}

			// delete gallery
			galleryGroupDao.delete(galleryGroupIdentifier);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	private void deleteImage(final GalleryImageIdentifier imageIdentifier) throws StorageException {
		galleryImageDao.delete(imageIdentifier);
	}

	@Override
	public GalleryCollection getCollection(
		final SessionIdentifier sessionIdentifier,
		final GalleryCollectionIdentifier galleryIdentifier
	) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getGallery");

			expectPermission(sessionIdentifier);

			return galleryCollectionDao.load(galleryIdentifier);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryCollectionIdentifier getCollectionIdentifierByName(final SessionIdentifier sessionIdentifier, final String name) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getCollectionIdentifierByName: " + name);
			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIterator();
			while (i.hasNext()) {
				final GalleryCollectionBean bean = i.next();
				if (name.equals(bean.getName())) {
					return bean.getId();
				}
			}
			return null;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryCollectionIdentifier getCollectionIdentifierByNameShared(final String name) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getCollectionIdentifierByNamePublic: " + name);
			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIteratorShared();
			while (i.hasNext()) {
				final GalleryCollectionBean bean = i.next();
				if (name.equals(bean.getName())) {
					return bean.getId();
				}
			}
			return null;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryCollectionIdentifier> getCollectionIdentifiers(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getGalleryIdentifiers");

			expectPermission(sessionIdentifier);

			final List<GalleryCollectionIdentifier> result = new ArrayList<GalleryCollectionIdentifier>();
			final IdentifierIterator<GalleryCollectionIdentifier> i = galleryCollectionDao.getIdentifierIterator();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (IdentifierIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryCollection getCollectionShared(final GalleryCollectionIdentifier galleryCollectionIdentifier)
		throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getGallery");

			final GalleryCollectionBean result = galleryCollectionDao.load(galleryCollectionIdentifier);
			if (new SharedPredicate<GalleryCollectionBean>().apply(result)) {
				return result;
			} else {
				return null;
			}
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryCollection> getCollections(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);
			logger.debug("getGalleries");

			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIterator();
			final List<GalleryCollection> result = new ArrayList<GalleryCollection>();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryCollection> getCollectionsShared() throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getCollectionsPublic");

			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIteratorShared();
			final List<GalleryCollection> result = new ArrayList<GalleryCollection>();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryCollection> getCollectionsWithGroup(final SessionIdentifier sessionIdentifier, final GalleryGroupIdentifier galleryGroupIdentifier)
		throws GalleryServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getCollectionsWithGroup");
			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIterator();
			final List<GalleryCollection> result = new ArrayList<GalleryCollection>();
			while (i.hasNext()) {
				final GalleryCollectionBean collection = i.next();
				if (galleryGroupIdentifier.equals(collection.getGroupId())) {
					result.add(collection);
				}
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryCollection> getCollectionsWithGroupShared(final GalleryGroupIdentifier galleryGroupIdentifier)
		throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getCollectionsWithGroupPublic");
			final EntityIterator<GalleryCollectionBean> i = galleryCollectionDao.getEntityIteratorShared();
			final List<GalleryCollection> result = new ArrayList<GalleryCollection>();
			while (i.hasNext()) {
				final GalleryCollectionBean collection = i.next();
				if (galleryGroupIdentifier.equals(collection.getGroupId())) {
					result.add(collection);
				}
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryEntry> getEntries(final SessionIdentifier sessionIdentifier, final GalleryCollectionIdentifier galleryCollectionIdentifier)
		throws GalleryServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getEntries");
			final EntityIterator<GalleryEntryBean> i = galleryEntryDao.getEntityIterator();
			final List<GalleryEntry> result = new ArrayList<GalleryEntry>();
			while (i.hasNext()) {
				final GalleryEntryBean entry = i.next();
				if (galleryCollectionIdentifier.equals(entry.getCollectionId())) {
					result.add(entry);
				}
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryEntry> getEntriesShared(final GalleryCollectionIdentifier galleryCollectionIdentifier)
		throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getEntriesPublic");
			final EntityIterator<GalleryEntryBean> i = galleryEntryDao.getEntityIteratorShared();
			final List<GalleryEntry> result = new ArrayList<GalleryEntry>();
			while (i.hasNext()) {
				final GalleryEntryBean entry = i.next();
				if (galleryCollectionIdentifier.equals(entry.getCollectionId())) {
					result.add(entry);
				}
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryEntry getEntry(
		final SessionIdentifier sessionIdentifier,
		final GalleryEntryIdentifier id
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getImage");

			expectPermission(sessionIdentifier);

			return galleryEntryDao.load(id);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public List<GalleryEntryIdentifier> getEntryIdentifiers(final SessionIdentifier sessionIdentifier, final GalleryCollectionIdentifier galleryIdentifier)
		throws GalleryServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getImages - GallerIdentifier: " + galleryIdentifier);

			expectPermission(sessionIdentifier);

			final List<GalleryEntryIdentifier> result = new ArrayList<GalleryEntryIdentifier>(galleryEntryDao.getGalleryImageIdentifiers(galleryIdentifier));
			logger.debug("getImages - GallerIdentifier: " + galleryIdentifier + " => " + result.size());
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryGroup getGroup(final SessionIdentifier sessionIdentifier, final GalleryGroupIdentifier galleryGroupIdentifier) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getGroup");
			return galleryGroupDao.load(galleryGroupIdentifier);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryGroupIdentifier getGroupByName(
		final SessionIdentifier sessionIdentifier,
		final String groupName
	) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getGroupByName: " + groupName);
			final EntityIterator<GalleryGroupBean> i = galleryGroupDao.getEntityIterator();
			while (i.hasNext()) {
				final GalleryGroupBean group = i.next();
				if (groupName.equals(group.getName())) {
					return group.getId();
				}
			}
			return null;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryGroupIdentifier getGroupByNameShared(final String groupName) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.debug("getGroupByName: " + groupName);
			final EntityIterator<GalleryGroupBean> i = galleryGroupDao.getEntityIteratorShared();
			while (i.hasNext()) {
				final GalleryGroupBean group = i.next();
				if (groupName.equals(group.getName())) {
					return group.getId();
				}
			}
			return null;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryGroupIdentifier> getGroupIdentifiers(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException,
		PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getGroupIdentifiers");
			final List<GalleryGroupIdentifier> result = new ArrayList<GalleryGroupIdentifier>();
			final IdentifierIterator<GalleryGroupIdentifier> i = galleryGroupDao.getIdentifierIterator();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (IdentifierIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public Collection<GalleryGroup> getGroups(final SessionIdentifier sessionIdentifier) throws GalleryServiceException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("getGalleries");
			final EntityIterator<GalleryGroupBean> i = galleryGroupDao.getEntityIterator();
			final List<GalleryGroup> result = new ArrayList<GalleryGroup>();
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} catch (EntityIteratorException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public GalleryImage getImage(final GalleryImageIdentifier id) throws GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			return galleryImageDao.load(id);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void updateEntry(
		final SessionIdentifier sessionIdentifier, final GalleryEntryIdentifier galleryEntryIdentifier,
		final GalleryCollectionIdentifier galleryCollectionIdentifier, final String entryName, final Long priority, final Boolean shared
	) throws GalleryServiceException,
		ValidationException, LoginRequiredException, PermissionDeniedException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("updateEntry");

			final GalleryEntryBean entry = galleryEntryDao.load(galleryEntryIdentifier);
			entry.setCollectionId(galleryCollectionIdentifier);
			entry.setName(entryName);
			entry.setPriority(priority);
			entry.setShared(shared);

			final ValidationResult errors = validationExecutor.validate(entry);
			if (errors.hasErrors()) {
				logger.warn("updateEntry " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryEntryDao.save(entry);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void updateGroup(
		final SessionIdentifier sessionIdentifier,
		final GalleryGroup galleryGroup
	)
		throws GalleryServiceException, LoginRequiredException, PermissionDeniedException, ValidationException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("updateGroup name: " + galleryGroup.getName());
			final GalleryGroupBean group = galleryGroupDao.load(galleryGroup.getId());
			group.setName(galleryGroup.getName());
			group.setShared(galleryGroup.getShared());

			group.setPreviewLongSideMaxLength(galleryGroup.getPreviewLongSideMaxLength());
			group.setPreviewLongSideMinLength(galleryGroup.getPreviewLongSideMinLength());
			group.setPreviewShortSideMaxLength(galleryGroup.getPreviewShortSideMaxLength());
			group.setPreviewShortSideMinLength(galleryGroup.getPreviewShortSideMinLength());

			group.setLongSideMaxLength(galleryGroup.getLongSideMaxLength());
			group.setLongSideMinLength(galleryGroup.getLongSideMinLength());
			group.setShortSideMaxLength(galleryGroup.getShortSideMaxLength());
			group.setShortSideMinLength(galleryGroup.getShortSideMinLength());

			final ValidationResult errors = validationExecutor.validate(group);
			if (errors.hasErrors()) {
				logger.warn("updateGroup " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryGroupDao.save(group);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void updateCollection(
		final SessionIdentifier sessionIdentifier, final GalleryCollectionIdentifier galleryCollectionIdentifier,
		final GalleryGroupIdentifier galleryGroupIdentifier, final String collectionName, final Long prio, final Boolean shared
	) throws GalleryServiceException,
		LoginRequiredException, PermissionDeniedException, ValidationException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("updateCollection name: " + collectionName);

			final GalleryCollectionBean collection = galleryCollectionDao.load(galleryCollectionIdentifier);
			collection.setGroupId(galleryGroupIdentifier);
			collection.setName(collectionName);
			collection.setPriority(prio);
			collection.setShared(shared);

			final ValidationResult errors = validationExecutor.validate(collection);
			if (errors.hasErrors()) {
				logger.warn("createCollection " + errors.toString());
				throw new ValidationException(errors);
			}

			galleryCollectionDao.save(collection);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void swapEntryPrio(
		final SessionIdentifier sessionIdentifier,
		final GalleryEntryIdentifier galleryEntryIdentifierA,
		final GalleryEntryIdentifier galleryEntryIdentifierB
	)
		throws PermissionDeniedException, LoginRequiredException, GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.trace("swapPrio " + galleryEntryIdentifierA + " <=> " + galleryEntryIdentifierB);
			final GalleryEntryBean galleryEntryA = galleryEntryDao.load(galleryEntryIdentifierA);
			final GalleryEntryBean galleryEntryB = galleryEntryDao.load(galleryEntryIdentifierB);

			Long p1 = galleryEntryA.getPriority();
			Long p2 = galleryEntryB.getPriority();
			if (p1 == null && p2 == null) {
				p1 = 0l;
				p2 = 1l;
			}
			if (p1 != null && p2 == null) {
				p2 = p1;
				p1++;
			}
			if (p1 == null && p2 != null) {
				p1 = p2;
				p2++;
			}
			if (p1 != null && p2 != null) {
				if (p2.equals(p1)) {
					p1++;
				} else {
					final Long tmp = p1;
					p1 = p2;
					p2 = tmp;
				}
			}
			galleryEntryA.setPriority(p1);
			galleryEntryB.setPriority(p2);
			galleryEntryDao.save(galleryEntryA);
			galleryEntryDao.save(galleryEntryB);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void shareEntry(
		final SessionIdentifier sessionIdentifier,
		final GalleryEntryIdentifier galleryEntryIdentifier
	) throws PermissionDeniedException, LoginRequiredException,
		GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("shareEntry");

			final GalleryEntryBean entry = galleryEntryDao.load(galleryEntryIdentifier);
			entry.setShared(true);

			galleryEntryDao.save(entry);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void unshareEntry(final SessionIdentifier sessionIdentifier, final GalleryEntryIdentifier galleryEntryIdentifier) throws PermissionDeniedException,
		LoginRequiredException, GalleryServiceException {
		final Duration duration = durationUtil.getDuration();
		try {
			expectPermission(sessionIdentifier);

			logger.debug("unshareEntry");

			final GalleryEntryBean entry = galleryEntryDao.load(galleryEntryIdentifier);
			entry.setShared(false);

			galleryEntryDao.save(entry);
		} catch (final StorageException e) {
			throw new GalleryServiceException(e.getClass().getName(), e);
		} finally {
			logger.debug("duration " + duration.getTime());
		}
	}

	@Override
	public void expectPermission(final SessionIdentifier sessionIdentifier) throws PermissionDeniedException, LoginRequiredException, GalleryServiceException {
		try {
			authorizationService.expectPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(GalleryConstants.PERMISSION));
		} catch (final AuthorizationServiceException e) {
			throw new GalleryServiceException(e);
		}
	}

	@Override
	public boolean hasPermission(final SessionIdentifier sessionIdentifier) throws GalleryServiceException {
		try {
			return authorizationService.hasPermission(sessionIdentifier, authorizationService.createPermissionIdentifier(GalleryConstants.PERMISSION));
		} catch (final AuthorizationServiceException e) {
			throw new GalleryServiceException(e);
		}
	}
}
