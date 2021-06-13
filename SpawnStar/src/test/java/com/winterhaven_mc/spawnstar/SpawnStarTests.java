package com.winterhaven_mc.spawnstar;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SpawnStarTests {

    private ServerMock server;
//    private WorldMock worldMock;
    private PluginMain plugin;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        // create mock world
//        worldMock = server.addSimpleWorld("world");

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
    @DisplayName("Test SpawnStar elements.")
    class SpawnStar {

        @Nested
        @DisplayName("Test LanguageManager.")
        class LanguageManager {

            @Test
            @DisplayName("language manager is not null.")
            void LanguageManagerNotNull() {
                Assertions.assertNotNull(plugin.languageManager);
            }

            @Test
            @DisplayName("item name is not null.")
            void ItemNameNotNull() {
                Assertions.assertNotNull(plugin.languageManager.getItemName());
            }

            @Test
            @DisplayName("item lore is not null.")
            void ItemLoreNotNull() {
                Assertions.assertNotNull(plugin.languageManager.getItemLore());
            }

            @Test
            @DisplayName("teleport manager is not null.")
            void TeleportManagerNotNull() {
                Assertions.assertNotNull(plugin.teleportManager);
            }

            @Test
            @DisplayName("command manager is not null.")
            void CommandManagerNotNull() {
                Assertions.assertNotNull(plugin.commandManager);
            }

            @Test
            @DisplayName("player event listener is not null.")
            void PlayerEventListenerNotNull() {
                Assertions.assertNotNull(plugin.playerEventListener);
            }
        }
    }
}
