package de.benjaminborbe.gallery.dao;

import de.benjaminborbe.gallery.api.GalleryImage;
import de.benjaminborbe.gallery.api.GalleryImageIdentifier;
import de.benjaminborbe.storage.tools.EntityBase;
import de.benjaminborbe.storage.tools.HasCreated;
import de.benjaminborbe.storage.tools.HasModified;

import java.util.Calendar;

public class GalleryImageBean extends EntityBase<GalleryImageIdentifier> implements GalleryImage, HasCreated, HasModified {

	private static final long serialVersionUID = 6353074828349973344L;

	private GalleryImageIdentifier id;

	private byte[] content;

	private String contentType;

	private Calendar created;

	private Calendar modified;

	private String name;

	@Override
	public byte[] getContent() {
		return content;
	}

	public void setContent(final byte[] content) {
		this.content = content;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	@Override
	public Calendar getCreated() {
		return created;
	}

	@Override
	public void setCreated(final Calendar created) {
		this.created = created;
	}

	@Override
	public Calendar getModified() {
		return modified;
	}

	@Override
	public void setModified(final Calendar modified) {
		this.modified = modified;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public GalleryImageIdentifier getId() {
		return id;
	}

	@Override
	public void setId(final GalleryImageIdentifier id) {
		this.id = id;
	}

}
