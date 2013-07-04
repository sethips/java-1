package de.benjaminborbe.slash.gui.servlet;

import com.google.inject.Injector;
import de.benjaminborbe.slash.gui.guice.SlashGuiModulesMock;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlashGuiLogFilterIntegrationTest {

	@Test
	public void testSingleton() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new SlashGuiModulesMock());
		final SlashGuiLogFilter a = injector.getInstance(SlashGuiLogFilter.class);
		final SlashGuiLogFilter b = injector.getInstance(SlashGuiLogFilter.class);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a, b);
	}

}
