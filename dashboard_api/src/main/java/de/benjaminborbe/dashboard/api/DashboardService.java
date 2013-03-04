package de.benjaminborbe.dashboard.api;

import java.util.Collection;

import de.benjaminborbe.authentication.api.LoginRequiredException;
import de.benjaminborbe.authentication.api.SessionIdentifier;
import de.benjaminborbe.authorization.api.PermissionDeniedException;

public interface DashboardService {

	String PERMISSION = "dashboard";

	Collection<DashboardIdentifier> getSelectedDashboards(final SessionIdentifier sessionIdentifier) throws DashboardServiceException, LoginRequiredException,
			PermissionDeniedException;

	void selectDashboard(final SessionIdentifier sessionIdentifier, DashboardIdentifier dashboardIdentifier) throws DashboardServiceException, LoginRequiredException,
			PermissionDeniedException;

	void deselectDashboard(final SessionIdentifier sessionIdentifier, DashboardIdentifier dashboardIdentifier) throws DashboardServiceException, LoginRequiredException,
			PermissionDeniedException;
}
