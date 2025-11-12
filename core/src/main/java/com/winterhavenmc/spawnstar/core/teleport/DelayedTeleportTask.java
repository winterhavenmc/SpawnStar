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

package com.winterhavenmc.spawnstar.core.teleport;

import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.spawnstar.core.util.Macro;
import com.winterhavenmc.spawnstar.core.util.MessageId;
import com.winterhavenmc.spawnstar.core.util.SoundId;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


/**
 * Class that extends BukkitRunnable to teleport a player to the world spawn location
 * after a configured warmup period.
 */
final class DelayedTeleportTask extends BukkitRunnable
{
	private final Plugin plugin;
	private final MessageBuilder messageBuilder;
	private final TeleportHandler teleportHandler;
	private final Player player;
	private final Location destination;
	private BukkitTask particleTask;
	private final ItemStack playerItem;


	/**
	 * Class constructor
	 *
	 * @param player      the player to be teleported
	 * @param destination the world spawn location
	 * @param playerItem  the player item used to initiate teleport
	 */
	DelayedTeleportTask(final Plugin plugin,
	                    final MessageBuilder messageBuilder,
						final TeleportHandler teleportHandler,
						final Player player,
						final Location destination,
						final ItemStack playerItem)
	{
		this.plugin = plugin;
		this.messageBuilder = messageBuilder;
		this.teleportHandler = teleportHandler;
		this.player = player;
		this.destination = destination;
		this.playerItem = playerItem;

		// start repeating task for generating particles at player location
		if (plugin.getConfig().getBoolean("particle-effects"))
		{
			// start particle task, with 2 tick delay so it doesn't self cancel on first run
			particleTask = new ParticleTask(teleportHandler, player).runTaskTimer(plugin, 2L, 10);
		}
	}


	@Override
	public void run()
	{
		// cancel particles task
		particleTask.cancel();

		// if player is in warmup map
		if (this.teleportHandler.isWarmingUp(player))
		{
			// remove player from warmup map
			teleportHandler.removeWarmingUpPlayer(player);

			// if remove-from-inventory is configured on-success, take one spawn star item from inventory now
			if ("on-success".equalsIgnoreCase(plugin.getConfig().getString("remove-from-inventory")))
			{
				// try to remove one SpawnStar item from player inventory
				boolean notRemoved = true;
				for (ItemStack itemStack : player.getInventory())
				{
					if (playerItem.isSimilar(itemStack))
					{
						ItemStack removeItem = itemStack.clone();
						removeItem.setAmount(1);
						player.getInventory().removeItem(removeItem);
						notRemoved = false;
						break;
					}
				}

				// if one SpawnStar item could not be removed from inventory, send message, set cooldown and return
				if (notRemoved)
				{
					messageBuilder.compose(player, MessageId.TELEPORT_CANCELLED_NO_ITEM).send();
					teleportHandler.startPlayerCooldown(player);
					return;
				}
			}

			// play pre-teleport sound if sound effects are enabled
			messageBuilder.sounds().play(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);

			// teleport player to destination
			player.teleport(destination);

			// send player respawn message
			messageBuilder.compose(player, MessageId.TELEPORT_SUCCESS)
					.setMacro(Macro.DESTINATION_WORLD, destination.getWorld())
					.send();

			// play post-teleport sound if sound effects are enabled
			messageBuilder.sounds().play(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning"))
			{
				player.getWorld().strikeLightningEffect(destination);
			}

			// start player cooldown
			teleportHandler.startPlayerCooldown(player);
		}
	}

}
