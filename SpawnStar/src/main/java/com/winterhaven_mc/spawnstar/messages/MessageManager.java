package com.winterhaven_mc.spawnstar.messages;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


/**
 * Implements message manager for SpawnStar
 *
 * @author      Tim Savage
 * @version		1.0
 *
 */
public final class MessageManager extends AbstractMessageManager {

	private PluginMain plugin;

	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);

		this.plugin = plugin;
	}


	@Override
	protected Map<String,String> getDefaultReplacements(CommandSender recipient) {

		Map<String,String> replacements = new HashMap<>();

		// strip color codes
		replacements.put("%PLAYER_NAME%",ChatColor.stripColor(recipient.getName()));
		replacements.put("%WORLD_NAME%",ChatColor.stripColor(getWorldName(recipient)));
		replacements.put("%ITEM_NAME%", ChatColor.stripColor(getItemName()));
		replacements.put("%QUANTITY%","1");
		replacements.put("%MATERIAL%","unknown");
		replacements.put("%DESTINATION_NAME%",ChatColor.stripColor(getSpawnDisplayName()));
		replacements.put("%TARGET_PLAYER%","target player");
		replacements.put("%WARMUP_TIME%",getTimeString(plugin.getConfig().getInt("teleport-warmup")));

		// leave color codes intact
		replacements.put("%player_name%",recipient.getName());
		replacements.put("%world_name%",getWorldName(recipient));
		replacements.put("%item_name%",getItemName());
		replacements.put("%destination_name%",getSpawnDisplayName());
		replacements.put("%target_player%","target player");

		// if recipient is player, get remaining cooldown time from teleport manager
		if (recipient instanceof Player) {
			replacements.put("%COOLDOWN_TIME%",
					getTimeString(plugin.teleportManager.getCooldownTimeRemaining((Player)recipient)));
		}
		else {
			replacements.put("%COOLDOWN_TIME",getTimeString(0L));
		}

		return replacements;
	}


	/**
	 *  Send message to player
	 *
	 * @param recipient			player receiving message
	 * @param messageId			message identifier in messages file
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId) {

		Map<String,String> replacements = getDefaultReplacements(recipient);

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient			player receiving message
	 * @param messageId			message identifier in messages file
	 * @param quantity			number of items
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final Integer quantity) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set quantity in replacement map
		replacements.put("%QUANTITY$",quantity.toString());

		// if quantity is greater than one, use substitute plural item name
		if (quantity > 1) {
			replacements.put("%ITEM_NAME%",getItemNamePlural());
		}

		// send message
		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient			player receiving message
	 * @param messageId			message identifier in messages file
	 * @param destinationName	name of teleport destination
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final String destinationName) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set destination name in replacement map
		replacements.put("%DESTINATION_NAME%",ChatColor.stripColor(destinationName));
		replacements.put("%destination_name%",destinationName);

		// send message
		//noinspection unchecked
		this.sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient			player recieving message
	 * @param messageId			message identifier in messages file
	 * @param quantity			number of items
	 * @param targetPlayer		player targeted
	 */
	public final void sendMessage(final CommandSender recipient,
								  final MessageId messageId,
								  final Integer quantity,
								  final CommandSender targetPlayer) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set strings replacement map
		replacements.put("%QUANTITY%",quantity.toString());
		replacements.put("%TARGET_PLAYER%",ChatColor.stripColor(targetPlayer.getName()));
		replacements.put("%target_player%",targetPlayer.getName());

		// send message
		//noinspection unchecked
		this.sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Get configured plural item name from language file
	 * @return the formatted plural display name of the SpawnStar item
	 */
	public final String getItemNamePlural() {
		return ChatColor.translateAlternateColorCodes('&',
				messages.getString("item_info.ITEM_NAME_PLURAL"));
	}


	/**
	 * Get spawn display name from language file
	 * @return the formatted display name for the world spawn
	 */
	public final String getSpawnDisplayName() {
		return ChatColor.translateAlternateColorCodes('&',
				messages.getString("item_info.SPAWN_DISPLAY_NAME"));
	}

}
