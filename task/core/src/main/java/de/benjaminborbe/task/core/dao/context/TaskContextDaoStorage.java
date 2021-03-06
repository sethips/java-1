package de.benjaminborbe.task.core.dao.context;

import com.google.inject.Provider;
import de.benjaminborbe.authentication.api.UserIdentifier;
import de.benjaminborbe.storage.api.StorageException;
import de.benjaminborbe.storage.api.StorageService;
import de.benjaminborbe.storage.tools.DaoStorage;
import de.benjaminborbe.storage.tools.EntityIterator;
import de.benjaminborbe.storage.tools.EntityIteratorException;
import de.benjaminborbe.storage.tools.IdentifierIterator;
import de.benjaminborbe.storage.tools.IdentifierIteratorException;
import de.benjaminborbe.storage.tools.StorageValueMap;
import de.benjaminborbe.task.api.TaskContextIdentifier;
import de.benjaminborbe.task.core.dao.task.TaskBean;
import de.benjaminborbe.task.core.dao.task.TaskBeanMapper;
import de.benjaminborbe.task.core.dao.task.TaskDao;
import de.benjaminborbe.tools.date.CalendarUtil;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Singleton
public class TaskContextDaoStorage extends DaoStorage<TaskContextBean, TaskContextIdentifier> implements TaskContextDao {

	private static final String COLUMN_FAMILY = "task_context";

	private final TaskDao taskDao;

	private final Logger logger;

	@Inject
	public TaskContextDaoStorage(
		final Logger logger,
		final StorageService storageService,
		final Provider<TaskContextBean> beanProvider,
		final TaskContextBeanMapper mapper,
		final TaskDao taskDao,
		final TaskContextIdentifierBuilder identifierBuilder,
		final CalendarUtil calendarUtil
	) {
		super(logger, storageService, beanProvider, mapper, identifierBuilder, calendarUtil);
		this.taskDao = taskDao;
		this.logger = logger;
	}

	@Override
	protected String getColumnFamily() {
		return COLUMN_FAMILY;
	}

	@Override
	public Collection<TaskContextBean> getTaskContextsByUser(final UserIdentifier userIdentifier) throws StorageException {
		try {
			final List<TaskContextBean> result = new ArrayList<TaskContextBean>();
			final EntityIterator<TaskContextBean> i = getEntityIterator(new StorageValueMap(getEncoding()).add(TaskContextBeanMapper.OWNER, String.valueOf(userIdentifier)));
			while (i.hasNext()) {
				final TaskContextBean task = i.next();
				result.add(task);
			}
			return result;
		} catch (final EntityIteratorException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public Collection<TaskContextIdentifier> getTaskContextIdentifiersByUser(final UserIdentifier userIdentifier) throws StorageException {
		try {
			final List<TaskContextIdentifier> result = new ArrayList<TaskContextIdentifier>();
			final IdentifierIterator<TaskContextIdentifier> i = getIdentifierIterator(new StorageValueMap(getEncoding()).add(TaskContextBeanMapper.OWNER, String.valueOf(userIdentifier)));
			while (i.hasNext()) {
				result.add(i.next());
			}
			return result;
		} catch (final IdentifierIteratorException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public TaskContextBean findByName(final UserIdentifier userIdentifier, final String taskContextName) throws StorageException {
		try {
			final EntityIterator<TaskContextBean> i = getEntityIterator(new StorageValueMap(getEncoding()).add(TaskContextBeanMapper.OWNER, String.valueOf(userIdentifier)).add(
				TaskContextBeanMapper.NAME, taskContextName));
			if (i.hasNext()) {
				return i.next();
			}
			return null;
		} catch (final EntityIteratorException e) {
			throw new StorageException(e);
		}
	}

	@Override
	public void onPostDelete(final TaskContextIdentifier id) throws StorageException, EntityIteratorException {
		logger.debug("onPostDelete " + id);
		final EntityIterator<TaskBean> ti = taskDao.getTasks(id);
		while (ti.hasNext()) {
			final TaskBean task = ti.next();
			logger.debug("remove context from task " + task.getId());
			task.setContext(null);
			taskDao.save(task, Arrays.asList(buildValue(TaskBeanMapper.CONTEXT)));
		}
	}
}
