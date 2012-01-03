package de.benjaminborbe.tools.date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import com.google.inject.Injector;

import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;
import de.benjaminborbe.tools.guice.ToolModules;

public class CalendarUtilTest {

	@Test
	public void singleton() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new ToolModules());
		final CalendarUtil a = injector.getInstance(CalendarUtil.class);
		final CalendarUtil b = injector.getInstance(CalendarUtil.class);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a, b);
	}

	@Test
	public void toDateString() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		assertEquals("2011-12-24", u.toDateString(calendar));
	}

	@Test
	public void toHourString() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		assertEquals("20:15:13", u.toTimeString(calendar));
	}

	@Test
	public void getCalendar() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		assertEquals(TimeZone.getTimeZone("UTF8"), calendar.getTimeZone());
		assertEquals(2011, calendar.get(Calendar.YEAR));
		assertEquals(11, calendar.get(Calendar.MONTH));
		assertEquals(24, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(20, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(15, calendar.get(Calendar.MINUTE));
		assertEquals(13, calendar.get(Calendar.SECOND));
	}

	@Test
	public void testClone() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		final Calendar clone = u.clone(calendar);
		assertTrue(calendar.getTimeInMillis() == clone.getTimeInMillis());
		calendar.set(Calendar.YEAR, 2010);
		assertFalse(calendar.getTimeInMillis() == clone.getTimeInMillis());
	}

	@Test
	public void addDays() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		final Calendar clone = u.addDays(calendar, 1);
		assertEquals("2011-12-25", u.toDateString(clone));
	}

	@Test
	public void subDays() {
		final CalendarUtil u = new CalendarUtilImpl(null);
		final Calendar calendar = u.getCalendar(TimeZone.getTimeZone("UTF8"), 2011, 11, 24, 20, 15, 13);
		final Calendar clone = u.subDays(calendar, 1);
		assertEquals("2011-12-23", u.toDateString(clone));
	}

}
