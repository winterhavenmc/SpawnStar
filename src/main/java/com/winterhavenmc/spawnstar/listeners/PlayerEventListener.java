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

package com.winterhavenmc.spawnstar.listeners;

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
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

import java.util.*;


/**
 * Implements player event listener for SpawnStar events
 */
public final class PlayerEventListener implements Listener {

	// reference to main class
	private final PluginMain plugin;

	// set to hold craft table materials
	private final Set<Material> craftTables = Set.of(
			Material.CARTOGRAPHY_TABLE,
			Material.CRAFTING_TABLE,
			Material.FLETCHING_TABLE,
			Material.SMITHING_TABLE,
			Material.LOOM,
			Material.STONECUTTER );


	/**
	 * Class constructor for PlayerEventListener
	 *
	 * @param plugin reference to this plugin's main class
	 */
	public PlayerEventListener(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// register events in this class
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	/**
	 * PlayerInteract event handler
	 *
	 * @param event PlayerInteractEvent handled by this method
	 */
	@EventHandler
	void onPlayerInteract(final PlayerInteractEvent event) {

		// get player
		final Player player = event.getPlayer();

		// if cancel-on-interaction is configured true, check if player is in warmup hashmap
		if (plugin.getConfig().getBoolean("cancel-on-interaction")) {

			// if player is in warmup hashmap, check if they are interacting with a block (not air)
			if (plugin.teleportHandler.isWarmingUp(player)) {

				// if player is interacting with a block, cancel teleport, output message and return
				if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

					// if player's last teleport initiated time is less than x ticks (def: 2), do nothing and return
					// this is a workaround for event double firing (once for each hand) on every player interaction
					if (!plugin.teleportHandler.isInitiated(player)) {
						return;
					}

					// cancel teleport
					plugin.teleportHandler.cancelTeleport(player);

					// send cancelled teleport message
					plugin.messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_INTERACTION).send();

					// play cancelled teleport sound
					plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
					return;
				}
			}
		}

		// if item used is not a SpawnStar, do nothing and return
		if (!plugin.spawnStarUtility.isItem(event.getItem())) {
			return;
		}

		// get event action
		Action action = event.getAction();

		// if event action is PHYSICAL (not left-click or right click), do nothing and return
		if (action.equals(Action.PHYSICAL)) {
			return;
		}

		// if event action is left-click, and left-click is config disabled, do nothing and return
		if (action.equals(Action.LEFT_CLICK_BLOCK)
				|| action.equals(Action.LEFT_CLICK_AIR)
				&& !plugin.getConfig().getBoolean("left-click")) {
			return;
		}

		// if player is not warming
		if (!plugin.teleportHandler.isWarmingUp(player)) {

			// get clicked block
			Block block = event.getClickedBlock();

			// check if clicked block is air (null)
			if (block != null) {

				// check that player is not sneaking, to interact with blocks
				if (!event.getPlayer().isSneaking()) {

					// allow use of doors, gates and trap doors with item in hand
					if (block.getBlockData() instanceof Openable) {
						return;
					}

					// allow use of switches with item in hand
					if (block.getBlockData() instanceof Switch) {
						return;
					}

					// allow use of containers and other tile entity blocks with item in hand
					if (block.getState() instanceof TileState) {
						return;
					}

					// allow use of crafting tables with item in hand
					if (craftTables.contains(block.getType())) {
						return;
					}
				}
			}

			// cancel event
			event.setCancelled(true);

			// if players current world is not enabled in config, send message and return
			if (!plugin.worldManager.isEnabled(player.getWorld())) {
				plugin.messageBuilder.compose(player, MessageId.TELEPORT_FAIL_WORLD_DISABLED).send();
				plugin.soundConfig.playSound(player, SoundId.TELEPORT_DENIED_WORLD_DISABLED);
				return;
			}

			// if player does not have spawnstar.use permission, send message and return
			if (!player.hasPermission("spawnstar.use")) {
				plugin.messageBuilder.compose(player, MessageId.TELEPORT_FAIL_PERMISSION).send();
				plugin.soundConfig.playSound(player, SoundId.TELEPORT_DENIED_PERMISSION);
				return;
			}

			// if shift-click configured and player is not sneaking, send message and return
			if (plugin.getConfig().getBoolean("shift-click")
					&& !player.isSneaking()) {
				plugin.messageBuilder.compose(player, MessageId.TELEPORT_FAIL_SHIFT_CLICK).send();
				return;
			}

			// initiate teleport
			plugin.teleportHandler.initiateTeleport(player);
		}
	}


	/**
	 * Cancel pending teleport on player death
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onPlayerDeath(final PlayerDeathEvent event) {

		// get event player
		Player player = event.getEntity();

		// cancel any pending teleport for player
		plugin.teleportHandler.cancelTeleport(player);
	}


	/**
	 * Perform cleanup tasks when player logs off server
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onPlayerQuit(final PlayerQuitEvent event) {

		// get event player
		Player player = event.getPlayer();

		// cancel any pending teleport for player
		plugin.teleportHandler.cancelTeleport(player);
	}


	/**
	 * Prepare Item Craft event handler<br>
	 * Prevents SpawnStar items from being used in crafting recipes if configured
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onCraftPrepare(final PrepareItemCraftEvent event) {

		// if allow-in-recipes is true in configuration, do nothing and return
		if (plugin.getConfig().getBoolean("allow-in-recipes")) {
			return;
		}

		// if crafting inventory contains SpawnStar item, set result item to null
		for (ItemStack itemStack : event.getInventory()) {
			if (plugin.spawnStarUtility.isItem(itemStack)) {
				event.getInventory().setResult(null);
			}
		}
	}


	/**
	 * Cancels pending teleport if player takes damage during warmup
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler(ignoreCancelled = true)
	void onEntityDamage(final EntityDamageEvent event) {

		// if cancel-on-damage configuration is true, check if damaged entity is player
		if (plugin.getConfig().getBoolean("cancel-on-damage")) {

			Entity entity = event.getEntity();

			// if damaged entity is player, check for pending teleport
			if (entity instanceof Player player) {

				// if player is in warmup hashmap, cancel teleport and send player message
				if (plugin.teleportHandler.isWarmingUp(player)) {
					plugin.teleportHandler.cancelTeleport(player);
					plugin.messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_DAMAGE).send();
					plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
				}
			}
		}
	}


	/**
	 * Cancels teleport if player moves during warmup
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onPlayerMovement(final PlayerMoveEvent event) {

		// if cancel-on-movement configuration is false, do nothing and return
		if (!plugin.getConfig().getBoolean("cancel-on-movement")) {
			return;
		}

		// get event player
		Player player = event.getPlayer();

		// if player is in warmup hashmap, cancel teleport and send player message
		if (plugin.teleportHandler.isWarmingUp(player)) {

			// check for player movement other than head turning
			if (event.getTo() != null && event.getFrom().distance(event.getTo()) > 0) {
				plugin.teleportHandler.cancelTeleport(player);
				plugin.messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_MOVEMENT).send();
				plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
			}
		}
	}

}
