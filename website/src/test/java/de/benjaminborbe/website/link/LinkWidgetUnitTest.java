package de.benjaminborbe.website.link;

import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.website.util.StringWidget;
import de.benjaminborbe.website.util.Target;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class LinkWidgetUnitTest {

	@Test
	public void testLink() throws Exception {
		final URL url = new URL("http://www.google.de");

		final StringWidget content = new StringWidget("Google");

		final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		final StringWriter sw = new StringWriter();
		final PrintWriter writer = new PrintWriter(sw);

		final HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(writer).anyTimes();
		EasyMock.replay(response);

		final HttpContext context = EasyMock.createMock(HttpContext.class);
		EasyMock.replay(context);

		final LinkWidget link = new LinkWidget(url, content);
		link.render(request, response, context);
		assertEquals("<a href=\"http://www.google.de\">Google</a>", sw.toString());
	}

	@Test
	public void testLinkTarget() throws Exception {
		final URL url = new URL("http://www.google.de");

		final StringWidget content = new StringWidget("Google");

		final HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
		EasyMock.replay(request);

		final StringWriter sw = new StringWriter();
		final PrintWriter writer = new PrintWriter(sw);

		final HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
		EasyMock.expect(response.getWriter()).andReturn(writer).anyTimes();
		EasyMock.replay(response);

		final HttpContext context = EasyMock.createMock(HttpContext.class);
		EasyMock.replay(context);

		final LinkWidget link = new LinkWidget(url, content);
		link.addTarget(Target.BLANK);
		link.render(request, response, context);
		assertEquals("<a href=\"http://www.google.de\" target=\"_blank\">Google</a>", sw.toString());
	}
}
