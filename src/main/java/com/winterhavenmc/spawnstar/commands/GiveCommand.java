package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.messages.Macro;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.OfflinePlayer;

import java.util.*;


final class GiveCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	GiveCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "give";
		this.usage = "/spawnstar give <player> [quantity]";
		this.description = MessageId.COMMAND_HELP_GIVE;
		this.permission = "spawnstar.give";
		this.minArgs = 1;
		this.maxArgs = 2;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
	                                        final String alias, final String[] args) {

		// initialize return list
		final List<String> returnList = new ArrayList<>();

		if (args.length == 2) {
			List<Player> matchedPlayers = plugin.getServer().matchPlayer(args[1]);
			for (Player player : matchedPlayers) {
				returnList.add(player.getName());
			}
		}

		// return some useful quantities
		else if (args.length == 3) {
			returnList.add("1");
			returnList.add("2");
			returnList.add("3");
			returnList.add("5");
			returnList.add("10");
		}

		return returnList;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to give SpawnStars, output error message and return
		if (!sender.hasPermission("spawnstar.give")) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_GIVE_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check min arguments
		if (args.size() < getMinArgs()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// get passed player name
		String targetPlayerName = args.get(0);

		// try to match target player name to currently online player
		Player targetPlayer = matchPlayer(sender, targetPlayerName);

		// if no match, do nothing and return (message was output by matchPlayer method)
		if (targetPlayer == null) {
			return true;
		}

		// set default quantity
		int quantity = 1;

		// if second argument, try to parse as integer
		if (args.size() == 2) {
			try {
				quantity = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_GIVE_QUANTITY_INVALID).send();
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = plugin.getConfig().getInt("max-give-amount");
		if (maxQuantity < 0) {
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		// add specified quantity of spawnstar(s) to player inventory
		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(plugin.spawnStarFactory.create(quantity));

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet()) {
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_GIVE_INVENTORY_FULL)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// subtract noFitCount from quantity
		quantity -= noFitCount;

		// send both players message if not giving item to self
		if (!sender.getName().equals(targetPlayer.getName())) {

			// send message and play sound to giver
			plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_GIVE_SENDER)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_GIVE_SENDER);

			// send message to target player
			plugin.messageBuilder.build(targetPlayer, MessageId.COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, sender)
					.send();
		} else {
			// send message when giving to self
			plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_GIVE_SELF)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.send();
		}

		// play sound to target player
		plugin.soundConfig.playSound(targetPlayer, SoundId.COMMAND_SUCCESS_GIVE_TARGET);
		return true;
	}


	/**
	 * Match online player; sends appropriate message for offline or unknown players
	 *
	 * @param sender           the command sender
	 * @param targetPlayerName the player name to match
	 * @return Player - a matching player object, or null if no match
	 */
	private Player matchPlayer(final CommandSender sender, final String targetPlayerName) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(targetPlayerName);

		Player targetPlayer;

		// check exact match first
		targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

		// if no match, try substring match
		if (targetPlayer == null) {
			List<Player> playerList = plugin.getServer().matchPlayer(targetPlayerName);

			// if only one matching player, use it, otherwise send error message (no match or more than 1 match)
			if (playerList.size() == 1) {
				targetPlayer = playerList.get(0);
			}
		}

		// if match found, return target player object
		if (targetPlayer != null) {
			return targetPlayer;
		}

		// check if name matches known offline player
		HashSet<OfflinePlayer> matchedPlayers = new HashSet<>();
		for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
			if (targetPlayerName.equalsIgnoreCase(offlinePlayer.getName())) {
				matchedPlayers.add(offlinePlayer);
			}
		}
		if (matchedPlayers.isEmpty()) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_GIVE_PLAYER_NOT_FOUND).send();
		} else {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_GIVE_PLAYER_NOT_ONLINE).send();
		}
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return null;
	}

}
