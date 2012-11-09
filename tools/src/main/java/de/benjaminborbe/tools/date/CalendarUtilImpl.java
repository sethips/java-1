package de.benjaminborbe.tools.date;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.TimeZone;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.benjaminborbe.tools.util.ParseException;
import de.benjaminborbe.tools.util.ParseUtil;

@Singleton
public class CalendarUtilImpl implements CalendarUtil {

	private static final long DAY_MILLISECONDS = 24l * 60l * 60l * 1000l;

	private final ParseUtil parseUtil;

	private final TimeZoneUtil timeZoneUtil;

	@Inject
	public CalendarUtilImpl(final ParseUtil parseUtil, final TimeZoneUtil timeZoneUtil) {
		this.parseUtil = parseUtil;
		this.timeZoneUtil = timeZoneUtil;
	}

	@Override
	public String toDateTimeString(final Calendar date) {
		if (date == null) {
			return null;
		}
		return toDateString(date) + " " + toTimeString(date);
	}

	@Override
	public String toDateString(final Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		final StringWriter sw = new StringWriter();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int date = calendar.get(Calendar.DAY_OF_MONTH);
		sw.append(String.valueOf(year));
		sw.append("-");
		if (month < 10)
			sw.append("0");
		sw.append(String.valueOf(month));
		sw.append("-");
		if (date < 10)
			sw.append("0");
		sw.append(String.valueOf(date));
		return sw.toString();
	}

	@Override
	public String toTimeString(final Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		final StringWriter sw = new StringWriter();
		final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final int second = calendar.get(Calendar.SECOND);
		if (hourOfDay < 10)
			sw.append("0");
		sw.append(String.valueOf(hourOfDay));
		sw.append(":");
		if (minute < 10)
			sw.append("0");
		sw.append(String.valueOf(minute));
		sw.append(":");
		if (second < 10)
			sw.append("0");
		sw.append(String.valueOf(second));
		return sw.toString();
	}

	@Override
	public Calendar getCalendar(final TimeZone timeZone, final int year, final int month, final int date) {
		return getCalendar(timeZone, year, month, date, 0, 0, 0, 0);
	}

	@Override
	public Calendar getCalendar(final TimeZone timeZone, final int year, final int month, final int date, final int hourOfDay, final int minute, final int second) {
		return getCalendar(timeZone, year, month, date, hourOfDay, minute, second, 0);
	}

	@Override
	public Calendar getCalendar(final TimeZone timeZone, final int year, final int month, final int date, final int hourOfDay, final int minute, final int second,
			final int millisecond) {
		final Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTimeZone(timeZone);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, date);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
		return calendar;
	}

	@Override
	public Calendar parseDateTime(final TimeZone timeZone, final String dateTime) throws ParseException {
		try {
			final String[] parts = dateTime.split(" ");
			final String[] dateParts = parts[0].split("-");
			final String[] hourParts = parts[1].split(":");
			return getCalendar(timeZone, parseUtil.parseInt(dateParts[0]), parseUtil.parseInt(dateParts[1]) - 1, parseUtil.parseInt(dateParts[2]), parseUtil.parseInt(hourParts[0]),
					parseUtil.parseInt(hourParts[1]), parseUtil.parseInt(hourParts[2]));
		}
		catch (final NullPointerException e) {
			throw new ParseException("NullPointerException while parseing timeZone " + (timeZone != null ? timeZone.getID() : "null") + " dateTime: " + dateTime);
		}
	}

	@Override
	public Calendar now(final TimeZone timeZone) {
		return Calendar.getInstance(timeZone);
	}

	@Override
	public Calendar clone(final Calendar calendar) {
		return (Calendar) calendar.clone();
	}

	@Override
	public Calendar addDays(final Calendar calendar, final int amountOfDays) {
		final Calendar result = clone(calendar);
		result.setTimeInMillis(result.getTimeInMillis() + amountOfDays * DAY_MILLISECONDS);
		return result;
	}

	@Override
	public Calendar subDays(final Calendar calendar, final int amountOfDays) {
		return addDays(calendar, amountOfDays * -1);
	}

	@Override
	public long getTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getTime(final Calendar calendar) {
		return calendar.getTimeInMillis();
	}

	@Override
	public String getWeekday(final Calendar calendar) {
		final int day = calendar.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.MONDAY:
			return "monday";
		case Calendar.TUESDAY:
			return "tuesday";
		case Calendar.WEDNESDAY:
			return "wednesday";
		case Calendar.THURSDAY:
			return "thursday";
		case Calendar.FRIDAY:
			return "friday";
		case Calendar.SATURDAY:
			return "saturday";
		case Calendar.SUNDAY:
			return "sunday";
		default:
			return null;
		}
	}

	@Override
	public boolean dayEquals(final Calendar calendar1, final Calendar calendar2) {
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Calendar now() {
		return now(timeZoneUtil.getUTCTimeZone());
	}

	@Override
	public Calendar parseDate(final TimeZone timeZone, final String dateTime) throws ParseException {
		try {
			final String[] dateParts = dateTime.split("-");
			return getCalendar(timeZone, parseUtil.parseInt(dateParts[0]), parseUtil.parseInt(dateParts[1]) - 1, parseUtil.parseInt(dateParts[2]));
		}
		catch (final NullPointerException e) {
			throw new ParseException("NullPointerException while parseing timeZone " + (timeZone != null ? timeZone.getID() : "null") + " dateTime: " + dateTime);
		}
	}

	@Override
	public Calendar getCalendar(final long timeInMillis) {
		final Calendar result = now();
		result.setTimeInMillis(timeInMillis);
		return result;
	}

	@Override
	public Calendar today() {
		return today(timeZoneUtil.getUTCTimeZone());
	}

	@Override
	public Calendar today(final TimeZone timeZone) {
		final Calendar calendar = now(timeZone);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	@Override
	public Calendar getCalendarSmart(final String input) throws ParseException {
		return null;
	}

	@Override
	public boolean isLE(final Calendar c1, final Calendar c2) {
		return c1.getTimeInMillis() <= c2.getTimeInMillis();
	}

	@Override
	public boolean isGE(final Calendar c1, final Calendar c2) {
		return c1.getTimeInMillis() >= c2.getTimeInMillis();
	}

	@Override
	public boolean isLT(final Calendar c1, final Calendar c2) {
		return c1.getTimeInMillis() < c2.getTimeInMillis();
	}

	@Override
	public boolean isGT(final Calendar c1, final Calendar c2) {
		return c1.getTimeInMillis() > c2.getTimeInMillis();
	}

	@Override
	public boolean isEQ(final Calendar c1, final Calendar c2) {
		return c1.getTimeInMillis() == c2.getTimeInMillis();
	}

}
