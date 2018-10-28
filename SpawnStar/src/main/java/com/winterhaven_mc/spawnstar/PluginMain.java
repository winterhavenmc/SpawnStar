package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.commands.CommandManager;
import com.winterhaven_mc.spawnstar.listeners.PlayerEventListener;
import com.winterhaven_mc.spawnstar.teleport.TeleportManager;
import com.winterhaven_mc.spawnstar.messages.MessageManager;
import com.winterhaven_mc.util.SoundConfiguration;
import com.winterhaven_mc.util.WorldManager;
import com.winterhaven_mc.util.YamlSoundConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * A Bukkit/Spigot plugin that allows the use of a custom inventory item to return to the
 * world spawn when used.<br>
 * An alternative to the /spawn command.
 * 
 * @author      Tim Savage
 * @version		1.0
 */
@SuppressWarnings("WeakerAccess")
public final class PluginMain extends JavaPlugin {

	// static reference to main class
	public static PluginMain instance;

	// global debug setting read from config file
	public Boolean debug = getConfig().getBoolean("debug");

	public MessageManager messageManager;
	public SoundConfiguration soundConfig;
	public TeleportManager teleportManager;
	public WorldManager worldManager;


	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;

		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate command manager
		new CommandManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);
	}

}
