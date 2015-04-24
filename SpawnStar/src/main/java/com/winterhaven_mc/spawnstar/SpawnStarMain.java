package com.winterhaven_mc.spawnstar;

import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;

/**
 * Bukkit plugin to create items that return player to
 * world spawn when clicked.<br>
 * An alternative to the /spawn command.
 * 
 * @author      Tim Savage
 * @version		1.0
 */
public final class SpawnStarMain extends JavaPlugin {
	
	// static reference to main class
	static SpawnStarMain instance;

	Boolean debug = getConfig().getBoolean("debug");
	
	CooldownManager cooldownManager;
	WarmupManager warmupManager;
	MessageManager messageManager;
	CommandManager commandManager;
	PlayerEventListener playerEventListener;

	MultiverseCore mvCore;
	Boolean mvEnabled = false;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;
		
		// install default config.yml if not present  
		saveDefaultConfig();
		
		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate command manager
		commandManager = new CommandManager(this);

		// instantiate cooldown manager
		cooldownManager = new CooldownManager(this);
		
		// instantiate warmup manager
		warmupManager = new WarmupManager(this);
		
		// instantiate player listener
		playerEventListener = new PlayerEventListener(this);
		
		// set reference item for making comparisons
		SpawnStarUtilities.setStandard(SpawnStarUtilities.createItem(1));
		
		// get reference to Multiverse-Core if installed
		mvCore = (MultiverseCore) this.getServer().getPluginManager().getPlugin("Multiverse-Core");
		if (mvCore != null && mvCore.isEnabled()) {
			this.getLogger().info("Multiverse-Core detected.");
			this.mvEnabled = true;
		}

	}

}

