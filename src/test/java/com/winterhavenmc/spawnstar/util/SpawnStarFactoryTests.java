package com.winterhavenmc.spawnstar.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
//import be.seeseemelk.mockbukkit.WorldMock;
import com.winterhavenmc.spawnstar.PluginMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpawnStarFactoryTests {

	private ServerMock server;
	//    private WorldMock world;
	private PluginMain plugin;

	@BeforeAll
	public void setUp() {
		// Start the mock server
		server = MockBukkit.mock();

		// create mock world
//        world = server.addSimpleWorld("world");

		// start the mock plugin
		plugin = MockBukkit.load(PluginMain.class);

	}

	@AfterAll
	public void tearDown() {
		// Stop the mock server
		MockBukkit.unmock();
	}

	@Nested
	@DisplayName("Test mocking setup.")
	class MockingTests {

		@Test
		@DisplayName("mock server is not null.")
		void MockServerNotNull() {
			Assertions.assertNotNull(server, "server is null.");
		}

		@Test
		@DisplayName("mock plugin is not null.")
		void MockPluginNotNull() {
			Assertions.assertNotNull(plugin, "plugin is null");
		}
	}

	@Nested
	@DisplayName("Test spawn star factory methods.")
	class SpawnStarFactoryMethodTests {

		ItemStack spawnStarItem = plugin.spawnStarFactory.create();

		@Test
		@DisplayName("new item type is nether star.")
		void ItemSetDefaultType() {
			Assertions.assertEquals(Material.NETHER_STAR, spawnStarItem.getType(),
					"new item type is not nether star.");
		}

		@Test
		@DisplayName("new item name is SpawnStar.")
		void NewItemHasDefaultName() {
			Assertions.assertNotNull(spawnStarItem.getItemMeta(), "new item stack meta data is null.");
			Assertions.assertNotNull(spawnStarItem.getItemMeta().getDisplayName(),
					"new item stack display name meta data is null.");
			Assertions.assertEquals("SpawnStar",
					ChatColor.stripColor(spawnStarItem.getItemMeta().getDisplayName()),
					"new item display name is not SpawnStar.");
		}

		@Test
		@DisplayName("new item has lore.")
		void NewItemHasDefaultLore() {
			Assertions.assertNotNull(spawnStarItem.getItemMeta());
			Assertions.assertNotNull(spawnStarItem.getItemMeta().getLore());
			Assertions.assertEquals("Use to Return to World Spawn",
					ChatColor.stripColor(String.join(" ",
							spawnStarItem.getItemMeta().getLore())),"" +
							"new item stack lore does not match default lore.");
		}

		@Test
		@DisplayName("new item is valid spawn star item.")
		void CreateAndTestValidItem() {
			Assertions.assertTrue(plugin.spawnStarFactory.isItem(spawnStarItem),
					"new item stack is not a valid spawn star item.");
		}

		@Test
		@DisplayName("spawn star factory is not null after reload.")
		void ReloadSpawnStarFactory() {
			plugin.spawnStarFactory.reload();
			Assertions.assertNotNull(plugin.spawnStarFactory, "spawn star factory is null after reload.");
		}
	}
}
