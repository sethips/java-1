package de.benjaminborbe.portfolio.gui.servlet;

import de.benjaminborbe.portfolio.gui.util.PortfolioGuiCacheUtil;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.http.HttpFilter;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class PortfolioGuiCacheFilter extends HttpFilter {

	private final CalendarUtil calendarUtil;

	private final PortfolioGuiCacheUtil portfolioGuiCacheUtil;

	@Inject
	public PortfolioGuiCacheFilter(final Logger logger, final CalendarUtil calendarUtil, final PortfolioGuiCacheUtil portfolioGuiCacheUtil) {
		super(logger);
		this.calendarUtil = calendarUtil;
		this.portfolioGuiCacheUtil = portfolioGuiCacheUtil;
	}

	@Override
	public void doFilter(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final FilterChain filterChain
	) throws IOException, ServletException {
		try {
			final String uri = request.getRequestURI();
			if (portfolioGuiCacheUtil.isCacheable(request.getContextPath(), uri)) {
				logger.debug("cache: " + uri);
				final int days = 7;
				// seconds
				final int cacheAge = days * 24 * 60 * 60;
				final long expiry = calendarUtil.getTime() + cacheAge * 1000;
				response.setDateHeader("Expires", expiry);
				response.setHeader("Cache-Control", "max-age=" + cacheAge);
			} else {
				logger.debug("not cache: " + uri);
			}
		} finally {
			filterChain.doFilter(request, response);
		}
	}

}
