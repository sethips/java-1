package de.benjaminborbe.search.core.util;

import de.benjaminborbe.search.api.SearchServiceComponent;
import de.benjaminborbe.tools.registry.Registry;

public interface SearchServiceComponentRegistry extends Registry<SearchServiceComponent> {

	SearchServiceComponent get(String alias);

}
