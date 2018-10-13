package com.winterhaven_mc.spawnstar.teleport;

import com.winterhaven_mc.spawnstar.PluginMain;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.winterhaven_mc.spawnstar.messages.MessageId;
import com.winterhaven_mc.spawnstar.messages.SoundId;

final class DelayedTeleportTask extends BukkitRunnable {

	// reference to main class
	private final PluginMain plugin;
	
	// player being teleported
	private final Player player;
	
	// teleport destination
	private final Location destination;
	
	// teleport destination display name
	private final String destinationName;
	
	// particle task
	private BukkitTask particleTask;
	
	// SpawnStar item used by player
	private final ItemStack playerItem;

	
	/**
	 * Class constructor method
	 */
	DelayedTeleportTask(final Player player, final Location destination,
			final String destinationName, final ItemStack playerItem) {
		
		this.plugin = PluginMain.instance;
		this.player = player;
		this.playerItem = playerItem;
		this.destination = destination;
		this.destinationName = destinationName;
		
		// start repeating task for generating particles at player location
		if (plugin.getConfig().getBoolean("particle-effects")) {

			// start particle task, with 2 tick delay so it doesn't self cancel on first run
			particleTask = new ParticleTask(player).runTaskTimer(plugin, 2L, 10);
		}
	}

	@Override
	public final void run() {

		// cancel particles task
		particleTask.cancel();
		
		// if player is in warmup hashmap
		if (plugin.teleportManager.isWarmingUp(player)) {

			// remove player from warmup hashmap
			plugin.teleportManager.removeWarmup(player);
		
			// if remove-from-inventory is configured on-success, take one spawn star item from inventory now
			if (plugin.getConfig().getString("remove-from-inventory").equalsIgnoreCase("on-success")) {
				
				// try to remove one SpawnStar item from player inventory
				boolean notRemoved = true;
				for (ItemStack itemStack : player.getInventory()) {
					if (playerItem.isSimilar(itemStack)) {
						ItemStack removeItem = itemStack.clone();
						removeItem.setAmount(1);
						player.getInventory().removeItem(removeItem);
						notRemoved = false;
						break;
					}
				}
				
				// if one SpawnStar item could not be removed from inventory, send message, set cooldown and return
				if (notRemoved) {
					plugin.messageManager.sendPlayerMessage(player, MessageId.TELEPORT_CANCELLED_NO_ITEM);
					plugin.messageManager.sendPlayerSound(player, SoundId.TELEPORT_CANCELLED_NO_ITEM);
					plugin.teleportManager.startCooldown(player);
					return;
				}
			}

			// play pre-teleport sound if sound effects are enabled
			plugin.messageManager.sendPlayerSound(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);

			// teleport player to destination
			player.teleport(destination);

			// send player respawn message
			plugin.messageManager.sendPlayerMessage(player, MessageId.TELEPORT_SUCCESS, destinationName);

			// play post-teleport sound if sound effects are enabled
			plugin.messageManager.sendPlayerSound(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning")) {
				player.getWorld().strikeLightningEffect(destination);
			}
			
			// set player cooldown
			plugin.teleportManager.startCooldown(player);
		}
	}
	
}
