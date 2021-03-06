package de.benjaminborbe.cron.util;

import de.benjaminborbe.cron.api.CronJob;
import org.slf4j.Logger;

import javax.inject.Inject;

public class CronExecutor {

	private final CronJobRegistry cronJobRegistry;

	private final CronExecutionHistory cronExecutionHistory;

	private final Logger logger;

	@Inject
	public CronExecutor(final Logger logger, final CronJobRegistry cronJobRegistry, final CronExecutionHistory cronExecutionHistory) {
		this.logger = logger;
		this.cronJobRegistry = cronJobRegistry;
		this.cronExecutionHistory = cronExecutionHistory;
	}

	public void execute(final String name) {
		logger.trace("execute - starting job: " + name);
		final CronJob cronJob = cronJobRegistry.getByName(name);
		if (cronJob != null) {
			cronJob.execute();
			try {
				cronExecutionHistory.add(name);
			} catch (Exception e) {
				logger.warn("add to cron-history failed", e);
			}
			logger.trace("execute - finished job: " + name);
		} else {
			logger.error("execute - found no cronJob for name: " + name);
		}
	}
}
