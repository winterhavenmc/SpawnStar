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

import com.winterhavenmc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


abstract class AbstractSubcommand implements Subcommand {

	protected String name;
	protected Collection<String> aliases = new ArrayList<>();
	protected String usage;
	protected MessageId description;
	protected String permission;
	protected int minArgs;
	protected int maxArgs;


	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUsage() {
		return usage;
	}

	@Override
	public final void displayUsage(final CommandSender sender) {
		sender.sendMessage(usage);
	}

	@Override
	public final MessageId getDescription() {
		return description;
	}

	@Override
	public final String getPermission() {
		return permission;
	}

	@Override
	public final int getMinArgs() {
		return minArgs;
	}

	@Override
	public final int getMaxArgs() {
		return maxArgs;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
	                                  final String alias, final String[] args) {

		return Collections.emptyList();
	}

}
