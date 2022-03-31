package com.winterhavenmc.spawnstar;

import com.winterhavenmc.spawnstar.commands.CommandManager;
import com.winterhavenmc.spawnstar.listeners.PlayerEventListener;
import com.winterhavenmc.spawnstar.messages.Macro;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.teleport.TeleportHandler;
import com.winterhavenmc.spawnstar.util.SpawnStarFactory;
import com.winterhavenmc.util.messagebuilder.MessageBuilder;
import com.winterhavenmc.util.soundconfig.SoundConfiguration;
import com.winterhavenmc.util.soundconfig.YamlSoundConfiguration;
import com.winterhavenmc.util.worldmanager.WorldManager;
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

	public MessageBuilder<MessageId, Macro> messageBuilder;
	public SoundConfiguration soundConfig;
	public TeleportHandler teleportHandler;
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
	private PluginMain(final JavaPluginLoader loader,
	                   final PluginDescriptionFile descriptionFile,
	                   final File dataFolder,
	                   final File file) {
		super(loader, descriptionFile, dataFolder, file);
	}


	@Override
	public void onEnable() {

		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate message builder
		messageBuilder = new MessageBuilder<>(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate teleport manager
		teleportHandler = new TeleportHandler(this);

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
