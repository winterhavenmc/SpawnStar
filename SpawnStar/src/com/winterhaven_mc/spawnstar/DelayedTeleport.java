package com.winterhaven_mc.spawnstar;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DelayedTeleport extends BukkitRunnable {

	SpawnStarMain plugin;
	Player player;
	Location spawnLocation;
	BukkitTask particleTask;

	/**
	 * Class constructor method
	 */
	public DelayedTeleport(final SpawnStarMain plugin, final Player player, final Location spawnLocation) {
		
		this.plugin = plugin;
		this.player = player;
		
		this.spawnLocation = spawnLocation;
		
		plugin.cooldownManager.putPlayerWarmup(player);
		
		this.particleTask = new BukkitRunnable() {
			
			public void run() {
				
				// do particle effects if configured
				if (plugin.getConfig().getBoolean("particle-effects",true)) {
					player.getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.ENDER_SIGNAL, 0, 10);
				}
			}
		}.runTaskTimer(plugin, 0L, 10);

		
	}

	@Override
	public void run() {

		// cancel particles task
		particleTask.cancel();
		
		if (plugin.cooldownManager.isWarmingUp(player)) {

			// remove player from warmup hashset
			plugin.cooldownManager.removePlayerWarmup(player);
		
			// teleport player to spawn location
			player.teleport(spawnLocation);

			// send player respawn message
			plugin.messageManager.sendPlayerMessage(player, "respawn");

			// if lightning is enabled in config, strike lightning at spawn location
			if (plugin.getConfig().getBoolean("lightning", true)) {
				player.getWorld().strikeLightningEffect(spawnLocation);
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
