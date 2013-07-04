package de.benjaminborbe.slash.gui.util;

import de.benjaminborbe.tools.util.ComparatorUtil;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SlashGuiRedirectDeterminerImplUnitTest {

	@Test
	public void testbuildRedirectTargetPath() {
		final SlashGuiRuleRegistry slashGuiRuleRegistry = EasyMock.createMock(SlashGuiRuleRegistry.class);
		EasyMock.expect(slashGuiRuleRegistry.getAll()).andReturn(new ArrayList<SlashGuiRule>());
		EasyMock.replay(slashGuiRuleRegistry);

		final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getContextPath()).andReturn("/bb");
		EasyMock.replay(request);

		final ComparatorUtil comparatorUtil = new ComparatorUtil();
		final SlashGuiRedirectDeterminer slashGuiRedirectDeterminer = new SlashGuiRedirectDeterminerImpl(null, null, slashGuiRuleRegistry, comparatorUtil);

		assertThat(slashGuiRedirectDeterminer.getTarget(request), is("/bb/search"));

		EasyMock.verify(request);
	}
}
