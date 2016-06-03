package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.commands.CommandManager;
import com.winterhaven_mc.spawnstar.listeners.PlayerEventListener;
import com.winterhaven_mc.spawnstar.teleport.TeleportManager;
import com.winterhaven_mc.spawnstar.util.MessageManager;
import com.winterhaven_mc.util.SoundManager;
import com.winterhaven_mc.util.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit plugin to create items that return player to
 * world spawn when clicked.<br>
 * An alternative to the /spawn command.
 * 
 * @author      Tim Savage
 * @version		1.0
 */
@SuppressWarnings("WeakerAccess")
public final class PluginMain extends JavaPlugin {

	// static reference to main class
	public static PluginMain instance;

	public Boolean debug = getConfig().getBoolean("debug");

	public MessageManager messageManager;
	public SoundManager soundManager;
	public TeleportManager teleportManager;
	public WorldManager worldManager;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;

		// install default configuration file if not already present
		saveDefaultConfig();

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate sound manager
		soundManager = new SoundManager(this);

		// instantiate teleport manager
		teleportManager = new TeleportManager(this);

		// instantiate command manager
		new CommandManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);
	}

}

