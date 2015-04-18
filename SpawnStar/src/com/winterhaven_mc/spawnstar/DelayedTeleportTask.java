package com.winterhaven_mc.spawnstar;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DelayedTeleportTask extends BukkitRunnable {

	SpawnStarMain plugin;
	Player player;
	Location spawnLocation;
	BukkitTask particleTask;

	/**
	 * Class constructor method
	 */
	public DelayedTeleportTask(final Player player, final Location spawnLocation) {
		
		this.plugin = SpawnStarMain.instance;
		this.player = player;
		
		this.spawnLocation = spawnLocation;
		
		// start repeating task for generating particles at player location
		if (plugin.getConfig().getBoolean("particle-effects",true)) {

			// start particle task, with 2 tick delay so it doesn't self cancel on first run
			particleTask = new ParticleTask(player).runTaskTimer(plugin, 2L, 10);
		
		}
	}

	@Override
	public void run() {

		// cancel particles task
		particleTask.cancel();
		
		// if player is in warmup hashmap
		if (plugin.warmupManager.isWarmingUp(player)) {

			// remove player from warmup hashmap
			plugin.warmupManager.removePlayer(player);
		
			// teleport player to spawn location
			player.teleport(spawnLocation);

			// send player respawn message
			plugin.messageManager.sendPlayerMessage(player, "respawn");

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning", true)) {
				player.getWorld().strikeLightningEffect(spawnLocation);
			}
			
			// if remove-from-inventory is configured on-success, take one spawn star item from inventory now
			if (plugin.getConfig().getString("remove-from-inventory","on-use").equalsIgnoreCase("on-success")) {
				ItemStack playerItem = player.getItemInHand();
				ItemStack removeItem = playerItem;
				removeItem.setAmount(playerItem.getAmount() - 1);
				player.setItemInHand(removeItem);
			}
			

			// set player cooldown
			plugin.cooldownManager.setPlayerCooldown(player);

			// try to prevent player spawning inside block and suffocating
			preventSuffocation(player, spawnLocation);
		}
	}

	
	private void preventSuffocation(final Player player, final Location spawnLoc) {
		
		final int spawnAir = player.getRemainingAir();
		
		new BukkitRunnable(){

			public void run() {
				if (player.getRemainingAir() < spawnAir) {
					player.teleport(spawnLoc.add(0,1,0));
					player.setRemainingAir(spawnAir);
				}
			}
		}.runTaskLater(plugin, 20);		
	}

}
