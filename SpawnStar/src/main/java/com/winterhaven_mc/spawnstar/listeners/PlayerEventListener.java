package com.winterhaven_mc.spawnstar.listeners;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.sounds.SoundId;
import com.winterhaven_mc.spawnstar.messages.MessageId;

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

import static com.winterhaven_mc.spawnstar.SimpleAPI.isSpawnStar;


/**
 * Implements player event listener for SpawnStar events
 *
 * @author Tim Savage
 * @version 1.0
 */
public final class PlayerEventListener implements Listener {

	// reference to main class
	private final PluginMain plugin;


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
	 * @param event the event handled by this method
	 */
	@EventHandler
	final void onPlayerUse(final PlayerInteractEvent event) {

		// get player
		final Player player = event.getPlayer();

		// if cancel-on-interaction is configured true, check if player is in warmup hashmap
		if (plugin.getConfig().getBoolean("cancel-on-interaction")) {

			// if player is in warmup hashmap, check if they are interacting with a block (not air)
			if (plugin.teleportManager.isWarmingUp(player)) {

				// if player is interacting with a block, cancel teleport, output message and return
				if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					plugin.teleportManager.cancelTeleport(player);
					plugin.messageManager.sendMessage(player, MessageId.TELEPORT_CANCELLED_INTERACTION);

					// play sound effects if enabled
					plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
					return;
				}
			}
		}

		// if item used is not a SpawnStar, do nothing and return
		if (!isSpawnStar(player.getInventory().getItemInMainHand())) {
			return;
		}

		// if event action is not a right click, or not a left click if configured, do nothing and return
		if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR)
				|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				|| (plugin.getConfig().getBoolean("left-click")
				&& !(event.getAction().equals(Action.LEFT_CLICK_AIR)
				|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)))) {
			return;
		}

		// cancel event
		event.setCancelled(true);
		//noinspection deprecation
		player.updateInventory();

		// if players current world is not enabled in config, do nothing and return
		if (!plugin.worldManager.isEnabled(player.getWorld())) {
			return;
		}

		// if player does not have spawnstar.use permission, send message and return
		if (!player.hasPermission("spawnstar.use")) {
			plugin.messageManager.sendMessage(player, MessageId.TELEPORT_FAIL_PERMISSION);
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_DENIED_PERMISSION);
			return;
		}

		// if shift-click is configured true and player is not sneaking, send message and return
		if (plugin.getConfig().getBoolean("shift-click") && !event.getPlayer().isSneaking()) {
			plugin.messageManager.sendMessage(player, MessageId.TELEPORT_FAIL_SHIFT_CLICK);
			return;
		}

		// initiate teleport
		plugin.teleportManager.initiateTeleport(player);
	}


	/**
	 * Cancel pending teleport on player death
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	final void onPlayerDeath(final PlayerDeathEvent event) {

		// get event player
		Player player = event.getEntity();

		// cancel any pending teleport for player
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Perform cleanup tasks when player logs off server
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	final void onPlayerQuit(final PlayerQuitEvent event) {

		// get event player
		Player player = event.getPlayer();

		// cancel any pending teleport for player
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Prepare Item Craft event handler<br>
	 * Prevents SpawnStar items from being used in crafting recipes if configured
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	final void onCraftPrepare(final PrepareItemCraftEvent event) {

		// if allow-in-recipes is true in configuration, do nothing and return
		if (plugin.getConfig().getBoolean("allow-in-recipes")) {
			return;
		}

		// if crafting inventory contains SpawnStar item, set result item to null
		for (ItemStack itemStack : event.getInventory()) {
			if (isSpawnStar(itemStack)) {
				event.getInventory().setResult(null);
			}
		}
	}


	/**
	 * Cancels pending teleport if player takes damage during warmup
	 *
	 * @param event the event handled by this method
	 */
	@EventHandler
	final void onEntityDamage(final EntityDamageEvent event) {

		// if event is already cancelled, do nothing and return
		if (event.isCancelled()) {
			return;
		}

		// if cancel-on-damage configuration is true, check if damaged entity is player
		if (plugin.getConfig().getBoolean("cancel-on-damage")) {

			Entity entity = event.getEntity();

			// if damaged entity is player, check for pending teleport
			if (entity instanceof Player) {

				Player player = (Player) entity;
				// if player is in warmup hashmap, cancel teleport and send player message
				if (plugin.teleportManager.isWarmingUp(player)) {
					plugin.teleportManager.cancelTeleport(player);
					plugin.messageManager.sendMessage(player, MessageId.TELEPORT_CANCELLED_DAMAGE);
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
	final void onPlayerMovement(final PlayerMoveEvent event) {

		// if cancel-on-movement configuration is false, do nothing and return
		if (!plugin.getConfig().getBoolean("cancel-on-movement")) {
			return;
		}

		// get event player
		Player player = event.getPlayer();

		// if player is in warmup hashmap, cancel teleport and send player message
		if (plugin.teleportManager.isWarmingUp(player)) {

			// check for player movement other than head turning
			if (event.getFrom().distance(event.getTo()) > 0) {
				plugin.teleportManager.cancelTeleport(player);
				plugin.messageManager.sendMessage(player, MessageId.TELEPORT_CANCELLED_MOVEMENT);
				plugin.soundConfig.playSound(player, SoundId.TELEPORT_CANCELLED);
			}
		}
	}

}
