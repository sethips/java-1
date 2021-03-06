package de.benjaminborbe.authorization.dao;

import com.google.inject.Provider;
import de.benjaminborbe.authorization.api.RoleIdentifier;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.storage.tools.DaoStorage;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;
import de.benjaminborbe.tools.date.CalendarUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoleDaoImpl extends DaoStorage<RoleBean, RoleIdentifier> implements RoleDao {

	private static final String COLUMN_FAMILY = "role";

	private final PermissionRoleManyToManyRelation permissionRoleManyToManyRelation;

	private final UserRoleManyToManyRelation userRoleManyToManyRelation;

	@Inject
	public RoleDaoImpl(
		final Logger logger,
		final StorageService storageService,
		final Provider<RoleBean> beanProvider,
		final RoleBeanMapper mapper,
		final RoleIdentifierBuilder identifierBuilder,
		final PermissionRoleManyToManyRelation permissionRoleManyToManyRelation,
		final UserRoleManyToManyRelation userRoleManyToManyRelation,
		final CalendarUtil calendarUtil
	) {
		super(logger, storageService, beanProvider, mapper, identifierBuilder, calendarUtil);
		this.permissionRoleManyToManyRelation = permissionRoleManyToManyRelation;
		this.userRoleManyToManyRelation = userRoleManyToManyRelation;
	}

	@Override
	public RoleBean findByRolename(final RoleIdentifier roleIdentifier) throws StorageException {
		try {
			final EntityIterator<RoleBean> i = getEntityIterator();
			while (i.hasNext()) {
				final RoleBean role = i.next();
				if (role.getId().equals(roleIdentifier)) {
					return role;
				}
			}
			return null;
		} catch (final EntityIteratorException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public RoleBean findOrCreateByRolename(final RoleIdentifier roleIdentifier) throws StorageException {
		{
			final RoleBean session = findByRolename(roleIdentifier);
			if (session != null) {
				return session;
			}
		}
		{
			final RoleBean role = create();
			role.setId(roleIdentifier);
			save(role);
			return role;
		}
	}

	@Override
	protected String getColumnFamily() {
		return COLUMN_FAMILY;
	}

	@Override
	public void delete(final RoleIdentifier id) throws StorageException {
		super.delete(id);
		permissionRoleManyToManyRelation.removeB(id);
		userRoleManyToManyRelation.removeB(id);
	}

	@Override
	public void delete(final RoleBean entity) throws StorageException {
		super.delete(entity);
		permissionRoleManyToManyRelation.removeB(entity.getId());
		userRoleManyToManyRelation.removeB(entity.getId());
	}

}
