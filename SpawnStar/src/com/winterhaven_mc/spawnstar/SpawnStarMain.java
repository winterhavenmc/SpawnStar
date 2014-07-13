package com.winterhaven_mc.spawnstar;

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

	final Boolean debug = getConfig().getBoolean("debug");
	public CooldownManager cooldown;
	public MessageManager messages;
	public InventoryManager inventory_manager;

	@Override
	public void onEnable() {

		// save default config.yml
		saveDefaultConfig();

		// register command executor
		getCommand("spawnstar").setExecutor(new CommandManager(this));

		// instantiate player listener
		new PlayerEventListener(this);

		// instantiate message manager
		messages = new MessageManager(this);

		// instantiate inventory manager
		inventory_manager = new InventoryManager(this);

		// instantiate cooldown manager
		cooldown = new CooldownManager(this);
	}

}

