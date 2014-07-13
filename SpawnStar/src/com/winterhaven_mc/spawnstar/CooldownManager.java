package com.winterhaven_mc.spawnstar;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Implements cooldown tasks for <code>SpawnStar</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class CooldownManager {
	
	private final SpawnStarMain plugin;		// reference to main class
	private HashMap<String, Long> cooldown;	// private hashmap to store player uuids and cooldown expire times

	
	/**
	 * constructor method for <code>CooldownManager</code> class
	 * 
	 * @param	plugin		A reference to this plugin's main class
	 */
public CooldownManager(SpawnStarMain plugin) {
		this.plugin = plugin;
		cooldown = new HashMap<String, Long>();
	}

	
	/**
	 * Insert player uuid into cooldown hashmap with <code>expiretime</code> as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 * @param player
	 */
	public void setPlayerCooldown(final Player player) {

		int cooldown_seconds = plugin.getConfig().getInt("cooldown");

		Long expiretime = System.currentTimeMillis() + (cooldown_seconds * 1000);
		cooldown.put(player.getUniqueId().toString(), expiretime);
		new BukkitRunnable(){

			public void run() {
				cooldown.remove(player.getUniqueId().toString());
			}
		}.runTaskLater(plugin, (cooldown_seconds * 20));
	}

	
	/**
	 * Get time remaining for player cooldown
	 * @param player
	 * @return long remainingtime
	 */
	public long getTimeRemaining(Player player) {
		long remainingtime = 0;
		if (!cooldown.containsKey(player.getUniqueId().toString())) return remainingtime;
		remainingtime = (cooldown.get(player.getUniqueId().toString()) - System.currentTimeMillis()) / 1000;
		return remainingtime;
	}

}

