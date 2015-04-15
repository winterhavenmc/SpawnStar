package com.winterhaven_mc.spawnstar;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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

	final Boolean debug = getConfig().getBoolean("debug");
	public CooldownManager cooldownManager;
	public MessageManager messageManager;
	public ItemStack referenceItem;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;
		
		// save default config.yml
		saveDefaultConfig();
		
		// register command executor
		getCommand("spawnstar").setExecutor(new CommandManager(this));

		// instantiate player listener
		new PlayerEventListener(this);

		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate cooldown manager
		cooldownManager = new CooldownManager(this);
		
		// create reference item for comparisons
		referenceItem = new SpawnStarStack(1);
		
	}

}

