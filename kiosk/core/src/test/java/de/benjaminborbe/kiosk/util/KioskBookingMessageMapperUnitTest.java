package de.benjaminborbe.kiosk.util;

import com.google.inject.Provider;
import de.benjaminborbe.tools.guice.ProviderMock;
import de.benjaminborbe.tools.mapper.MapperLong;
import de.benjaminborbe.tools.util.ParseUtil;
import de.benjaminborbe.tools.util.ParseUtilImpl;
import org.easymock.EasyMock;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KioskBookingMessageMapperUnitTest {

	@Test
	public void testMap() throws Exception {
		{
			final Long customer = null;
			final Long ean = null;
			final KioskBookingMessage bookingMessage = new KioskBookingMessage(customer, ean);
			final KioskBookingMessageMapper mapper = getBookingMessageMapper();
			final String string = mapper.map(bookingMessage);
			assertNotNull(string);
			final KioskBookingMessage bookingMessageAfter = mapper.map(string);
			assertEquals(customer, bookingMessageAfter.getCustomer());
			assertEquals(ean, bookingMessageAfter.getEan());
		}

		{
			final Long customer = 123l;
			final Long ean = 1337l;
			final KioskBookingMessage bookingMessage = new KioskBookingMessage(customer, ean);
			final KioskBookingMessageMapper mapper = getBookingMessageMapper();
			final String string = mapper.map(bookingMessage);
			assertNotNull(string);
			final KioskBookingMessage bookingMessageAfter = mapper.map(string);
			assertEquals(customer, bookingMessageAfter.getCustomer());
			assertEquals(ean, bookingMessageAfter.getEan());
		}
	}

	private KioskBookingMessageMapper getBookingMessageMapper() {
		final Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);

		final ParseUtil parseUtil = new ParseUtilImpl();
		final Provider<KioskBookingMessage> p = new ProviderMock<KioskBookingMessage>(KioskBookingMessage.class);
		final MapperLong mapperLong = new MapperLong(parseUtil);
		return new KioskBookingMessageMapperImpl(p, mapperLong);
	}
}
