package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.SpawnStarMain;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Implements player event listener for <code>SpawnStar</code> events.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class PlayerEventListener
implements Listener {

	private final SpawnStarMain plugin; // reference to main class

	
	/**
	 * constructor method for <code>PlayerEventListener</code> class
	 * 
	 * @param	plugin		A reference to this plugin's main class
	 */
	public PlayerEventListener(SpawnStarMain plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
	}

	
	/**
	 * Player interact event listener
	 */
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {

		String overworldname;
		World overworld;
		Player player = event.getPlayer();
		World world = player.getWorld();
		Location spawnLoc = world.getSpawnLocation();
		ItemStack player_item = player.getItemInHand();
		String config_itemname = plugin.getConfig().getString("itemname", "SpawnStar");
		ItemStack config_item = plugin.inventory_manager.createSpawnStarItem(1);

		// if item used is not a spawnstar item config, do nothing and return
		if (!player_item.getType().equals(config_item.getType())) {
			return;
		}
		
		//if item used does not have spawnstar metadata, do nothing and return
		if (!player_item.getItemMeta().equals(config_item.getItemMeta())) {
			return;
		}
		
		// if item used does not spawnstar durability from config, do nothing and return
		if (player_item.getDurability() != config_item.getDurability()) {
			return;
		}
		
		// if event action is not a left or right click, do nothing and return
		if (event.getAction() == Action.PHYSICAL) {
			return;
		}
		
		// if player current workd is not enabled in config, do nothing and return
		if (!this.playerWorldEnabled(player)) {
			return;
		}
		
		// if player does not have spawnstar.use permission, do nothing and return
		if (!player.hasPermission("spawnstar.use")) {
			this.plugin.messages.sendPlayerMessage(player, "deniedpermission");
			return;
		}
		
		// cancel event
		event.setCancelled(true);
		
		// if player cooldown has not expired, send player cooldown message and return
		if (this.plugin.cooldown.getTimeRemaining(player) > 0) {
			this.plugin.messages.sendPlayerMessage(player, "cooldown");
			return;
		}
		
		overworldname = world.getName().replaceFirst("_nether$", "");
		overworld = Bukkit.getWorld(overworldname);
		
		// if from-nether is enabled in config and player is in nether, try to get overworld spawn location
		if (plugin.getConfig().getBoolean("from-nether", false) &&
				world.getName().endsWith("_nether") &&
				overworld != null) {
			world = overworld;
			spawnLoc = world.getSpawnLocation();
		}
		
		overworldname = world.getName().replaceFirst("_the_end$", "");
		overworld = Bukkit.getWorld(overworldname);

		// if from-end is enabled in config, and player is in end, try to get overworld spawn location 
		if (plugin.getConfig().getBoolean("from-end", false) &&
				world.getName().endsWith("_the_end") &&
				overworld != null) {
			world = overworld;
			spawnLoc = world.getSpawnLocation();
		}
		
		// if player is less than config min-distance from spawn, send player min-distance message and return
		if (player.getWorld() == world && spawnLoc.distance(player.getLocation()) < plugin.getConfig().getInt("mindistance", 10)) {
			plugin.messages.sendPlayerMessage(player, "min-distance");
			return;
		}
		
		// send player respawn message
		plugin.messages.sendPlayerMessage(player, "respawn");
		
		// if log-use is enabled in config, write log entry
		if (plugin.getConfig().getBoolean("log-use", true)) {
			
			// construct log message
			String log_message = player.getName() + " just used a " + config_itemname + " in " + player.getWorld().getName() + ".";
			
			// strip color codes from log message
			log_message = log_message.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "");
			
			// write message to log
			plugin.getLogger().info(log_message);
		}
		
		// teleport player to spawn location
		player.teleport(spawnLoc);
		
		// remove one SpawnStar item from player inventory
		ItemStack remove_item = player_item;
		remove_item.setAmount(player_item.getAmount() - 1);
		player.setItemInHand(remove_item);
		
		// if lightning is enabled in config, strike lightning at spawn location
		if (plugin.getConfig().getBoolean("lightning", true)) {
			world.strikeLightningEffect(spawnLoc);
		}
		
		// set player cooldown
		plugin.cooldown.setPlayerCooldown(player);
	}

	
	/**
	 * Test if player world is enabled in config
	 * @param player
	 * @return
	 */
	private boolean playerWorldEnabled(Player player) {
		
		// get string list of enabled worlds from config
		List<String> enabledworlds = plugin.getConfig().getStringList("enabled-worlds");
		
		// if player current world is in list of enabled worlds, return true
		if (enabledworlds.contains(player.getWorld().getName())) {
			return true;
		}
		// otherwise return false
		return false;
	}
}
