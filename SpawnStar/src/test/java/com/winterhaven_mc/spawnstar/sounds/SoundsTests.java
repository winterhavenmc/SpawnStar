package com.winterhaven_mc.spawnstar.sounds;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import com.winterhaven_mc.spawnstar.PluginMain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


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

        Collection<String> configSoundNames = plugin.soundConfig.getSoundNames();
    }

    @AfterAll
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Test Sounds config.")
    class Sounds {


        private final Collection<String> configSoundNames = plugin.soundConfig.getSoundNames();

        @Test
        @DisplayName("Sounds config is not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @Test
        @DisplayName("get enum member names of SoundId as list of string")
        void SoundEnumContainsAllConfigSounds() {

            // get enum sound names
            Set<String> enumSoundNames = new HashSet<>();
            for (SoundId soundId : SoundId.values()) {
                enumSoundNames.add(soundId.toString());
            }

            // get config sound names
            Collection<String> configSoundNames = plugin.soundConfig.getSoundNames();

            for (String configSoundName : configSoundNames) {
                Assertions.assertTrue(enumSoundNames.contains(configSoundName));
            }
        }


//        @ParameterizedTest
//        @ArgumentsSources(strings = configSoundNames)
//        @DisplayName("all sound config file keys have matching SoundId enum members")
//        void soundEnumContainsAllConfigFileSounds(String soundName) {
//        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("all SoundId enum members have matching key in sound config file")
        void SoundConfigContainsAllEnumSounds(SoundId soundId) {
            Assertions.assertTrue(plugin.soundConfig.getSoundNames().contains(soundId.toString()));
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
