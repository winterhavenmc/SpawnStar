package com.winterhaven_mc.spawnstar;

//import org.bukkit.inventory.ItemStack;
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
	public WarmupManager warmupManager;
	public MessageManager messageManager;
	PlayerEventListener playerListener;

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;
		
		// install default config.yml if not present  
		saveDefaultConfig();
		
		// load config.yml
		reloadConfig();
		
		// register command executor
		getCommand("spawnstar").setExecutor(new CommandManager(this));

		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate cooldown manager
		cooldownManager = new CooldownManager(this);
		
		// instantiate warmup manager
		warmupManager = new WarmupManager(this);
		
		// instantiate player listener
		playerListener = new PlayerEventListener(this);

	}

}

