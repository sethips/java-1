package de.benjaminborbe.monitoring.gui.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;

import de.benjaminborbe.html.api.Widget;
import de.benjaminborbe.monitoring.gui.MonitoringGuiConstants;
import de.benjaminborbe.tools.url.MapParameter;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.website.link.LinkRelativWidget;

public class MonitoringGuiLinkFactory {

	private final UrlUtil urlUtil;

	@Inject
	public MonitoringGuiLinkFactory(final UrlUtil urlUtil) {
		this.urlUtil = urlUtil;
	}

	public Widget checkLive(final HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
		return new LinkRelativWidget(urlUtil, request, "/" + MonitoringGuiConstants.NAME + MonitoringGuiConstants.URL_CHECK_LIVE, new MapParameter(), "check live");
	}

	public Widget checkCache(final HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
		return new LinkRelativWidget(urlUtil, request, "/" + MonitoringGuiConstants.NAME + MonitoringGuiConstants.URL_CHECK_CACHE, new MapParameter(), "check cache");
	}

	public Widget nodeList(final HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
		return new LinkRelativWidget(urlUtil, request, "/" + MonitoringGuiConstants.NAME + MonitoringGuiConstants.URL_NODE_LIST, new MapParameter(), "node list");
	}

}
