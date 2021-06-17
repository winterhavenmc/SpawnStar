package com.winterhaven_mc.spawnstar.sounds;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import com.winterhaven_mc.spawnstar.PluginMain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SoundsTests {
    private PluginMain plugin;
    @SuppressWarnings("FieldCanBeLocal")
    private ServerMock server;
    private WorldMock world;
    private PlayerMock player;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        player = server.addPlayer("testy");

        world = MockBukkit.getMock().addSimpleWorld("world");

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);
    }

    @AfterAll
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Test Sounds config.")
    class Sounds {

        // collection of enum sound name strings
        Collection<String> enumSoundNames = new HashSet<>();

        // class constructor
        Sounds() {
            // add all SoundId enum values to collection
            for (SoundId soundId : SoundId.values()) {
                enumSoundNames.add(soundId.name());
            }
        }

        @Test
        @DisplayName("Sounds config is not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @SuppressWarnings("unused")
        Collection<String> GetConfigFileKeys() {
            return plugin.soundConfig.getSoundNames();
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("get enum member names of SoundId as list of string")
        void SoundConfigTest12(String soundName) {
            Assertions.assertTrue(enumSoundNames.contains(soundName));
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("all SoundId enum members have matching key in sound config file")
        void SoundConfigContainsAllEnumSounds(SoundId soundId) {
            Assertions.assertTrue(plugin.soundConfig.getSoundNames().contains(soundId.name()));
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("play each sound for player")
        void SoundConfigPlaySoundForPlayer(SoundId soundId) {
            plugin.soundConfig.playSound(player, soundId);
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("play each sound for location")
        void SoundConfigPlaySoundForLocation(SoundId soundId) {
            plugin.soundConfig.playSound(world.getSpawnLocation(), soundId);
        }

    }

}
