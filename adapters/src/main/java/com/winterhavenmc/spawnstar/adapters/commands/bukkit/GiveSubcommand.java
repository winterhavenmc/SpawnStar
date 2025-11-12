/*
 * Copyright (c) 2022-2025 Tim Savage.
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

package com.winterhavenmc.spawnstar.adapters.commands.bukkit;

import com.winterhavenmc.spawnstar.core.util.Macro;
import com.winterhavenmc.spawnstar.core.util.MessageId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


final class GiveSubcommand extends AbstractSubcommand
{
	private final CommandCtx ctx;


	GiveSubcommand(final CommandCtx ctx)
	{
		this.ctx = Objects.requireNonNull(ctx);
		this.name = "give";
		this.usage = "/spawnstar give <player> [quantity]";
		this.description = MessageId.COMMAND_HELP_GIVE;
		this.permissionNode = "spawnstar.give";
		this.minArgs = 1;
		this.maxArgs = 2;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
	                                  final String alias, final String[] args)
	{
		return switch (args.length)
		{
			case 2 -> null; // return null for list of matching online players
			case 3 -> List.of("1", "2", "3", "5", "10"); // return some useful quantities
			default -> List.of(); // return empty list
		};
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if command sender does not have permission to give SpawnStars, output error message and return
		if (!sender.hasPermission("spawnstar.give"))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_GIVE_PERMISSION).send();
			return true;
		}

		// check min arguments
		if (args.size() < getMinArgs())
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			return true;
		}

		// get passed player name
		String targetPlayerName = args.getFirst();

		// try to match target player name to currently online player
		Player targetPlayer = ctx.plugin().getServer().getPlayer(targetPlayerName);

		// if no match, send player not found message and return
		if (targetPlayer == null)
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_GIVE_PLAYER_NOT_FOUND).send();
			return true;
		}

		// set default quantity
		int quantity = 1;

		// if second argument, try to parse as integer
		if (args.size() == 2)
		{
			try
			{
				quantity = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e)
			{
				ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_GIVE_QUANTITY_INVALID).send();
				return true;
			}
		}

		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = ctx.plugin().getConfig().getInt("max-give-amount");
		if (maxQuantity < 0)
		{
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		ItemStack item = ctx.spawnStarUtility().create(quantity);

		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(item);

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet())
		{
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity)
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_GIVE_INVENTORY_FULL)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.setMacro(Macro.ITEM, item)
					.send();
			return true;
		}

		// send message when giving to self
		if (sender.getName().equals(targetPlayer.getName()))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_SUCCESS_GIVE_SELF)
					.setMacro(Macro.ITEM, item)
					.send();
		}
		else
		{
			// send message and play sound to giver
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_SUCCESS_GIVE_SENDER)
					.setMacro(Macro.TARGET_PLAYER, targetPlayerName)
					.setMacro(Macro.ITEM, item)
					.send();

			// send message to target player
			ctx.messageBuilder().compose(targetPlayer, MessageId.COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(Macro.TARGET_PLAYER, sender)
					.setMacro(Macro.ITEM, item)
					.send();
		}

		return true;
	}

}
