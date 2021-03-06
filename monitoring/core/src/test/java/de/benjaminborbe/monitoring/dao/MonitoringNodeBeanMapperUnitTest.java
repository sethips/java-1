package de.benjaminborbe.monitoring.dao;

import com.google.inject.Provider;
import de.benjaminborbe.monitoring.util.MapperMonitoringCheck;
import de.benjaminborbe.monitoring.util.MapperMonitoringNodeIdentifier;
import de.benjaminborbe.tools.date.CalendarUtil;
import de.benjaminborbe.tools.date.CalendarUtilImpl;
import de.benjaminborbe.tools.date.CurrentTime;
import de.benjaminborbe.tools.date.TimeZoneUtil;
import de.benjaminborbe.tools.date.TimeZoneUtilImpl;
import de.benjaminborbe.tools.guice.ProviderMock;
import de.benjaminborbe.tools.json.JSONParser;
import de.benjaminborbe.tools.json.JSONParserSimple;
import de.benjaminborbe.tools.mapper.MapperBoolean;
import de.benjaminborbe.tools.mapper.MapperCalendar;
import de.benjaminborbe.tools.mapper.MapperInteger;
import de.benjaminborbe.tools.mapper.MapperMapString;
import de.benjaminborbe.tools.mapper.MapperString;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.tools.util.ParseUtilImpl;
import org.easymock.EasyMock;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MonitoringNodeBeanMapperUnitTest {

	private MonitoringNodeBeanMapper getMonitoringNodeBeanMapper() {
		final Provider<MonitoringNodeBean> monitoringNodeBeanProvider = new ProviderMock<MonitoringNodeBean>(MonitoringNodeBean.class);
		final Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);

		final TimeZoneUtil timeZoneUtil = new TimeZoneUtilImpl();
		final ParseUtil parseUtil = new ParseUtilImpl();

		final CurrentTime currentTime = EasyMock.createMock(CurrentTime.class);
		EasyMock.replay(currentTime);

		final CalendarUtil calendarUtil = new CalendarUtilImpl(logger, currentTime, parseUtil, timeZoneUtil);
		final MapperCalendar mapperCalendar = new MapperCalendar(timeZoneUtil, calendarUtil, parseUtil);
		final MapperString mapperString = new MapperString();
		final MapperMonitoringCheck mapperMonitoringCheck = new MapperMonitoringCheck();
		final MapperMonitoringNodeIdentifier mapperMonitoringNodeIdentifier = new MapperMonitoringNodeIdentifier();
		final JSONParser jsonParser = new JSONParserSimple();
		final MapperMapString mapperMapString = new MapperMapString(jsonParser);
		final MapperBoolean mapperBoolean = new MapperBoolean(parseUtil);
		final MapperInteger mapperInteger = new MapperInteger(parseUtil);
		return new MonitoringNodeBeanMapper(monitoringNodeBeanProvider, mapperMonitoringNodeIdentifier, mapperString, mapperBoolean, mapperInteger, mapperCalendar, mapperMapString,
			mapperMonitoringCheck);
	}

	@Test
	public void testOwnerMapping() throws Exception {
		final MonitoringNodeBeanMapper mapper = getMonitoringNodeBeanMapper();
		final String value = "nodeName";
		final String fieldname = "name";
		{
			final MonitoringNodeBean bean = new MonitoringNodeBean();
			bean.setName(value);
			final Map<String, String> data = mapper.map(bean);
			assertEquals(data.get(fieldname), String.valueOf(value));
		}
		{
			final Map<String, String> data = new HashMap<String, String>();
			data.put(fieldname, String.valueOf(value));
			final MonitoringNodeBean bean = mapper.map(data);
			assertEquals(value, bean.getName());
		}
	}
}
