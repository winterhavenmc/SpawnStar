package com.winterhaven_mc.spawnstar.messages;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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


	/**
	 * Class constructor
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);
	}


	@Override
	protected Map<String,String> getDefaultReplacements(CommandSender recipient) {

		Map<String,String> replacements = new HashMap<>();
		replacements.put("%PLAYER_NAME%",recipient.getName());
		replacements.put("%WORLD_NAME%",ChatColor.stripColor(getWorldName(recipient)));
		replacements.put("%ITEM_NAME%", ChatColor.stripColor(getItemName()));
		replacements.put("%QUANTITY%","1");
		replacements.put("%MATERIAL%","unknown");
		replacements.put("%DESTINATION_NAME%","unknown");
		replacements.put("%TARGET_PLAYER%","target player");

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
		replacements.put("%DESTINATION_NAME%",destinationName);

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
		replacements.put("%TARGET_PLAYER%",targetPlayer.getName());

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


	/**
	 * Format the time string with hours, minutes, seconds
	 * @param duration the time duration in milliseconds to format
	 * @return formatted time string
	 */
	public String getTimeString(long duration) {

		StringBuilder timeString = new StringBuilder();

		int hours =   (int)duration / 3600;
		int minutes = (int)(duration % 3600) / 60;
		int seconds = (int)duration % 60;

		String hour_string = this.messages.getString("time_strings.HOUR");
		String hour_plural_string = this.messages.getString("time_strings.HOUR_PLURAL");
		String minute_string = this.messages.getString("time_strings.MINUTE");
		String minute_plural_string = this.messages.getString("time_strings.MINUTE_PLURAL");
		String second_string = this.messages.getString("time_strings.SECOND");
		String second_plural_string = this.messages.getString("time_strings.SECOND_PLURAL");

		if (hours > 1) {
			timeString.append(hours);
			timeString.append(' ');
			timeString.append(hour_plural_string);
			timeString.append(' ');
		}
		else if (hours == 1) {
			timeString.append(hours);
			timeString.append(' ');
			timeString.append(hour_string);
			timeString.append(' ');
		}

		if (minutes > 1) {
			timeString.append(minutes);
			timeString.append(' ');
			timeString.append(minute_plural_string);
			timeString.append(' ');
		}
		else if (minutes == 1) {
			timeString.append(minutes);
			timeString.append(' ');
			timeString.append(minute_string);
			timeString.append(' ');
		}

		if (seconds > 1) {
			timeString.append(seconds);
			timeString.append(' ');
			timeString.append(second_plural_string);
		}
		else if (seconds == 1) {
			timeString.append(seconds);
			timeString.append(' ');
			timeString.append(second_string);
		}

		return ChatColor.translateAlternateColorCodes('&',timeString.toString().trim());
	}

}
