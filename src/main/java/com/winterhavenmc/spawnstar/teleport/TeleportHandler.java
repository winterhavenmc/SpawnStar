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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.winterhavenmc.util.TimeUnit.SECONDS;


/**
 * Class that manages player teleportation, including warmup and cooldown.
 */
public final class TeleportHandler {

	// reference to main class
	private final PluginMain plugin;

	// Map of player UUID and cooldown expire time in milliseconds
	private final CooldownMap cooldownMap;

	// Map of player UUID as key and warmup task id as value
	private final WarmupMap warmupMap;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to plugin main class
	 */
	public TeleportHandler(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// initialize cooldown map
		cooldownMap = new CooldownMap(plugin);

		// initialize warmup map
		warmupMap = new WarmupMap(plugin);
	}


	/**
	 * Start the player teleport
	 *
	 * @param player the player being teleported
	 */
	public void initiateTeleport(final Player player) {

		// check for null parameter
		if (player == null) {
			return;
		}

		// get player item in main hand
		final ItemStack playerItem = player.getInventory().getItemInMainHand();

		// if player cooldown has not expired, send player cooldown message and return
		if (cooldownMap.isCoolingDown(player)) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_COOLDOWN)
					.setMacro(Macro.DURATION, cooldownMap.getCooldownTimeRemaining(player))
					.send();
			return;
		}

		// if player is warming up, do nothing and return
		if (plugin.teleportHandler.isWarmingUp(player)) {
			return;
		}

		// get player world
		World playerWorld = player.getWorld();

		// get spawn location from world manager
		Location location = plugin.worldManager.getSpawnLocation(playerWorld);

		// if from-nether is enabled in config and player is in nether, try to get overworld spawn location
		if (plugin.getConfig().getBoolean("from-nether") && isInNetherWorld(player)) {
			location = getOverworldSpawnLocation(player).orElse(location);
		}

		// if from-end is enabled in config and player is in end, try to get overworld spawn location
		else if (plugin.getConfig().getBoolean("from-end") && isInEndWorld(player)) {
			location = getOverworldSpawnLocation(player).orElse(location);
		}

		// if player is less than config min-distance from destination, send player min-distance message and return
		if (isUnderMinimumDistance(player, location)) {
			plugin.messageBuilder.build(player, MessageId.TELEPORT_FAIL_MIN_DISTANCE)
					.setMacro(Macro.WORLD, location.getWorld())
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
					.setMacro(Macro.WORLD, location.getWorld())
					.setMacro(Macro.DURATION, SECONDS.toMillis(warmupTime))
					.send();

			// if enabled, play sound effect
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_WARMUP);
		}

		// initiate delayed teleport for player to destination
		BukkitTask teleportTask = new DelayedTeleportTask(plugin, player, location, playerItem.clone())
				.runTaskLater(plugin, SECONDS.toTicks(plugin.getConfig().getLong("teleport-warmup")));

		// insert player and taskId into warmup hashmap
		warmupMap.startPlayerWarmUp(player, teleportTask.getTaskId());

		// write log entry if configured
		logUsage(player);
	}


	/**
	 * Cancel pending player teleport
	 *
	 * @param player the player whose teleport will be cancelled
	 */
	public void cancelTeleport(final Player player) {

		// if player is in warmup hashmap, cancel delayed teleport task and remove player from warmup hashmap
		if (warmupMap.containsPlayer(player)) {

			// get delayed teleport task id
			int taskId = warmupMap.getTaskId(player);

			// cancel delayed teleport task
			plugin.getServer().getScheduler().cancelTask(taskId);

			// remove player from warmup hashmap
			removeWarmingUpPlayer(player);
		}
	}


	/**
	 * Insert player uuid into cooldown hashmap with expireTime as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 *
	 * @param player the player whose uuid will be added to the cooldown map
	 */
	void startPlayerCooldown(final Player player) {
		cooldownMap.startPlayerCooldown(player);
	}


	/**
	 * Check if a player is in a nether world
	 *
	 * @param player the player
	 * @return true if player is in a nether world, false if not
	 */
	private boolean isInNetherWorld(final Player player) {
		return player.getWorld().getEnvironment().equals(World.Environment.NETHER);
	}


	/**
	 * Check if a player is in an end world
	 *
	 * @param player the player
	 * @return true if player is in an end world, false if not
	 */
	private boolean isInEndWorld(final Player player) {
		return player.getWorld().getEnvironment().equals(World.Environment.THE_END);
	}


	/**
	 * Get overworld spawn location corresponding to a player nether or end world.
	 *
	 * @param player the passed player whose current world will be used to find a matching over world spawn location
	 * @return {@link Optional} wrapped spawn location of the normal world associated with the passed player
	 * nether or end world, or the current player world spawn location if no matching normal world found
	 */
	private Optional<Location> getOverworldSpawnLocation(final Player player) {

		// check for null parameter
		if (player == null) {
			return Optional.empty();
		}

		// create list to store normal environment worlds
		List<World> normalWorlds = new ArrayList<>();

		// iterate through all server worlds
		for (World checkWorld : plugin.getServer().getWorlds()) {

			// if world is normal environment, try to match name to passed world
			if (checkWorld.getEnvironment().equals(World.Environment.NORMAL)) {

				// check if normal world matches passed world minus nether/end suffix
				if (checkWorld.getName().equals(player.getWorld().getName().replaceFirst("(_nether$|_the_end$)", ""))) {
					return Optional.of(plugin.worldManager.getSpawnLocation(checkWorld));
				}

				// if no match, add to list of normal worlds
				normalWorlds.add(checkWorld);
			}
		}

		// if only one normal world exists, return that world
		if (normalWorlds.size() == 1) {
			return Optional.of(normalWorlds.get(0).getSpawnLocation());
		}

		// if no matching normal world found and more than one normal world exists, return passed world spawn location
		return Optional.of(player.getWorld().getSpawnLocation());
	}


	/**
	 * Check if player is within configured minimum distance from location
	 *
	 * @param player   the player
	 * @param location the location
	 * @return true if under minimum distance, false if not
	 */
	private boolean isUnderMinimumDistance(final Player player, final Location location) {
		return location != null
				&& location.getWorld() != null
				&& player.getWorld().equals(location.getWorld())
				&& player.getLocation().distanceSquared(location) < Math.pow(plugin.getConfig().getInt("minimum-distance"), 2);
	}


	/**
	 * Check if player is in teleport initiated set. Public pass through method.
	 *
	 * @param player the player to check if teleport is initiated
	 * @return {@code true} if teleport been initiated, {@code false} if it has not
	 */
	public boolean isInitiated(final Player player) {
		return warmupMap.isInitiated(player);
	}


	/**
	 * Test if player uuid is in warmup hashmap. Public pass through method.
	 *
	 * @param player the player to test if in warmup map
	 * @return {@code true} if player is in warmup map, {@code false} if not
	 */
	public boolean isWarmingUp(final Player player) {
		return warmupMap.isWarmingUp(player);
	}


	/**
	 * Remove player uuid from warmup hashmap. Public pass through method.
	 *
	 * @param player the player to remove from the warmup map
	 */
	void removeWarmingUpPlayer(final Player player) {
		warmupMap.removePlayer(player);
	}


	/**
	 * Log usage of SpawnStar item if configured
	 *
	 * @param player the player being logged using a SpawnStar item
	 */
	private void logUsage(final Player player) {
		if (plugin.getConfig().getBoolean("log-use")) {

			// get console command sender
			CommandSender console = plugin.getServer().getConsoleSender();

			// write message to log
			console.sendMessage(player.getName() + ChatColor.RESET + " used a "
					+ plugin.messageBuilder.getItemName() + ChatColor.RESET + " in "
					+ plugin.worldManager.getWorldName(player) + ChatColor.RESET + ".");
		}
	}

}
