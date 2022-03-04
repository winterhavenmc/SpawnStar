/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.spawnstar.teleport;

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.messages.Macro;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.winterhavenmc.spawnstar.util.BukkitTime.SECONDS;


/**
 * Class that manages player teleportation, including warmup and cooldown.
 */
public final class TeleportManager {

	// reference to main class
	private final PluginMain plugin;

	// Map of player UUID and cooldown expire time in milliseconds
	private final Map<UUID, Long> cooldownMap;

	// Map of player UUID as key and warmup task id as value
	private final Map<UUID, Integer> warmupMap;

	// Set containing player uuid for teleport initiated, removed by task
	private final Set<UUID> teleportInitiated;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to plugin main class
	 */
	public TeleportManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// initialize cooldown map
		cooldownMap = new ConcurrentHashMap<>();

		// initialize warmup map
		warmupMap = new ConcurrentHashMap<>();

		// initialize tpi set
		teleportInitiated = ConcurrentHashMap.newKeySet();
	}


	/**
	 * Start the player teleport
	 *
	 * @param player the player being teleported
	 */
	public void initiateTeleport(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get player item in main hand
		final ItemStack playerItem = player.getInventory().getItemInMainHand();

		// if player cooldown has not expired, send player cooldown message and return
		if (plugin.teleportManager.getCooldownTimeRemaining(player) > 0) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_COOLDOWN)
					.setMacro(Macro.DURATION, getCooldownTimeRemaining(player))
					.send();
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
		if (playerWorld.getEnvironment().equals(World.Environment.NETHER)
				&& plugin.getConfig().getBoolean("from-nether")) {
			destination = getOverWorld(playerWorld).getSpawnLocation();
		}
		// if player is in end, get over world if configured
		else if (playerWorld.getEnvironment().equals(World.Environment.THE_END)
				&& plugin.getConfig().getBoolean("from-end")) {
			destination = getOverWorld(playerWorld).getSpawnLocation();
		}

		// if player is less than config min-distance from destination, send player min-distance message and return
		if (player.getWorld().equals(destination.getWorld())
				&& destination.distance(player.getLocation()) < plugin.getConfig().getInt("minimum-distance")) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_FAIL_MIN_DISTANCE)
					.setMacro(Macro.WORLD, destination.getWorld())
					.send();
			return;
		}

		// if remove-from-inventory is configured on-use, take one spawn star item from inventory now
		if ("on-use".equalsIgnoreCase(plugin.getConfig().getString("remove-from-inventory"))) {
			playerItem.setAmount(playerItem.getAmount() - 1);
			player.getInventory().setItemInMainHand(playerItem);
		}

		// if warmup setting is greater than zero, send warmup message
		long warmupTime = plugin.getConfig().getLong("teleport-warmup");
		if (warmupTime > 0) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_WARMUP)
					.setMacro(Macro.WORLD, destination.getWorld())
					.setMacro(Macro.DURATION, SECONDS.toMillis(warmupTime))
					.send();

			// if enabled, play sound effect
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_WARMUP);
		}

		// initiate delayed teleport for player to destination
		BukkitTask teleportTask =
				new DelayedTeleportTask(plugin, player, destination, playerItem.clone())
						.runTaskLater(plugin, SECONDS.toTicks(plugin.getConfig().getLong("teleport-warmup")));

		// insert player and taskId into warmup hashmap
		putWarmup(player, teleportTask.getTaskId());

		// write log entry if configured
		logUsage(player);
	}


	/**
	 * Insert player uuid and taskId into warmup hashmap.
	 *
	 * @param player the player whose uuid will be used as the key in the warmup map
	 * @param taskId the warmup task Id to be placed in the warmup map
	 */
	void putWarmup(final Player player, final int taskId) {

		// check for null parameter
		Objects.requireNonNull(player);

		// put player uuid, taskId in warmup map
		warmupMap.put(player.getUniqueId(), taskId);

		// insert player uuid into teleport initiated set
		teleportInitiated.add(player.getUniqueId());

		// create task to remove player uuid from tpi set after 2 ticks
		new BukkitRunnable() {
			@Override
			public void run() {
				teleportInitiated.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, plugin.getConfig().getLong("interact-delay"));

	}


	/**
	 * Remove player uuid from warmup hashmap
	 *
	 * @param player the player whose uuid will be removed from the warmup map
	 */
	void removeWarmup(final Player player) {

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
	public boolean isWarmingUp(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		return warmupMap.containsKey(player.getUniqueId());
	}


	/**
	 * Cancel pending player teleport
	 *
	 * @param player the player whose teleport will be cancelled
	 */
	public void cancelTeleport(final Player player) {

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
			removeWarmup(player);
		}
	}


	/**
	 * Insert player uuid into cooldown hashmap with expireTime as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 *
	 * @param player the player whose uuid will be added to the cooldown map
	 */
	void startCooldown(final Player player) {

		// check for null parameter
		Objects.requireNonNull(player);

		// get cooldown time in seconds from config
		final long cooldownSeconds = plugin.getConfig().getLong("teleport-cooldown");

		// set expireTime to current time + configured cooldown period, in milliseconds
		final long expireTime = System.currentTimeMillis() + (SECONDS.toMillis(cooldownSeconds));

		// put in cooldown map with player UUID as key and expireTime as value
		cooldownMap.put(player.getUniqueId(), expireTime);

		// schedule task to remove player from cooldown map
		new BukkitRunnable() {
			public void run() {
				cooldownMap.remove(player.getUniqueId());
			}
		}.runTaskLater(plugin, SECONDS.toTicks(cooldownSeconds));
	}


	public boolean isCoolingDown(final Player player) {
		return getCooldownTimeRemaining(player) > 0;
	}


	/**
	 * Get time remaining for player cooldown
	 *
	 * @param player the player whose cooldown time remaining to retrieve
	 * @return long remainingTime in milliseconds
	 */
	public long getCooldownTimeRemaining(final Player player) {

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


	/**
	 * Attempt to get normal world associated with passed nether or end world
	 *
	 * @param passedWorld the passed world from which to evince an over world
	 * @return the normal world associated with passed nether or end world,
	 * or passed world if no matching normal world found
	 */
	private World getOverWorld(final World passedWorld) {

		// check for null parameter
		Objects.requireNonNull(passedWorld);

		// create list to store normal environment worlds
		List<World> normalWorlds = new ArrayList<>();

		// iterate through all server worlds
		for (World checkWorld : plugin.getServer().getWorlds()) {

			// if world is normal environment, try to match name to passed world
			if (checkWorld.getEnvironment().equals(World.Environment.NORMAL)) {

				// check if normal world matches passed world minus nether/end suffix
				if (checkWorld.getName().equals(passedWorld.getName()
						.replaceFirst("(_nether$|_the_end$)", ""))) {
					return checkWorld;
				}

				// if no match, add to list of normal worlds
				normalWorlds.add(checkWorld);
			}
		}

		// if only one normal world exists, return that world
		if (normalWorlds.size() == 1) {
			return normalWorlds.get(0);
		}

		// if no matching normal world found and more than one normal world exists, return passed world
		return passedWorld;
	}


	/**
	 * Check if player is in teleport initiated set
	 *
	 * @param player the player to check if teleport is initiated
	 * @return {@code true} if teleport been initiated, {@code false} if it has not
	 */
	public boolean isInitiated(final Player player) {

		// check for null parameter
		if (player == null) {
			return false;
		}

		return !teleportInitiated.contains(player.getUniqueId());
	}


	/**
	 * Log usage of SpawnStar item if configured
	 *
	 * @param player the player being logged using a SpawnStar item
	 */
	private void logUsage(final Player player) {
		if (plugin.getConfig().getBoolean("log-use")) {

			// write message to log
			plugin.getLogger().info(player.getName() + ChatColor.RESET + " used a "
					+ plugin.messageBuilder.getItemName() + ChatColor.RESET + " in "
					+ plugin.worldManager.getWorldName(player) + ChatColor.RESET + ".");
		}
	}

}
