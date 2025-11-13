/*
 * Copyright (c) 2022-2025 Tim Savage.
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

package com.winterhavenmc.spawnstar.adapters.listeners.bukkit;

import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.spawnstar.core.ports.listeners.PlayerEventListener;
import com.winterhavenmc.spawnstar.core.teleport.TeleportHandler;
import com.winterhavenmc.spawnstar.core.util.Macro;
import com.winterhavenmc.spawnstar.core.util.MessageId;

import com.winterhavenmc.spawnstar.core.util.SoundId;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Set;


/**
 * Implements player event listener for SpawnStar events
 */
public final class BukkitPlayerEventListener implements PlayerEventListener
{
	@org.jetbrains.annotations.NotNull
	private final Plugin plugin;
	private final MessageBuilder messageBuilder;
	private final TeleportHandler teleportHandler;

	// set to hold craft table materials
	private final Set<Material> craftTables = Set.of(
			Material.CARTOGRAPHY_TABLE,
			Material.CRAFTING_TABLE,
			Material.FLETCHING_TABLE,
			Material.SMITHING_TABLE,
			Material.LOOM,
			Material.STONECUTTER);


	/**
	 * Class constructor for PlayerEventListener
	 */
	public BukkitPlayerEventListener(final Plugin plugin, final MessageBuilder messageBuilder, final TeleportHandler teleportHandler)
	{
		this.plugin = plugin;
		this.messageBuilder = messageBuilder;
		this.teleportHandler = teleportHandler;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}


	/**
	 * PlayerInteract event handler
	 *
	 * @param event PlayerInteractEvent handled by this method
	 */
	@EventHandler
	void onPlayerInteract(final PlayerInteractEvent event)
	{
		// get player
		final Player player = event.getPlayer();

		// if cancel-on-interaction is configured true, check if player is in warmup hashmap
		// if player is in warmup hashmap, check if they are interacting with a block (not air)
		// if player is interacting with a block, cancel teleport, output message and return
		if (plugin.getConfig().getBoolean("cancel-on-interaction")
				&& teleportHandler.isWarmingUp(player)
				&& (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
		{
			// if player's last teleport initiated time is less than x ticks (def: 2), do nothing and return
			// this is a workaround for event double firing (once for each hand) on every player interaction
			if (teleportHandler.isInitiated(player))
			{
				// cancel teleport
				teleportHandler.cancelTeleport(player);

				// send cancelled teleport message
				messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_INTERACTION).send();
			}
			return;
		}

		// if item used is not a SpawnStar, do nothing and return
		if (!messageBuilder.items().isItem(event.getItem()))
		{
			return;
		}

		// get event action
		Action action = event.getAction();

		// if event action is PHYSICAL (not left-click or right click), do nothing and return
		if (action.equals(Action.PHYSICAL))
		{
			return;
		}

		// if event action is left-click, and left-click is config disabled, do nothing and return
		if (action.equals(Action.LEFT_CLICK_BLOCK)
				|| action.equals(Action.LEFT_CLICK_AIR)
				&& !plugin.getConfig().getBoolean("left-click"))
		{
			return;
		}

		// if player is not warming
		if (!teleportHandler.isWarmingUp(player))
		{
			// get clicked block
			Block block = event.getClickedBlock();

			// check if clicked block is air (null)
			// check that player is not sneaking, to interact with blocks
			if (block != null && !event.getPlayer().isSneaking())
			{
				// allow use of doors, gates and trap doors with item in hand
				if (block.getBlockData() instanceof Openable)
				{
					return;
				}

				// allow use of switches with item in hand
				if (block.getBlockData() instanceof Switch)
				{
					return;
				}

				// allow use of containers and other tile entity blocks with item in hand
				if (block.getState() instanceof TileState)
				{
					return;
				}

				// allow use of crafting tables with item in hand
				if (craftTables.contains(block.getType()))
				{
					return;
				}
			}

			// cancel event
			event.setCancelled(true);

			// if players current world is not enabled in config, send message and return
			if (!messageBuilder.worlds().isEnabled(player.getWorld().getUID()))
			{
				messageBuilder.compose(player, MessageId.TELEPORT_FAIL_WORLD_DISABLED).send();
				messageBuilder.sounds().play(player, SoundId.TELEPORT_DENIED_WORLD_DISABLED);
				return;
			}

			// if player does not have spawnstar.use permission, send message and return
			if (!player.hasPermission("spawnstar.use"))
			{
				messageBuilder.sounds().play(player, SoundId.TELEPORT_DENIED_PERMISSION);
				messageBuilder.compose(player, MessageId.TELEPORT_FAIL_PERMISSION)
						.setMacro(Macro.ITEM, event.getItem())
						.send();
				return;
			}

			// if shift-click configured and player is not sneaking, send message and return
			if (plugin.getConfig().getBoolean("shift-click")
					&& !player.isSneaking())
			{
				messageBuilder.compose(player, MessageId.TELEPORT_FAIL_SHIFT_CLICK)
						.setMacro(Macro.ITEM, event.getItem())
						.send();
				return;
			}

			// initiate teleport
			teleportHandler.initiateTeleport(player);
		}
	}


	/**
	 * Cancel pending teleport on player death
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onPlayerDeath(final PlayerDeathEvent event)
	{
		// get event player
		Player player = event.getEntity();

		// cancel any pending teleport for player
		teleportHandler.cancelTeleport(player);
	}


	/**
	 * Perform cleanup tasks when player logs off server
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onPlayerQuit(final PlayerQuitEvent event)
	{
		// get event player
		Player player = event.getPlayer();

		// cancel any pending teleport for player
		teleportHandler.cancelTeleport(player);
	}


	/**
	 * Prepare Item Craft event handler<br>
	 * Prevents SpawnStar items from being used in crafting recipes if configured
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	void onCraftPrepare(final PrepareItemCraftEvent event)
	{
		// if allow-in-recipes is true in configuration, do nothing and return
		if (plugin.getConfig().getBoolean("allow-in-recipes"))
		{
			return;
		}

		// if crafting inventory contains SpawnStar item, set result item to null
		for (ItemStack itemStack : event.getInventory())
		{
			if (messageBuilder.items().isItem(itemStack))
			{
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
	void onEntityDamage(final EntityDamageEvent event)
	{
		// if cancel-on-damage configuration is true, check if damaged entity is player
		if (plugin.getConfig().getBoolean("cancel-on-damage"))
		{
			Entity entity = event.getEntity();

			// if damaged entity is player, check for pending teleport
			if (entity instanceof Player player)
			{
				// if player is in warmup hashmap, cancel teleport and send player message
				if (teleportHandler.isWarmingUp(player))
				{
					teleportHandler.cancelTeleport(player);
					messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_DAMAGE).send();
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
	void onPlayerMovement(final PlayerMoveEvent event)
	{
		// if cancel-on-movement configuration is false, do nothing and return
		if (!plugin.getConfig().getBoolean("cancel-on-movement"))
		{
			return;
		}

		// get event player
		Player player = event.getPlayer();

		// if player is in warmup hashmap, cancel teleport and send player message
		if (teleportHandler.isWarmingUp(player))
		{
			// check for player movement other than head turning
			if (event.getTo() != null && event.getFrom().distance(event.getTo()) > 0)
			{
				teleportHandler.cancelTeleport(player);
				messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_MOVEMENT).send();
			}
		}
	}

}
