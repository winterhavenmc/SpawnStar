package com.winterhaven_mc.spawnstar.teleport;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.winterhaven_mc.spawnstar.PluginMain;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeleportManagerTests {
	@SuppressWarnings("FieldCanBeLocal")
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

	@Nested
	@DisplayName("Test Teleport Manager.")
	class TeleportManager {

		@Test
		@DisplayName("player is not warming up.")
		void PlayerIsNotWarmingUp() {
			Assertions.assertFalse(plugin.teleportManager.isWarmingUp(player),
					"player is warming up.");
		}

		@Test
		@DisplayName("player is warming up.")
		void PlayerIsWarmingUp() {
			plugin.teleportManager.putWarmup(player, 1234);
			Assertions.assertTrue(plugin.teleportManager.isWarmingUp(player),
					"player is not warming up.");
			plugin.teleportManager.cancelTeleport(player);
		}

		@Test
		@DisplayName("teleport testy somewhere.")
		void TeleportTesty() {
			player.teleport(player.getLocation().add(100, 0, 0));
			server.dispatchCommand(server.getConsoleSender(), "/spawnstar give testy");
		}
	}
}

