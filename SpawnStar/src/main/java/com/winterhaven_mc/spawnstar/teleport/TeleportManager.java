package com.winterhaven_mc.spawnstar.teleport;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.messages.MessageId;
import com.winterhaven_mc.spawnstar.sounds.SoundId;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public final class TeleportManager {

	// reference to main class
	private final PluginMain plugin;

	// Map to store player UUID and cooldown expire time in milliseconds
	private final Map<UUID, Long> cooldownMap;

	// Map containing player UUID as key and warmup task id as value
	private final Map<UUID, Integer> warmupMap;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public TeleportManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// initialize cooldown map
		cooldownMap = new ConcurrentHashMap<>();

		// initialize warmup map
		warmupMap = new ConcurrentHashMap<>();
	}


	/**
	 * Start the player teleport
	 *
	 * @param player the player being teleported
	 */
	public final void initiateTeleport(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get player item in main hand
		final ItemStack playerItem = player.getInventory().getItemInMainHand();

		// if player cooldown has not expired, send player cooldown message and return
		if (plugin.teleportManager.getCooldownTimeRemaining(player) > 0) {
			plugin.messageManager.sendMessage(player, MessageId.TELEPORT_COOLDOWN);
			return;
		}

		// if player is warming up, do nothing and return
		if (plugin.teleportManager.isWarmingUp(player)) {
			return;
		}

		// get player world
		World playerWorld = player.getWorld();

		// get spawn location from world manager
		Location destination = plugin.worldManager.getSpawnLocation(playerWorld);

		// if player is in nether, get over world if configured
		if (playerWorld.getName().endsWith("_nether")
				&& plugin.getConfig().getBoolean("from-nether")) {
			String overWorldName = playerWorld.getName().replace("_nether","");
			World overWorld = plugin.getServer().getWorld(overWorldName);
			if (overWorld != null) {
				destination = plugin.worldManager.getSpawnLocation(overWorld);
			}
		}
		// if player is in end, get over world if configured
		else if (playerWorld.getName().endsWith("_the_end")
				&& plugin.getConfig().getBoolean("from-end")) {
			String overWorldName = playerWorld.getName().replace("_the_end","");
			World overWorld = plugin.getServer().getWorld(overWorldName);
			if (overWorld != null) {
				destination = plugin.worldManager.getSpawnLocation(overWorld);
			}
		}

		// if player is less than config min-distance from destination, send player min-distance message and return
		if (player.getWorld().equals(destination.getWorld())
				&& destination.distance(player.getLocation()) < plugin.getConfig().getInt("minimum-distance")) {
			plugin.messageManager.sendMessage(player, MessageId.TELEPORT_FAIL_MIN_DISTANCE, destination);
			return;
		}

		// if remove-from-inventory is configured on-use, take one spawn star item from inventory now
		if (plugin.getConfig().getString("remove-from-inventory").equalsIgnoreCase("on-use")) {
			playerItem.setAmount(playerItem.getAmount() - 1);
			player.getInventory().setItemInMainHand(playerItem);
		}

		// if warmup setting is greater than zero, send warmup message
		if (plugin.getConfig().getInt("teleport-warmup") > 0) {
			plugin.messageManager.sendMessage(player, MessageId.TELEPORT_WARMUP, destination);

			// if enabled, play sound effect
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_WARMUP);
		}

		// initiate delayed teleport for player to destination
		BukkitTask teleportTask =
				new DelayedTeleportTask(player,
						destination,
						playerItem.clone()).runTaskLater(plugin, plugin.getConfig().getInt("teleport-warmup") * 20);

		// insert player and taskId into warmup hashmap
		plugin.teleportManager.putWarmup(player, teleportTask.getTaskId());

		// if log-use is enabled in config, write log entry
		if (plugin.getConfig().getBoolean("log-use")) {

			// write message to log
			plugin.getLogger().info(player.getName() + ChatColor.RESET + " used a "
					+ plugin.messageManager.getItemName() + ChatColor.RESET + " in "
					+ plugin.messageManager.getWorldName(player) + ChatColor.RESET + ".");
		}
	}


	/**
	 * Insert player uuid and taskId into warmup hashmap.
	 *
	 * @param player the player whose uuid will be used as the key in the warmup map
	 * @param taskId the warmup task Id to be placed in the warmup map
	 */
	private void putWarmup(final Player player, final int taskId) {

		// check for null parameter
		Objects.requireNonNull(player);

		warmupMap.put(player.getUniqueId(), taskId);
	}


	/**
	 * Remove player uuid from warmup hashmap
	 *
	 * @param player the player whose uuid will be removed from the warmup map
	 */
	final void removeWarmup(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// remove player uuid from warm up map
		warmupMap.remove(player.getUniqueId());
	}


	/**
	 * Test if player uuid is in warmup hashmap
	 *
	 * @param player the player whose uuid is to be checked for existence in the warmup map
	 * @return {@code true} if player uuid is in the warmup map, {@code false} if it is not
	 */
	public final boolean isWarmingUp(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		return warmupMap.containsKey(player.getUniqueId());
	}


	/**
	 * Cancel pending player teleport
	 *
	 * @param player the player whose teleport will be cancelled
	 */
	public final void cancelTeleport(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// if player is in warmup hashmap, cancel delayed teleport task and remove player from warmup hashmap
		if (warmupMap.containsKey(player.getUniqueId())) {

			// get delayed teleport task id
			Integer taskId = warmupMap.get(player.getUniqueId());

			// cancel delayed teleport task
			if (taskId != null) {
				plugin.getServer().getScheduler().cancelTask(taskId);
			}

			// remove player from warmup hashmap
			warmupMap.remove(player.getUniqueId());
		}
	}


	/**
	 * Insert player uuid into cooldown hashmap with expireTime as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 *
	 * @param player the player whose uuid will be added to the cooldown map
	 */
	final void startCooldown(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get cooldown time in seconds from config
		final int cooldownSeconds = plugin.getConfig().getInt("teleport-cooldown");

		// set expireTime to current time + configured cooldown period, in milliseconds
		final Long expireTime = System.currentTimeMillis() + (TimeUnit.SECONDS.toMillis(cooldownSeconds));

		// put in cooldown map with player UUID as key and expireTime as value
		cooldownMap.put(player.getUniqueId(), expireTime);

		// schedule task to remove player from cooldown map
		new BukkitRunnable() {
			public void run() {
				cooldownMap.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, (cooldownSeconds * 20));
	}


	/**
	 * Get time remaining for player cooldown
	 *
	 * @param player the player whose cooldown time remaining to retrieve
	 * @return long remainingTime in milliseconds
	 */
	public final long getCooldownTimeRemaining(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// initialize remainingTime
		long remainingTime = 0;

		// if player is in cooldown map, set remainTime to map value
		if (cooldownMap.containsKey(player.getUniqueId())) {
			remainingTime = (cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis());
		}
		return remainingTime;
	}

}
