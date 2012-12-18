package de.benjaminborbe.lunch.kioskconnector;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.inject.Inject;

import de.benjaminborbe.tools.http.HttpDownloadResult;
import de.benjaminborbe.tools.http.HttpDownloadUtil;
import de.benjaminborbe.tools.http.HttpDownloader;
import de.benjaminborbe.tools.http.HttpDownloaderException;
import de.benjaminborbe.tools.map.MapChain;
import de.benjaminborbe.tools.url.MapParameter;
import de.benjaminborbe.tools.url.UrlUtil;
import de.benjaminborbe.tools.util.Duration;
import de.benjaminborbe.tools.util.DurationUtil;

public class KioskBookingConnectorImpl implements KioskBookingConnector {

	private static final String MITTAG_EAN = "9780201379624";

	private static final String DELETE_EAN = "2030066600003";

	// 10 seconds
	private static final int TIMEOUT = 10000;

	private static final long DURATION_WARN = 5000;

	private final Logger logger;

	private final HttpDownloader httpDownloader;

	private final HttpDownloadUtil httpDownloadUtil;

	private final UrlUtil urlUtil;

	private final DurationUtil durationUtil;

	@Inject
	public KioskBookingConnectorImpl(
			final Logger logger,
			final HttpDownloader httpDownloader,
			final HttpDownloadUtil httpDownloadUtil,
			final UrlUtil urlUtil,
			final DurationUtil durationUtil) {
		this.logger = logger;
		this.httpDownloader = httpDownloader;
		this.httpDownloadUtil = httpDownloadUtil;
		this.urlUtil = urlUtil;
		this.durationUtil = durationUtil;
	}

	@Override
	public boolean bookLunch(final String customerNumber) {
		final Duration duration = durationUtil.getDuration();
		try {
			logger.info("bookLunch - customerNumber: " + customerNumber);
			// login
			final String sessionId = getLogin(customerNumber);

			// open cart
			{
				final String htmlContent = getCartContent(sessionId);
				if (htmlContent.indexOf("Hallo ") == -1) {
					logger.warn("open cart failed");
					return false;
				}
				else {
					logger.debug("open cart success");
				}
			}

			// delete all
			{
				addProduct(sessionId, DELETE_EAN);
			}

			// add mittag essen
			{
				addProduct(sessionId, MITTAG_EAN);

				final String htmlContent = getCartContent(sessionId);
				if (htmlContent.indexOf("Bastians - Mittagstisch") == -1) {
					logger.warn("add product failed");
					return false;
				}
				else {
					logger.debug("add product success");
				}
			}

			// logout
			{
				logout(sessionId, customerNumber);
				logger.debug("logout");
			}
			return true;
		}
		catch (final MalformedURLException e) {
			logger.info(e.getClass().getName(), e);
			return false;
		}
		catch (final HttpDownloaderException e) {
			logger.info(e.getClass().getName(), e);
			return false;
		}
		catch (final UnsupportedEncodingException e) {
			logger.info(e.getClass().getName(), e);
			return false;
		}
		finally {
			if (duration.getTime() > DURATION_WARN)
				logger.debug("duration " + duration.getTime());
		}
	}

	private void logout(final String sessionId, final String customerNumber) throws MalformedURLException, HttpDownloaderException {
		addProduct(sessionId, customerNumber);
	}

	private void addProduct(final String sessionId, final String ean) throws HttpDownloaderException, MalformedURLException {
		final String url = "https://kiosk.lf.seibert-media.net/index.cgi/cart";
		httpDownloader.postUrl(new URL(url), new MapChain<String, String>().add("ean", ean).add("form_action", "add"), new MapChain<String, String>().add("sessionID", sessionId),
				TIMEOUT);
	}

	private String getCartContent(final String sessionId) throws UnsupportedEncodingException, HttpDownloaderException, MalformedURLException {
		final String url = urlUtil.buildUrl("https://kiosk.lf.seibert-media.net/index.cgi/cart", new MapParameter());
		final HttpDownloadResult result = httpDownloader.getUrl(new URL(url), TIMEOUT, new MapChain<String, String>().add("sessionID", sessionId));
		final String htmlContent = httpDownloadUtil.getContent(result);
		return htmlContent;
	}

	private String getLogin(final String customerNumber) throws UnsupportedEncodingException, HttpDownloaderException, MalformedURLException {
		final String url = urlUtil.buildUrl("https://kiosk.lf.seibert-media.net/index.cgi", new MapParameter().add("customer_no", customerNumber));
		final HttpDownloadResult result = httpDownloader.getUrl(new URL(url), TIMEOUT);
		final Map<String, List<String>> headers = result.getHeaders();
		final List<String> cookies = headers.get("Set-Cookie");
		for (final String cookie : cookies) {
			logger.debug("login success");
			return parseSessionId(cookie);
		}
		logger.debug("login failed");
		return null;
	}

	protected String parseSessionId(final String cookie) {
		// sessionID=6B1E463C6C2238F591BDFF127F8EBC50; path=/
		final String s = "sessionID=";
		final int pos = cookie.indexOf(s);
		final int pos2 = cookie.indexOf(";", pos + s.length());
		if (pos != -1 && pos2 != -1) {
			return cookie.substring(pos + s.length(), pos2);
		}
		return null;
	}
}