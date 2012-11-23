package de.benjaminborbe.tools.url;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.tools.util.ComparatorBase;

@Singleton
public class UrlUtilImpl implements UrlUtil {

	private final class Sort extends ComparatorBase<String, String> {

		@Override
		public String getValue(final String o) {
			return o;
		}

	}

	@Inject
	public UrlUtilImpl() {
	}

	@Override
	public String encode(final String url) throws UnsupportedEncodingException {
		return URLEncoder.encode(url, "UTF8");
	}

	@Override
	public String decode(final String url) throws UnsupportedEncodingException {
		return URLDecoder.decode(url, "UTF8");
	}

	@Override
	public String buildUrl(final String path, final Map<String, String[]> parameter) throws UnsupportedEncodingException {
		final StringWriter sw = new StringWriter();
		sw.append(path);
		if (!parameter.isEmpty()) {
			final List<String> keys = new ArrayList<String>(parameter.keySet());
			Collections.sort(keys, new Sort());
			boolean first = false;
			for (final String key : keys) {
				final String[] values = parameter.get(key);
				if (key != null && values != null) {
					for (final String value : values) {
						if (value != null) {
							if (!first) {
								sw.append('?');
								first = true;
							}
							else {
								sw.append('&');
							}
							sw.append(key);
							sw.append('=');
							sw.append(encode(value));
						}
					}
				}
			}
		}
		return sw.toString();
	}

	@Override
	public String removeTailingSlash(final String path) {
		if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
			return removeTailingSlash(path.substring(0, path.length() - 1));
		}
		else {
			return path;
		}
	}

	@Override
	public String parseId(final HttpServletRequest request, final String parameterName) {
		final String id = request.getParameter(parameterName);
		if (id != null && id.length() > 0) {
			return id;
		}
		final String uri = request.getRequestURI();
		if (uri != null && uri.length() > 0) {
			final int slashPos = uri.lastIndexOf('/');
			if (slashPos != -1) {
				final int dotPos = uri.indexOf('.', slashPos);
				if (dotPos != -1) {
					return uri.substring(slashPos + 1, dotPos);
				}
				else {
					return uri.substring(slashPos + 1);
				}
			}
		}
		return null;
	}

	@Override
	public boolean isUrl(final String url) {
		try {
			new URL(url);
			return true;
		}
		catch (final MalformedURLException e) {
			return false;
		}
	}
}
