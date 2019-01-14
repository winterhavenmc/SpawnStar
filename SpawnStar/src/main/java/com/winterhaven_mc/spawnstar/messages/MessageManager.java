package com.winterhaven_mc.spawnstar.messages;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Implements message manager for SpawnStar
 *
 * @author Tim Savage
 * @version 1.0
 */
public final class MessageManager extends AbstractMessageManager {

	// reference to plugin main class
	private PluginMain plugin;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);

		// set reference to main class
		this.plugin = plugin;
	}


	@Override
	protected Map<String, String> getDefaultReplacements(final CommandSender recipient) {

		// check for null parameters
		Objects.requireNonNull(recipient);

		Map<String, String> replacements = new HashMap<>();

		// strip color codes
		replacements.put("%PLAYER_NAME%", ChatColor.stripColor(recipient.getName()));
		replacements.put("%WORLD_NAME%", ChatColor.stripColor(getWorldName(recipient)));
		replacements.put("%ITEM_NAME%", ChatColor.stripColor(getItemName()));
		replacements.put("%QUANTITY%", "1");
		replacements.put("%MATERIAL%", "unknown");
		replacements.put("%DESTINATION_NAME%", ChatColor.stripColor(getSpawnDisplayName()));
		replacements.put("%TARGET_PLAYER%", "target player");
		replacements.put("%WARMUP_TIME%",
				getTimeString(TimeUnit.SECONDS.toMillis(plugin.getConfig().getInt("teleport-warmup"))));

		// leave color codes intact
		replacements.put("%player_name%", recipient.getName());
		replacements.put("%world_name%", getWorldName(recipient));
		replacements.put("%item_name%", getItemName());
		replacements.put("%destination_name%", getSpawnDisplayName());
		replacements.put("%target_player%", "target player");

		// if recipient is player, get remaining cooldown time from teleport manager
		if (recipient instanceof Player) {
			replacements.put("%COOLDOWN_TIME%",
					getTimeString(plugin.teleportManager.getCooldownTimeRemaining((Player) recipient)));
		}
		else {
			replacements.put("%COOLDOWN_TIME%", getTimeString(0L));
		}

		return replacements;
	}


	/**
	 * Send message to player
	 *
	 * @param recipient player receiving message
	 * @param messageId message identifier in messages file
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient player receiving message
	 * @param messageId message identifier in messages file
	 * @param quantity  number of items
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final int quantity) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// set quantity in replacement map
		replacements.put("%quantity%", String.valueOf(quantity));
		replacements.put("%QUANTITY%", String.valueOf(quantity));

		// if quantity is greater than one, use substitute plural item name
		if (quantity > 1) {
			replacements.put("%item_name%", getItemNamePlural());
			replacements.put("%ITEM_NAME%", ChatColor.stripColor(getItemNamePlural()));
		}

		// send message
		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient       player receiving message
	 * @param messageId       message identifier in messages file
	 * @param destination     name of teleport destination
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final Location destination) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(destination);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// get world name for destination
		replacements.put("%world_name%", getWorldName(destination));
		replacements.put("%WORLD_NAME%", ChatColor.stripColor(getWorldName(destination)));

		// send message
		//noinspection unchecked
		this.sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient    player recieving message
	 * @param messageId    message identifier in messages file
	 * @param quantity     number of items
	 * @param targetPlayer player targeted
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final int quantity,
								  final CommandSender targetPlayer) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(targetPlayer);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// set strings replacement map
		replacements.put("%quantity%", String.valueOf(quantity));
		replacements.put("%QUANTITY%", String.valueOf(quantity));
		replacements.put("%target_player%", targetPlayer.getName());
		replacements.put("%TARGET_PLAYER%", ChatColor.stripColor(targetPlayer.getName()));

		// send message
		//noinspection unchecked
		this.sendMessage(recipient, messageId, replacements);
	}

}
