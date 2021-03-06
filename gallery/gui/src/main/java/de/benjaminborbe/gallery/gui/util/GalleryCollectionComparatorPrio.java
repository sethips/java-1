package de.benjaminborbe.gallery.gui.util;

import de.benjaminborbe.gallery.api.GalleryCollection;
import de.benjaminborbe.tools.util.ComparatorBase;

public class GalleryCollectionComparatorPrio extends ComparatorBase<GalleryCollection, Long> {

	@Override
	public Long getValue(final GalleryCollection o) {
		return o.getPriority();
	}

	@Override
	public boolean nullFirst() {
		return false;
	}

}
