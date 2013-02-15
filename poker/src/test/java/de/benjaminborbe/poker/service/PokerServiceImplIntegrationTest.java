package de.benjaminborbe.poker.service;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Injector;

import de.benjaminborbe.api.ValidationException;
import de.benjaminborbe.poker.api.PokerCardIdentifier;
import de.benjaminborbe.poker.api.PokerGame;
import de.benjaminborbe.poker.api.PokerGameIdentifier;
import de.benjaminborbe.poker.api.PokerPlayer;
import de.benjaminborbe.poker.api.PokerPlayerIdentifier;
import de.benjaminborbe.poker.api.PokerService;
import de.benjaminborbe.poker.guice.PokerModulesMock;
import de.benjaminborbe.tools.guice.GuiceInjectorBuilder;

public class PokerServiceImplIntegrationTest {

	@Test
	public void testInject() {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		assertEquals(PokerServiceImpl.class, injector.getInstance(PokerService.class).getClass());
	}

	@Test
	public void testCreateGame() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		assertNotNull(service.getGameIdentifiers());
		assertEquals(0, service.getGameIdentifiers().size());
		{
			final PokerGameIdentifier gi = service.createGame("gameA", 100);
			assertNotNull(gi);
			assertNotNull(gi.getId());
		}
		assertNotNull(service.getGameIdentifiers());
		assertEquals(1, service.getGameIdentifiers().size());
		{
			final PokerGameIdentifier gi = service.createGame("gameB", 100);
			assertNotNull(gi);
			assertNotNull(gi.getId());
		}
		assertNotNull(service.getGameIdentifiers());
		assertEquals(2, service.getGameIdentifiers().size());
	}

	@Test
	public void testDelete() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		assertNotNull(service.getGameIdentifiers());
		assertEquals(0, service.getGameIdentifiers().size());
		final PokerGameIdentifier gameIdentifier = service.createGame("game", 100);
		assertNotNull(gameIdentifier);
		assertNotNull(gameIdentifier.getId());
		assertNotNull(service.getGameIdentifiers());
		assertEquals(1, service.getGameIdentifiers().size());

		final PokerPlayerIdentifier playerIdentifier = service.createPlayer("player", 10000);
		service.joinGame(gameIdentifier, playerIdentifier);

		{
			final PokerPlayer player = service.getPlayer(playerIdentifier);
			assertNotNull(player);
			assertNotNull(player.getGame());
			assertEquals(gameIdentifier, player.getGame());
		}

		service.deleteGame(gameIdentifier);
		assertEquals(0, service.getGameIdentifiers().size());

		{
			final PokerPlayer player = service.getPlayer(playerIdentifier);
			assertNotNull(player);
			assertNull(player.getGame());
		}

	}

	@Test
	public void testCreatePlayerIdentifier() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		assertNotNull(service.createPlayerIdentifier("1337"));
		assertEquals("1337", service.createPlayerIdentifier("1337").getId());
		assertNull(service.createPlayerIdentifier(null));
	}

	@Test
	public void testCreateGameIdentifier() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		assertNotNull(service.createGameIdentifier("1337"));
		assertEquals("1337", service.createGameIdentifier("1337").getId());
		assertNull(service.createGameIdentifier(null));
	}

	@Test
	public void testStartGame() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		assertNotNull(gameIdentifier);

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertNotNull(game);
			assertEquals(gameIdentifier, game.getId());
			assertEquals(Boolean.FALSE, game.getRunning());
			assertEquals(new Long(0), game.getPot());
			assertEquals(new Long(50), game.getSmallBlind());
			assertEquals(new Long(100), game.getBigBlind());

			assertNull(service.getActivePlayer(gameIdentifier));
		}

		assertNotNull(service.getPlayers(gameIdentifier));
		assertEquals(0, service.getPlayers(gameIdentifier).size());

		try {
			service.startGame(gameIdentifier);
			fail("ValidationException expected");
		}
		catch (final ValidationException e) {
			assertNotNull(e);
		}

		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		assertNotNull(playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierA);

		assertNotNull(service.getPlayers(gameIdentifier));
		assertEquals(1, service.getPlayers(gameIdentifier).size());

		try {
			service.startGame(gameIdentifier);
			fail("ValidationException expected");
		}
		catch (final ValidationException e) {
			assertNotNull(e);
		}

		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		assertNotNull(playerIdentifierB);
		service.joinGame(gameIdentifier, playerIdentifierB);

		assertNotNull(service.getPlayers(gameIdentifier));
		assertEquals(2, service.getPlayers(gameIdentifier).size());

		service.startGame(gameIdentifier);

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertNotNull(game);
			assertEquals(gameIdentifier, game.getId());
			assertEquals(Boolean.TRUE, game.getRunning());
			assertEquals(new Long(50), game.getSmallBlind());
			assertEquals(new Long(100), game.getBigBlind());
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(150), game.getPot());

			assertNotNull(service.getActivePlayer(gameIdentifier));

			final List<PokerPlayerIdentifier> players = game.getPlayers();
			assertNotNull(players);
			assertEquals(2, players.size());
			final List<PokerCardIdentifier> cards = game.getCards();
			assertNotNull(cards);
			assertEquals(52, cards.size());
		}
	}

	@Test
	public void testHandCards() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.startGame(gameIdentifier);

		assertNotNull(service.getHandCards(playerIdentifierA));
		assertEquals(2, service.getHandCards(playerIdentifierA).size());

		assertNotNull(service.getHandCards(playerIdentifierB));
		assertEquals(2, service.getHandCards(playerIdentifierB).size());

		assertNotNull(service.getBoardCards(gameIdentifier));
		assertEquals(0, service.getBoardCards(gameIdentifier).size());
	}

	@Ignore("todo")
	@Test
	public void testBoardCards() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.startGame(gameIdentifier);

		assertNotNull(service.getBoardCards(gameIdentifier));
		assertEquals(0, service.getBoardCards(gameIdentifier).size());
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		assertNotNull(service.getBoardCards(gameIdentifier));
		assertEquals(3, service.getBoardCards(gameIdentifier).size());
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		assertNotNull(service.getBoardCards(gameIdentifier));
		assertEquals(4, service.getBoardCards(gameIdentifier).size());
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		{
			final PokerPlayerIdentifier player = service.getActivePlayer(gameIdentifier);
			service.call(gameIdentifier, player);
		}
		assertNotNull(service.getBoardCards(gameIdentifier));
		assertEquals(5, service.getBoardCards(gameIdentifier).size());
	}

	@Test
	public void testActivePlayerCall() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.startGame(gameIdentifier);

		final PokerPlayerIdentifier activePlayerA = service.getActivePlayer(gameIdentifier);
		assertNotNull(activePlayerA);

		service.call(gameIdentifier, activePlayerA);

		final PokerPlayerIdentifier activePlayerB = service.getActivePlayer(gameIdentifier);
		assertNotNull(activePlayerB);

		assertFalse(activePlayerA.equals(activePlayerB));
	}

	@Test
	public void testActivePlayerRaise() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.startGame(gameIdentifier);

		final PokerPlayerIdentifier activePlayerA = service.getActivePlayer(gameIdentifier);
		assertNotNull(activePlayerA);

		service.raise(gameIdentifier, activePlayerA, 500l);

		final PokerPlayerIdentifier activePlayerB = service.getActivePlayer(gameIdentifier);
		assertNotNull(activePlayerB);

		assertFalse(activePlayerA.equals(activePlayerB));
	}

	@Test
	public void testCall() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		final PokerPlayerIdentifier playerIdentifierC = service.createPlayer("playerC", 10000);
		final PokerPlayerIdentifier playerIdentifierD = service.createPlayer("playerD", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.joinGame(gameIdentifier, playerIdentifierC);
		service.joinGame(gameIdentifier, playerIdentifierD);
		service.startGame(gameIdentifier);

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(150), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(250), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(350), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(400), game.getPot());
		}
	}

	@Test
	public void testRaise() throws Exception {
		final Injector injector = GuiceInjectorBuilder.getInjector(new PokerModulesMock());
		final PokerService service = injector.getInstance(PokerService.class);
		final PokerGameIdentifier gameIdentifier = service.createGame("testGame", 100);
		final PokerPlayerIdentifier playerIdentifierA = service.createPlayer("playerA", 10000);
		final PokerPlayerIdentifier playerIdentifierB = service.createPlayer("playerB", 10000);
		final PokerPlayerIdentifier playerIdentifierC = service.createPlayer("playerC", 10000);
		final PokerPlayerIdentifier playerIdentifierD = service.createPlayer("playerD", 10000);
		service.joinGame(gameIdentifier, playerIdentifierA);
		service.joinGame(gameIdentifier, playerIdentifierB);
		service.joinGame(gameIdentifier, playerIdentifierC);
		service.joinGame(gameIdentifier, playerIdentifierD);
		service.startGame(gameIdentifier);

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(100), game.getBet());
			assertEquals(new Long(150), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.raise(gameIdentifier, activePlayer, 200);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(200), game.getBet());
			assertEquals(new Long(350), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(200), game.getBet());
			assertEquals(new Long(550), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(200), game.getBet());
			assertEquals(new Long(700), game.getPot());
		}

		{
			final PokerPlayerIdentifier activePlayer = service.getActivePlayer(gameIdentifier);
			assertNotNull(activePlayer);
			service.call(gameIdentifier, activePlayer);
		}

		{
			final PokerGame game = service.getGame(gameIdentifier);
			assertEquals(new Long(200), game.getBet());
			assertEquals(new Long(800), game.getPot());
		}
	}
}
