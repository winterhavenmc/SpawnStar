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

package com.winterhavenmc.spawnstar.core.commands;

import com.winterhavenmc.library.messagebuilder.ItemForge;
import com.winterhavenmc.spawnstar.core.context.CommandCtx;
import com.winterhavenmc.spawnstar.core.util.Macro;
import com.winterhavenmc.spawnstar.core.util.MessageId;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;


final class DestroySubcommand extends AbstractSubcommand
{
	private final CommandCtx ctx;


	DestroySubcommand(final CommandCtx ctx)
	{
		this.ctx = Objects.requireNonNull(ctx);
		this.name = "destroy";
		this.usage = "/spawnstar destroy";
		this.permissionNode = "spawnstar.destroy";
		this.description = MessageId.COMMAND_HELP_DESTROY;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// sender must be in game player
		if (!(sender instanceof Player player))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_DESTROY_CONSOLE).send();
			return true;
		}

		// if command sender does not have permission to destroy SpawnStars, output error message and return true
		if (!sender.hasPermission(permissionNode))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_DESTROY_PERMISSION).send();
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			return true;
		}

		// get item in player's hand
		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player held item is a spawnstar stack
		if (!ItemForge.isCustomItem(playerItem))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_DESTROY_NO_MATCH).send();
			return true;
		}

		// get copy of itemStack (to display in message)
		ItemStack emptyItemStack = playerItem.clone();

		// set quantity of items to zero
		emptyItemStack.setAmount(0);

		// set player's item in hand to the zero quantity itemstack
		//noinspection deprecation
		player.getInventory().setItemInHand(emptyItemStack);

		// send success message
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_SUCCESS_DESTROY)
				.setMacro(Macro.ITEM, playerItem)
				.send();

		// return true to prevent display of bukkit command usage string
		return true;
	}

}
