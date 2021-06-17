package com.winterhaven_mc.spawnstar.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
//import be.seeseemelk.mockbukkit.WorldMock;
import com.winterhaven_mc.spawnstar.PluginMain;
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
    class Mocking {

        @Test
        @DisplayName("mock server is not null.")
        void MockServerNotNull() {
            Assertions.assertNotNull(server);
        }

        @Test
        @DisplayName("mock plugin is not null.")
        void MockPluginNotNull() {
            Assertions.assertNotNull(plugin);
        }
    }

    @Nested
    @DisplayName("Test spawn star factory methods.")
    class SpawnStarFactory {

        ItemStack spawnStarItem = plugin.spawnStarFactory.create();

        @Test
        @DisplayName("new item type is nether star.")
        void ItemSetDefaultType() {
            Assertions.assertEquals(Material.NETHER_STAR, spawnStarItem.getType());
        }

        @Test
        @DisplayName("new item name is SpawnStar.")
        void NewItemHasDefaultName() {
            Assertions.assertNotNull(spawnStarItem.getItemMeta());
            Assertions.assertNotNull(spawnStarItem.getItemMeta().getDisplayName());
            Assertions.assertEquals("SpawnStar",
                    ChatColor.stripColor(spawnStarItem.getItemMeta().getDisplayName()));
        }

        @Test
        @DisplayName("new item has lore.")
        void NewItemHasDefaultLore() {
            Assertions.assertNotNull(spawnStarItem.getItemMeta());
            Assertions.assertNotNull(spawnStarItem.getItemMeta().getLore());
            Assertions.assertEquals("Use to Return to World Spawn",
                    ChatColor.stripColor(String.join(" ",
                            spawnStarItem.getItemMeta().getLore())));
        }

        @Test
        void CreateAndTestValidItem() {
            Assertions.assertTrue(plugin.spawnStarFactory.isItem(spawnStarItem));
        }
    }
}
