package de.benjaminborbe.worktime.util;

import java.util.Calendar;

import de.benjaminborbe.worktime.api.Workday;

public class WorkdayImpl implements Workday {

	private final Calendar start;

	private final Calendar end;

	public WorkdayImpl(final Calendar start, final Calendar end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public Calendar getStart() {
		return start;
	}

	@Override
	public Calendar getEnd() {
		return end;
	}

}
