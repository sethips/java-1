package de.benjaminborbe.analytics.api;

import java.util.Collection;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;

public interface AnalyticsService {

	String ANALYTICS_ROLE_ADMIN = "AnalyticsAdmin";

	String ANALYTICS_ROLE_VIEW = "AnalyticsView";

	void addReportValue(AnalyticsReportIdentifier analyticsReportIdentifier) throws AnalyticsServiceException;

	void addReportValue(AnalyticsReportIdentifier analyticsReportIdentifier, AnalyticsReportValue reportValue) throws AnalyticsServiceException;

	void addReportValue(AnalyticsReportIdentifier analyticsReportIdentifier, double value) throws AnalyticsServiceException;

	void addReportValue(AnalyticsReportIdentifier analyticsReportIdentifier, long time) throws AnalyticsServiceException;

	void aggreate(SessionIdentifier sessionIdentifier) throws AnalyticsServiceException, PermissionDeniedException, LoginRequiredException;

	void createReport(SessionIdentifier sessionIdentifier, AnalyticsReportDto report) throws AnalyticsServiceException, PermissionDeniedException, LoginRequiredException,
			ValidationException;

	void deleteReport(SessionIdentifier sessionIdentifier, AnalyticsReportIdentifier analyticsIdentifier) throws AnalyticsServiceException, PermissionDeniedException,
			LoginRequiredException;

	void expectAnalyticsAdminRole(SessionIdentifier sessionIdentifier) throws PermissionDeniedException, LoginRequiredException, AnalyticsServiceException;

	void expectAnalyticsViewOrAdminRole(SessionIdentifier sessionIdentifier) throws PermissionDeniedException, LoginRequiredException, AnalyticsServiceException;

	void expectAnalyticsViewRole(SessionIdentifier sessionIdentifier) throws PermissionDeniedException, LoginRequiredException, AnalyticsServiceException;

	AnalyticsReportValueIterator getReportIterator(SessionIdentifier sessionIdentifier, AnalyticsReportIdentifier analyticsReportIdentifier,
			AnalyticsReportInterval analyticsReportInterval) throws AnalyticsServiceException, PermissionDeniedException, LoginRequiredException;

	AnalyticsReportValueIterator getReportIteratorFillMissing(SessionIdentifier sessionIdentifier, AnalyticsReportIdentifier analyticsReportIdentifier,
			AnalyticsReportInterval analyticsReportInterval) throws AnalyticsServiceException, PermissionDeniedException, LoginRequiredException;

	Collection<AnalyticsReport> getReports(SessionIdentifier sessionIdentifier) throws AnalyticsServiceException, PermissionDeniedException, LoginRequiredException;

	boolean hasAnalyticsAdminRole(SessionIdentifier sessionIdentifier) throws LoginRequiredException, AnalyticsServiceException;

	boolean hasAnalyticsViewOrAdminRole(SessionIdentifier sessionIdentifier) throws LoginRequiredException, AnalyticsServiceException;

	boolean hasAnalyticsViewRole(SessionIdentifier sessionIdentifier) throws LoginRequiredException, AnalyticsServiceException;

}
