package com.winterhaven_mc.spawnstar.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.winterhaven_mc.spawnstar.PluginMain;
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
		server.dispatchCommand(server.getConsoleSender(), "/spawnstar help");
	}

	@Test
	void StatusCommandTest() {
		server.dispatchCommand(server.getConsoleSender(), "/spawnstar status");
	}

	@Test
	void GiveCommandTest() {
		server.dispatchCommand(server.getConsoleSender(), "/spawnstar give testy");
	}

	@Test
	void ReloadCommandTest() {
		server.dispatchCommand(server.getConsoleSender(), "/spawnstar reload");
	}

}
