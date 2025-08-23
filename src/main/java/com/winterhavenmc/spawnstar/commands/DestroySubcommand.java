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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;


final class DestroySubcommand extends AbstractSubcommand
{
	private final PluginMain plugin;


	DestroySubcommand(final PluginMain plugin)
	{
		this.plugin = Objects.requireNonNull(plugin);
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
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_CONSOLE).send();
			return true;
		}

		// if command sender does not have permission to destroy SpawnStars, output error message and return true
		if (!sender.hasPermission(permissionNode))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// get item in player's hand
		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player held item is a spawnstar stack
		if (!plugin.spawnStarUtility.isItem(playerItem))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_DESTROY_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get quantity of items in stack (to display in message)
		int quantity = playerItem.getAmount();

		// set quantity of items to zero
		playerItem.setAmount(0);

		// set player's item in hand to the zero quantity itemstack
		//noinspection deprecation
		player.getInventory().setItemInHand(playerItem);

		// send success message
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_DESTROY)
				.setMacro(Macro.ITEM_QUANTITY, quantity)
				.send();

		// play success sound
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_DESTROY);

		// return true to prevent display of bukkit command usage string
		return true;
	}

}
