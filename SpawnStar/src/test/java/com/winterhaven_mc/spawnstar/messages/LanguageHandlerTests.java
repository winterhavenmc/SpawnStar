package com.winterhaven_mc.spawnstar.messages;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhaven_mc.spawnstar.PluginMain;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LanguageHandlerTests {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private ServerMock server;
    private PluginMain plugin;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);

    }

    @AfterAll
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Test LanguageManager.")
    class LanguageManager {

        @Test
        @DisplayName("language manager is not null.")
        void LanguageManagerNotNull() {
            Assertions.assertNotNull(plugin.languageHandler);
        }

        @Test
        @DisplayName("item name is not null.")
        void ItemNameNotNull() {
            Assertions.assertNotNull(plugin.languageHandler.getItemName());
        }

        @Test
        @DisplayName("item lore is not null.")
        void ItemLoreNotNull() {
            Assertions.assertNotNull(plugin.languageHandler.getItemLore());
        }
    }

//TODO: test messages

}
