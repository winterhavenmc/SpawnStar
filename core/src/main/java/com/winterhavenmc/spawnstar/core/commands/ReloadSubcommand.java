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

import com.winterhavenmc.spawnstar.core.context.CommandCtx;
import com.winterhavenmc.spawnstar.core.util.MessageId;
import com.winterhavenmc.spawnstar.core.util.SoundId;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;


final class ReloadSubcommand extends AbstractSubcommand
{
	private final CommandCtx ctx;


	ReloadSubcommand(final CommandCtx ctx)
	{
		this.ctx = Objects.requireNonNull(ctx);
		this.name = "reload";
		this.usage = "/spawnstar reload";
		this.description = MessageId.COMMAND_HELP_RELOAD;
		this.permissionNode = "spawnstar.reload";
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if sender does not have permission to reload config, send error message and return
		if (!sender.hasPermission(permissionNode))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_RELOAD_PERMISSION).send();
			ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reinstall main configuration file if necessary
		ctx.plugin().saveDefaultConfig();

		// reload main configuration
		ctx.plugin().reloadConfig();

		// update enabledWorlds list
		ctx.worldManager().reload();

		// reload messages
		ctx.messageBuilder().reload();

		// reload sounds
		ctx.soundConfiguration().reload();

		// send reloaded message
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_SUCCESS_RELOAD).send();
		ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_SUCCESS_RELOAD);
		return true;
	}

}
