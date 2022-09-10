package com.winterhavenmc.spawnstar;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PluginMainTests {

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


	@Nested
	@DisplayName("Test mocking setup.")
	class MockingTests {

		@Test
		@DisplayName("server is not null.")
		void ServerNotNull() {
			Assertions.assertNotNull(server, "server is null.");
		}

		@Test
		@DisplayName("plugin is not null.")
		void PluginNotNull() {
			Assertions.assertNotNull(plugin, "plugin is null.");
		}

		@Test
		@DisplayName("plugin is enabled.")
		void PluginEnabled() {
			Assertions.assertTrue(plugin.isEnabled(),"plugin is not enabled.");
		}
	}


	@Nested
	@DisplayName("Test plugin main objects.")
	class PluginMainObjectTests {

		@Test
		@DisplayName("language handler not null.")
		void LanguageHandlerNotNull() {
			Assertions.assertNotNull(plugin.messageBuilder,
					"language handler is null.");
		}

		@Test
		@DisplayName("sound config not null.")
		void SoundConfigNotNull() {
			Assertions.assertNotNull(plugin.soundConfig,
					"sound config is null.");
		}

		@Test
		@DisplayName("teleport manager not null.")
		void TeleportManagerNotNull() {
			Assertions.assertNotNull(plugin.teleportHandler,
					"teleport manager is null.");
		}

		@Test
		@DisplayName("world manager not null.")
		void WorldManagerNotNull() {
			Assertions.assertNotNull(plugin.worldManager,
					"world manager is null.");
		}

		@Test
		@DisplayName("command manager not null.")
		void commandManagerNotNull() {
			Assertions.assertNotNull(plugin.commandManager,
					"command manager is null.");
		}

		@Test
		@DisplayName("player event listener not null.")
		void PlayerEventListenerNotNull() {
			Assertions.assertNotNull(plugin.playerEventListener,
					"player event listener is null.");
		}

		@Test
		@DisplayName("spawn star factory not null.")
		void SpawnStarFactoryNotNull() {
			Assertions.assertNotNull(plugin.spawnStarUtility,
					"spawn star factory is null.");
		}
	}


	@Nested
	@DisplayName("Test plugin config.")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class ConfigTests {

		final Set<String> enumConfigKeyStrings = new HashSet<>();

		public ConfigTests() {
			for (ConfigSetting configSetting : ConfigSetting.values()) {
				this.enumConfigKeyStrings.add(configSetting.getKey());
			}
		}

		@Test
		@DisplayName("config not null.")
		void ConfigNotNull() {
			Assertions.assertNotNull(plugin.getConfig(),
					"plugin config is null.");
		}

		@Test
		@DisplayName("test configured language.")
		void GetLanguage() {
			Assertions.assertEquals("en-US", plugin.getConfig().getString("language"),
					"language does not equal 'en-US'");
		}

		@SuppressWarnings("unused")
		Set<String> ConfigFileKeys() {
			return plugin.getConfig().getKeys(false);
		}

		@ParameterizedTest
		@DisplayName("file config key is contained in ConfigSetting enum.")
		@MethodSource("ConfigFileKeys")
		void ConfigFileKeyNotNull(String key) {
			Assertions.assertNotNull(key);
			Assertions.assertTrue(enumConfigKeyStrings.contains(key),
					"file config key is not contained in ConfigSetting enum.");
		}

		@ParameterizedTest
		@EnumSource(ConfigSetting.class)
		@DisplayName("ConfigSetting enum matches config file key/value pairs.")
		void ConfigFileKeysContainsEnumKey(ConfigSetting configSetting) {
			Assertions.assertEquals(configSetting.getValue(), plugin.getConfig().getString(configSetting.getKey()),
					"ConfigSetting enum value does not match config file key/value pair.");
		}
	}


	@Test
	void HelpCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar help"),
				"help command returned true.");
	}

	@Test
	void StatusCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar status"),
				"status command returned true.");
	}

	@Test
	void GiveCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar give testy"),
				"give command returned true.");
	}

	@Test
	void ReloadCommandTest() {
		Assertions.assertFalse(server.dispatchCommand(server.getConsoleSender(), "/spawnstar reload"),
				"reload command returned true.");
	}


	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@DisplayName("Test messages.")
	class MessageTests {

		// collection of enum sound name strings
		final Collection<String> enumMessageNames = new HashSet<>();

		// class constructor
		MessageTests() {
			// add all MessageId enum values to collection
			for (com.winterhavenmc.spawnstar.messages.MessageId MessageId : MessageId.values()) {
				enumMessageNames.add(MessageId.name());
			}
		}

		@ParameterizedTest
		@EnumSource(MessageId.class)
		@DisplayName("enum member MessageId is contained in getConfig() keys.")
		void FileKeysContainsEnumValue(MessageId messageId) {
			Assertions.assertNotNull(messageId);
			Assertions.assertNotNull(plugin.messageBuilder.getMessage(messageId),
					"config file message is null.");
		}
	}


	@ParameterizedTest
	@EnumSource(SoundId.class)
	@DisplayName("enum member soundId is contained in config file keys.")
	void FileKeysContainsEnumValue(SoundId soundId) {
		Assertions.assertTrue(plugin.soundConfig.isValidSoundConfigKey(soundId.name()),
				"Enum value soundId is not in config file keys.");
	}


	@SuppressWarnings("unused")
	Collection<String> GetConfigFileKeys() {
		return plugin.soundConfig.getSoundConfigKeys();
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


	@Nested
	@DisplayName("Test spawn star factory methods.")
	class SpawnStarFactoryMethodTests {

		final ItemStack spawnStarItem = plugin.spawnStarUtility.create();

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
			Assertions.assertTrue(plugin.spawnStarUtility.isItem(spawnStarItem),
					"new item stack is not a valid spawn star item.");
		}

	}

}
