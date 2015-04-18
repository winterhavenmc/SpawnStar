package com.winterhaven_mc.spawnstar;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;


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
		this.spawnStar = new SpawnStarStack(1);
	}

	
	/**
	 * Player interact event listener
	 */
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {

		// get player
		final Player player = event.getPlayer();
		
		// if cancel-on-interact is configured true, check if player is in warmup hashmap
		if (plugin.getConfig().getBoolean("cancel-on-interact",false)) {
			
			// if player is in warmup hashmap, check if they are interacting with a block (not air)
			if (plugin.warmupManager.isWarmingUp(player)) {

				// if player is interacting with a block, cancel teleport, output message and return
				if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {			
					plugin.warmupManager.cancelTeleport(player);
					plugin.messageManager.sendPlayerMessage(player, "interact-cancelled");
					return;
				}
			}
		}
		
		// get players item in hand
		ItemStack playerItem = player.getItemInHand();

		// if item used is not a spawnstar, do nothing and return
		if (!this.spawnStar.isSimilar(playerItem)) {
			return;
		}
		
		// if event action is not a left or right click, do nothing and return
		if (event.getAction() == Action.PHYSICAL) {
			return;
		}
		
		// if players current world is not enabled in config, do nothing and return
		if (!this.playerWorldEnabled(player)) {
			return;
		}
		
		// if player does not have spawnstar.use permission, send message and return
		if (!player.hasPermission("spawnstar.use")) {
			plugin.messageManager.sendPlayerMessage(player, "deniedpermission");
			return;
		}
		
		// if shift-click is configured true and player is not sneaking, send message and return
		if (plugin.getConfig().getBoolean("shift-click",false) && !event.getPlayer().isSneaking()) {
			plugin.messageManager.sendPlayerMessage(player, "shift-click-usage");
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
		if (plugin.warmupManager.isWarmingUp(player)) {
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
		
		// if remove-from-inventory is configured on-use, take one spawn star item from inventory now
		if (plugin.getConfig().getString("remove-from-inventory","on-use").equalsIgnoreCase("on-use")) {
			ItemStack removeItem = playerItem;
			removeItem.setAmount(playerItem.getAmount() - 1);
			player.setItemInHand(removeItem);
		}
		
		// if warmup setting is greater than zero, send warmup message
		if (plugin.getConfig().getInt("warmup",0) > 0) {
			plugin.messageManager.sendPlayerMessage(player, "warmup");
		}
		
		// initiate delayed teleport for player to spawn location
		BukkitTask teleportTask = new DelayedTeleportTask(player, spawnLocation).runTaskLater(plugin, plugin.getConfig().getInt("warmup",0) * 20);
		
		// insert player and taskId into warmup hashmap
		plugin.warmupManager.putPlayer(player, teleportTask.getTaskId());
		
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
		plugin.warmupManager.removePlayer(player);
		
	}

	
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		plugin.warmupManager.removePlayer(player);
		
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
		if (event.getInventory().containsAtLeast(this.spawnStar, 1)) {
			event.getInventory().setResult(null);
		}
		
	}
	
	
	/**
	 * Event listener for EntityDamageByEntity event<br>
	 * Cancels pending teleport if player takes damage
	 * @param event
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		
		// if event is already cancelled, do nothing and return
		if (event.isCancelled()) {
			return;
		}
		
		// if cancel-on-damage configuration is true, check if damaged entity is player
		if (plugin.getConfig().getBoolean("cancel-on-damage", false)) {
			
			Entity entity = event.getEntity();

			// if damaged entity is player, check for pending teleport
			if (entity instanceof Player) {
				
				// if player is in warmup hashmap, cancel teleport and send player message
				if (plugin.warmupManager.isWarmingUp((Player) entity)) {
					plugin.warmupManager.cancelTeleport((Player) entity);
					plugin.messageManager.sendPlayerMessage((Player) entity, "damage-cancelled");
				}				
			}
		}
	}
	
	
	@EventHandler
	public void onPlayerMovement(PlayerMoveEvent event) {
				
		// if cancel-on-movement configuration is false, do nothing and return
		if (!plugin.getConfig().getBoolean("cancel-on-movement", false)) {
			return;
		}
			
		Player player = event.getPlayer();

		// if player is in warmup hashmap, cancel teleport and send player message
		if (plugin.warmupManager.isWarmingUp(player)) {

			// check for player movement other than head turning
			if (event.getFrom().distance(event.getTo()) > 0) {
				plugin.warmupManager.cancelTeleport(player);
				plugin.messageManager.sendPlayerMessage(player,
						"movement-cancelled");
			}
		}
	}

	
	/**
	 * Test if player world is enabled in config
	 * @param player
	 * @return
	 */
	private boolean playerWorldEnabled(Player player) {
		
		// get string list of enabled worlds from config
		List<String> enabledWorlds = plugin.getConfig().getStringList("enabled-worlds");
		
		// if player current world is in list of enabled worlds, return true
		if (enabledWorlds.contains(player.getWorld().getName())) {
			return true;
		}
		
		// get string list of disabled worlds from config
		List<String> disabledworlds = plugin.getConfig().getStringList("disabled-worlds");
		
		// if player current world is in list of disabled worlds, return false
		if (disabledworlds.contains(player.getWorld().getName())) {
			return false;
		}
		
		// if enabled worlds list is empty, return true
		if (enabledWorlds.isEmpty()) {
			return true;
		}

		// otherwise return false
		return false;
	}

}
