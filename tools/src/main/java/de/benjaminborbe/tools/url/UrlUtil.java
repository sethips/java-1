package de.benjaminborbe.tools.url;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface UrlUtil {

	String encode(String url) throws UnsupportedEncodingException;

	String decode(String url) throws UnsupportedEncodingException;

	String buildUrl(String path, Map<String, String[]> parameter) throws UnsupportedEncodingException;

	String removeTailingSlash(String path);

	String parseId(HttpServletRequest request, String paramterName);

	boolean isUrl(String url);

}
