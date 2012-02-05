package de.benjaminborbe.monitoring.gui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.html.api.CssResource;
import de.benjaminborbe.html.api.HttpContext;
import de.benjaminborbe.html.api.RequireCssResource;
import de.benjaminborbe.monitoring.api.CheckResult;
import de.benjaminborbe.monitoring.api.MonitoringService;
import de.benjaminborbe.monitoring.api.MonitoringSummaryWidget;
import de.benjaminborbe.monitoring.gui.util.MonitoringGuiCheckResultRenderer;
import de.benjaminborbe.tools.io.FlushPrintWriter;
import de.benjaminborbe.website.util.CssResourceImpl;

@Singleton
public class MonitoringGuiSummaryWidgetImpl implements MonitoringSummaryWidget, RequireCssResource {

	private final class CheckResultComparator implements Comparator<CheckResult> {

		@Override
		public int compare(final CheckResult a, final CheckResult b) {
			return a.toString().compareTo(b.toString());
		}
	}

	private final Logger logger;

	private final MonitoringService monitoringService;

	@Inject
	public MonitoringGuiSummaryWidgetImpl(final Logger logger, final MonitoringService monitoringService) {
		this.logger = logger;
		this.monitoringService = monitoringService;
	}

	@Override
	public void render(final HttpServletRequest request, final HttpServletResponse response, final HttpContext context) throws IOException {
		logger.debug("render");
		final FlushPrintWriter out = new FlushPrintWriter(response.getWriter());
		final List<CheckResult> checkResults = new ArrayList<CheckResult>(monitoringService.checkRootNodeWithCache());
		Collections.sort(checkResults, new CheckResultComparator());
		final List<CheckResult> failCheckResults = new ArrayList<CheckResult>();
		for (final CheckResult checkResult : checkResults) {
			if (!checkResult.isSuccess()) {
				failCheckResults.add(checkResult);
			}
		}
		out.println(failCheckResults.size() + " checks failed! ");
		out.println("<a href=\"" + request.getContextPath() + "/monitoring\">Details</a>");
		out.println("<ul>");
		for (final CheckResult checkResult : failCheckResults) {
			logger.debug(checkResult.toString());
			out.println("<li>");
			final MonitoringGuiCheckResultRenderer renderer = new MonitoringGuiCheckResultRenderer(checkResult);
			renderer.render(request, response, context);
			out.println("</li>");
		}
		out.println("</ul>");
	}

	@Override
	public List<CssResource> getCssResource(final HttpServletRequest request, final HttpServletResponse response) {
		final String contextPath = request.getContextPath();
		final List<CssResource> result = new ArrayList<CssResource>();
		result.add(new CssResourceImpl(contextPath + "/monitoring/css/style.css"));
		return result;
	}
}