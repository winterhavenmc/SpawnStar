package com.winterhaven_mc.spawnstar;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import org.bukkit.configuration.Configuration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PluginMainTests {

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
    @DisplayName("Test plugin main objects.")
    class PluginMainObjects {

        @Test
        @DisplayName("language handler not null.")
        void LanguageHandlerNotNull() {
            Assertions.assertNotNull(plugin.languageHandler);
        }

        @Test
        @DisplayName("sound config not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @Test
        @DisplayName("teleport manager not null.")
        void TeleportManagerNotNull() {
            Assertions.assertNotNull(plugin.teleportManager);
        }

        @Test
        @DisplayName("world manager not null.")
        void WorldManagerNotNull() {
            Assertions.assertNotNull(plugin.worldManager);
        }

        @Test
        @DisplayName("command manager not null.")
        void commandManagerNotNull() {
            Assertions.assertNotNull(plugin.commandManager);
        }

        @Test
        @DisplayName("player event listener not null.")
        void PlayerEventListenerNotNull() {
            Assertions.assertNotNull(plugin.playerEventListener);
        }

        @Test
        @DisplayName("spawn star factory not null.")
        void SpawnStarFactoryNotNull() {
            Assertions.assertNotNull(plugin.spawnStarFactory);
        }
    }


    @Nested
    @DisplayName("Test plugin config.")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Config {

        Configuration config = plugin.getConfig();
        Set<String> enumConfigKeyStrings = new HashSet<>();

        public Config() {
            for (ConfigKey configKey : ConfigKey.values()) {
                this.enumConfigKeyStrings.add(configKey.getKey());
            }
        }

        @Test
        @DisplayName("config not null.")
        void ConfigNotNull() {
            Assertions.assertNotNull(config);
        }

        @Test
        @DisplayName("test configured language.")
        void GetLanguage() {
            Assertions.assertEquals("en-US", config.getString("language"));
        }

        @ParameterizedTest
        @EnumSource(ConfigKey.class)
        @DisplayName("enum config key is contained in plugin.getConfig().getKeys().")
        void ConfigFileKeysContainsEnumKey(ConfigKey configKey) {
            Assertions.assertNotNull(configKey);
            Assertions.assertTrue(plugin.getConfig().getKeys(false).contains(configKey.getKey()));
        }

        @SuppressWarnings("unused")
        Set<String> GetConfigFileKeys() {
            return config.getKeys(false);
        }

        @ParameterizedTest
        @DisplayName("file config key is contained in enum.")
        @MethodSource("GetConfigFileKeys")
        void ConfigFileKeyNotNull(String key) {
            Assertions.assertNotNull(key);
            Assertions.assertTrue(enumConfigKeyStrings.contains(key));
        }
    }

}
