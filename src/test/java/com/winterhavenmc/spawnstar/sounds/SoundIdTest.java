/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.spawnstar.sounds;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhavenmc.spawnstar.PluginMain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SoundIdTest {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private ServerMock server;
	private PluginMain plugin;

	// collection of enum sound name strings
	final Collection<String> enumSoundNames = new HashSet<>();


	@BeforeAll
	public void setUp() {

		// add all SoundId enum values to collection
		for (SoundId SoundId : SoundId.values()) {
			enumSoundNames.add(SoundId.name());
		}

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


	@SuppressWarnings("unused")
	Collection<String> GetConfigFileKeys() {
		return plugin.soundConfig.getSoundConfigKeys();
	}


	@ParameterizedTest
	@EnumSource(SoundId.class)
	@DisplayName("enum member soundId is contained in config file keys.")
	void FileKeysContainsEnumValue(SoundId soundId) {
		Assertions.assertTrue(plugin.soundConfig.isValidSoundConfigKey(soundId.name()),
				"Enum value soundId is not in config file keys.");
	}


	@ParameterizedTest
	@MethodSource("GetConfigFileKeys")
	@DisplayName("config file key has matching key in enum sound names")
	void SoundConfigEnumContainsAllFileSounds(String key) {
		Assertions.assertTrue(enumSoundNames.contains(key),
				"Enum SoundId does not contain config file key: " + key);
	}


	@ParameterizedTest
	@MethodSource("GetConfigFileKeys")
	@DisplayName("sound file key has valid bukkit sound name")
	void SoundConfigFileHasValidBukkitSound(String key) {
		String bukkitSoundName = plugin.soundConfig.getBukkitSoundName(key);
		Assertions.assertTrue(plugin.soundConfig.isValidBukkitSoundName(bukkitSoundName),
				"file key '" + key + "' has invalid bukkit sound name: " + bukkitSoundName);
		System.out.println("File key '" + key + "' has valid bukkit sound name: " + bukkitSoundName);
	}

}
