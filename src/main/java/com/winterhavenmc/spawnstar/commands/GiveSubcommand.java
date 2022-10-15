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

package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.messages.Macro;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;


final class GiveSubcommand extends AbstractSubcommand {

	private final PluginMain plugin;


	GiveSubcommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "give";
		this.usage = "/spawnstar give <player> [quantity]";
		this.description = MessageId.COMMAND_HELP_GIVE;
		this.permissionNode = "spawnstar.give";
		this.minArgs = 1;
		this.maxArgs = 2;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
	                                        final String alias, final String[] args) {

		// initialize return list
		List<String> returnList = new ArrayList<>();

		// return list of matching players
		if (args.length == 2) {
			return plugin.getServer().matchPlayer(args[1]).stream()
					.map(Player::getName).collect(Collectors.toList());
		}

		// return some useful quantities
		else if (args.length == 3) {
			returnList = Arrays.asList("1", "2", "3", "5", "10");
		}

		return returnList;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to give SpawnStars, output error message and return
		if (!sender.hasPermission("spawnstar.give")) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check min arguments
		if (args.size() < getMinArgs()) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// get passed player name
		String targetPlayerName = args.get(0);

		// try to match target player name to currently online player
		Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

		// if no match, send player not found message and return
		if (targetPlayer == null) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_PLAYER_NOT_FOUND).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set default quantity
		int quantity = 1;

		// if second argument, try to parse as integer
		if (args.size() == 2) {
			try {
				quantity = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_QUANTITY_INVALID).send();
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
		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(plugin.spawnStarUtility.create(quantity));

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet()) {
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_GIVE_INVENTORY_FULL)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// subtract noFitCount from quantity
		quantity -= noFitCount;

		// send message when giving to self
		if (sender.getName().equals(targetPlayer.getName())) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_GIVE_SELF)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.send();
		}
		else {
			// send message and play sound to giver
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_GIVE_SENDER)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_GIVE_SENDER);

			// send message to target player
			plugin.messageBuilder.compose(targetPlayer, MessageId.COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(Macro.ITEM_QUANTITY, quantity)
					.setMacro(Macro.TARGET_PLAYER, sender)
					.send();
		}

		// play sound to target player
		plugin.soundConfig.playSound(targetPlayer, SoundId.COMMAND_SUCCESS_GIVE_TARGET);
		return true;
	}

}
