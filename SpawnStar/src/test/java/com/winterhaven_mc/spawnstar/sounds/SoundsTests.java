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
import java.util.EnumMap;
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
            for (ExtSoundId extSoundId : ExtSoundId.values()) {
                enumSoundNames.add(extSoundId.name());
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
        @DisplayName("all config file keys have matching key in enum sound names")
        void SoundConfigEnumContainsAllFileSounds(String soundName) {
            Assertions.assertTrue(enumSoundNames.contains(soundName));
        }

        @ParameterizedTest
        @EnumSource(ExtSoundId.class)
        @DisplayName("Test sound in TestSoundId enum maps to valid bukkit sound name")
        void SoundConfigFileContainsAllEnumSounds(ExtSoundId extSoundId) {
            Assertions.assertTrue(plugin.soundConfig.getValidSoundNames().contains(extSoundId.getBukkitSoundName()));
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("Test sound file key has valid bukkit sound name")
        void SoundConfigFileHasValidBukkitSound(String key) {
            Assertions.assertTrue(plugin.soundConfig.getValidSoundNames().contains(plugin.soundConfig.getBukkitSoundName(key)));
        }

        @Nested
        @DisplayName("Play all sounds.")
        class PlaySounds {

            @Nested
            @DisplayName("Play all sounds in SoundId for player")
            class PlayerSounds {

                private final EnumMap<ExtSoundId, Boolean> soundsPlayed = new EnumMap<>(ExtSoundId.class);

                @ParameterizedTest
                @EnumSource(ExtSoundId.class)
                @DisplayName("play sound for player")
                void SoundConfigPlaySoundForPlayer(ExtSoundId extSoundId) {
                    plugin.soundConfig.playSound(player, extSoundId);
                    soundsPlayed.put(extSoundId, true);
                    Assertions.assertTrue(soundsPlayed.containsKey(extSoundId));
                }
            }

            @Nested
            @DisplayName("Play all sounds in SoundId at world location")
            class WorldSounds {

                private final EnumMap<ExtSoundId, Boolean> soundsPlayed = new EnumMap<>(ExtSoundId.class);

                @ParameterizedTest
                @EnumSource(ExtSoundId.class)
                @DisplayName("play sound for location")
                void SoundConfigPlaySoundForPlayer(ExtSoundId extSoundId) {
                    plugin.soundConfig.playSound(world.getSpawnLocation(), extSoundId);
                    soundsPlayed.put(extSoundId, true);
                    Assertions.assertTrue(soundsPlayed.containsKey(extSoundId));
                }
            }
        }
    }
}
