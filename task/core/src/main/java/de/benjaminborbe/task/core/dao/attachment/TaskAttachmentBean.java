package de.benjaminborbe.task.core.dao.attachment;

import de.benjaminborbe.filestorage.api.FilestorageEntryIdentifier;
import de.benjaminborbe.storage.tools.EntityBase;
import de.benjaminborbe.storage.tools.HasCreated;
import de.benjaminborbe.storage.tools.HasModified;
import de.benjaminborbe.task.api.TaskAttachment;
import de.benjaminborbe.task.api.TaskAttachmentIdentifier;
import de.benjaminborbe.task.api.TaskIdentifier;

import java.util.Calendar;

public class TaskAttachmentBean extends EntityBase<TaskAttachmentIdentifier> implements TaskAttachment, HasCreated, HasModified {

	private static final long serialVersionUID = 6058606350883201939L;

	private TaskAttachmentIdentifier id;

	private String name;

	private Calendar created;

	private Calendar modified;

	private FilestorageEntryIdentifier file;

	private TaskIdentifier task;

	public FilestorageEntryIdentifier getFile() {
		return file;
	}

	public void setFile(final FilestorageEntryIdentifier file) {
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public TaskIdentifier getTask() {
		return task;
	}

	public void setTask(final TaskIdentifier task) {
		this.task = task;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(final Calendar created) {
		this.created = created;
	}

	public Calendar getModified() {
		return modified;
	}

	public void setModified(final Calendar modified) {
		this.modified = modified;
	}

	@Override
	public TaskAttachmentIdentifier getId() {
		return id;
	}

	@Override
	public void setId(final TaskAttachmentIdentifier id) {
		this.id = id;
	}

}
