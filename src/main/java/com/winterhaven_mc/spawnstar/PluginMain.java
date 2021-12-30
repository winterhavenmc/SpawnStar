package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.commands.CommandManager;
import com.winterhaven_mc.spawnstar.listeners.PlayerEventListener;
import com.winterhaven_mc.spawnstar.messages.Macro;
import com.winterhaven_mc.spawnstar.messages.MessageId;
import com.winterhaven_mc.spawnstar.teleport.TeleportManager;
import com.winterhaven_mc.spawnstar.util.SpawnStarFactory;
import com.winterhaven_mc.util.*;

import com.winterhavenmc.util.messagebuilder.LanguageHandler;
import com.winterhavenmc.util.messagebuilder.MessageBuilder;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;


/**
 * A Bukkit/Spigot plugin that allows the use of a custom inventory item to return to the
 * world spawn when used.<br>
 * An alternative to the /spawn command.
 *
 * @author Tim Savage
 */
public final class PluginMain extends JavaPlugin {

	public LanguageHandler languageHandler;
	public MessageBuilder<MessageId, Macro> messageBuilder;
	public SoundConfiguration soundConfig;
	public TeleportManager teleportManager;
	public WorldManager worldManager;
	public CommandManager commandManager;
	public PlayerEventListener playerEventListener;
	public SpawnStarFactory spawnStarFactory;


	/**
	 * Constructor for mocking
	 */
	@SuppressWarnings("unused")
	public PluginMain() {
		super();
	}


	/**
	 * Constructor for mocking
	 */
	@SuppressWarnings("unused")
	private PluginMain(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
		super(loader, descriptionFile, dataFolder, file);
	}


	@Override
	public void onEnable() {

		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate language manager
		languageHandler = new LanguageHandler(this);

		// instantiate message builder
		messageBuilder = new MessageBuilder<>();

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate player event listener
		playerEventListener = new PlayerEventListener(this);

		// instantiate SpawnStar item factory
		spawnStarFactory = new SpawnStarFactory(this);

	}

}
