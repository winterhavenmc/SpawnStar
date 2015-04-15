package com.winterhaven_mc.spawnstar;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Implements player event listener for <code>SpawnStar</code> events.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class PlayerEventListener implements Listener {

	// reference to main class
	private final SpawnStarMain plugin;
	ItemStack spawnStar;
	
	
	/**
	 * constructor method for <code>PlayerEventListener</code> class
	 * 
	 * @param	plugin		A reference to this plugin's main class
	 */
	public PlayerEventListener(SpawnStarMain plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	
	/**
	 * Player interact event listener
	 */
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {

		final Player player = event.getPlayer();
		ItemStack playerItem = player.getItemInHand();
//		ItemStack spawnStar = new SpawnStarStack(1);

		// if item used is not a spawnstar, do nothing and return
		if (!plugin.referenceItem.isSimilar(playerItem)) {
			return;
		}
		
		// if event action is not a left or right click, do nothing and return
		if (event.getAction() == Action.PHYSICAL) {
			return;
		}
		
		// if shift-click is configured true and player is not sneaking, do nothing and return
		if (plugin.getConfig().getBoolean("shift-click",false) && !event.getPlayer().isSneaking()) {
			return;
		}
		
		// if players current world is not enabled in config, do nothing and return
		if (!this.playerWorldEnabled(player)) {
			return;
		}
		
		// if player does not have spawnstar.use permission, do nothing and return
		if (!player.hasPermission("spawnstar.use")) {
			plugin.messageManager.sendPlayerMessage(player, "deniedpermission");
			return;
		}
		
		// cancel event
		event.setCancelled(true);
		
		// if player cooldown has not expired, send player cooldown message and return
		if (plugin.cooldownManager.getTimeRemaining(player) > 0) {
			plugin.messageManager.sendPlayerMessage(player, "cooldown");
			return;
		}
		
		// if player is warming up, do nothing and return
		if (plugin.cooldownManager.isWarmingUp(player)) {
			return;
		}
		
		World playerWorld = player.getWorld();
		String overworldName = playerWorld.getName().replaceFirst("(_nether|_the_end)$", "");
		World overworld = plugin.getServer().getWorld(overworldName);
		Location spawnLocation = playerWorld.getSpawnLocation();
		
		// if from-nether is enabled in config and player is in nether, try to get overworld spawn location
		if (plugin.getConfig().getBoolean("from-nether", false) &&
				playerWorld.getName().endsWith("_nether") &&
				overworld != null) {
			playerWorld = overworld;
			spawnLocation = playerWorld.getSpawnLocation();
			if (plugin.debug) {
				plugin.getLogger().info("Player in nether world, trying to respawn in " + playerWorld.getName());
			}
		}
		
		// if from-end is enabled in config, and player is in end, try to get overworld spawn location 
		if (plugin.getConfig().getBoolean("from-end", false) &&
				playerWorld.getName().endsWith("_the_end") &&
				overworld != null) {
			playerWorld = overworld;
			spawnLocation = playerWorld.getSpawnLocation();
			if (plugin.debug) {
				plugin.getLogger().info("Player in end world, trying to respawn in " + playerWorld.getName());
			}
		}
		
		// if player is less than config min-distance from spawn, send player min-distance message and return
		if (player.getWorld() == playerWorld && spawnLocation.distance(player.getLocation()) < plugin.getConfig().getInt("mindistance", 10)) {
			plugin.messageManager.sendPlayerMessage(player, "min-distance");
			return;
		}
		
		// remove one SpawnStar item from player inventory
		ItemStack remove_item = playerItem;
		remove_item.setAmount(playerItem.getAmount() - 1);
		player.setItemInHand(remove_item);
		
		if (plugin.getConfig().getInt("warmup",0) > 0) {
			plugin.messageManager.sendPlayerMessage(player, "warmup");
		}
		
		// initiate delayed teleport for player to spawn location
		new DelayedTeleport(plugin, player, spawnLocation).runTaskLater(plugin, plugin.getConfig().getInt("warmup",0) * 20);
		
		// if log-use is enabled in config, write log entry
		if (plugin.getConfig().getBoolean("log-use", true)) {
			
			// construct log message
			String configItemName = plugin.messageManager.getItemName();
			String log_message = player.getName() + " just used a " + configItemName + " in " + player.getWorld().getName() + ".";
			
			// strip color codes from log message
			log_message = log_message.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "");
			
			// write message to log
			plugin.getLogger().info(log_message);
		}
		
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player player = (Player)event.getEntity();
		plugin.cooldownManager.removePlayerWarmup(player);
		
	}

	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		plugin.cooldownManager.removePlayerWarmup(player);
		
	}
	
	
	/**
	 * Prevent spawnstar items from being used in crafting recipes if configured
	 * @param event
	 */
	@EventHandler
	public void onCraftPrepare(PrepareItemCraftEvent event) {

		// if allow-crafting is true in configuration, do nothing and return
		if (plugin.getConfig().getBoolean("allow-crafting",false)) {
			return;
		}

		// if crafting inventory contains spawnstar item, set result item to null
		if (event.getInventory().containsAtLeast(plugin.referenceItem, 1)) {
			event.getInventory().setResult(null);
		}
		
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
