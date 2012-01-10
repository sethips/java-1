package de.benjaminborbe.website.servlet;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Test;
import org.slf4j.Logger;

public class WebsiteRedirectServletTest {

	@Test
	public void buildRedirectTargetPath() {
		final Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);

		final WebsiteRedirectServlet calendarServlet = new WebsiteRedirectServlet(logger) {

			private static final long serialVersionUID = 1L;

			@Override
			protected String getTarget() {
				return "test";
			}
		};
		final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.expect(request.getContextPath()).andReturn("/bb");
		EasyMock.replay(request);
		assertEquals("/bb/test", calendarServlet.buildRedirectTargetPath(request));
	}
}