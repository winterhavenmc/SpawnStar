package com.winterhavenmc.spawnstar.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.winterhavenmc.spawnstar.PluginMain;
import org.junit.jupiter.api.*;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandManagerTests {
	private ServerMock server;
	private PlayerMock player;
	private PluginMain plugin;

	@BeforeAll
	public void setUp() {
		// Start the mock server
		server = MockBukkit.mock();

		player = server.addPlayer("testy");

		// start the mock plugin
		plugin = MockBukkit.load(PluginMain.class);

	}

	@AfterAll
	public void tearDown() {
		// Stop the mock server
		MockBukkit.unmock();
	}

	@Test
	void HelpCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar help"),
				"help command returned true.");
	}

	@Test
	void StatusCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar status"),
				"status command returned true.");
	}

	@Test
	void GiveCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar give testy"),
				"give command returned true.");
	}

	@Test
	void ReloadCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar reload"),
				"reload command returned true.");
	}

}
