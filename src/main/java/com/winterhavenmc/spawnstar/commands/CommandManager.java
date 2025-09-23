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

import com.winterhavenmc.spawnstar.PluginController;
import com.winterhavenmc.spawnstar.SpawnStarPluginController;
import com.winterhavenmc.spawnstar.util.MessageId;
import com.winterhavenmc.spawnstar.util.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;


/**
 * Implements command executor and tab completer for SpawnStar commands.
 */
public final class CommandManager implements TabExecutor
{
	private final SubcommandRegistry subcommandRegistry = new SubcommandRegistry();
	private final PluginController.CommandContextContainer ctx;


	/**
	 * Class constructor method for CommandManager
	 */
	public CommandManager(final SpawnStarPluginController.CommandContextContainer ctx)
	{
		this.ctx = ctx;

		// register this class as command executor
		Objects.requireNonNull(ctx.plugin().getCommand("spawnstar")).setExecutor(this);

		// register subcommands
		for (SubcommandType subcommandType : SubcommandType.values()) {
			subcommandRegistry.register(subcommandType.create(ctx));
		}

		// register help command
		subcommandRegistry.register(new HelpSubcommand(ctx, subcommandRegistry));
	}


	/**
	 * Tab completer for SpawnStar
	 */
	@Override
	public List<String> onTabComplete(final @Nonnull CommandSender sender,
	                                  final @Nonnull Command command,
	                                  final @Nonnull String alias,
	                                  final String[] args) {

		// if more than one argument, use tab completer of subcommand
		if (args.length > 1)
		{
			// get subcommand from map
			Optional<Subcommand> optionalSubcommand = subcommandRegistry.getSubcommand(args[0]);

			// if no subcommand returned from map, return empty list
			if (optionalSubcommand.isEmpty())
			{
				return Collections.emptyList();
			}

			// unwrap optional subcommand
			Subcommand subcommand = optionalSubcommand.get();

			// return subcommand tab completer output
			return subcommand.onTabComplete(sender, command, alias, args);
		}

		// return list of subcommands for which sender has permission
		return matchingNames(sender, args[0]);
	}


	/**
	 * command executor method for SpawnStar
	 */
	@Override
	public boolean onCommand(final @Nonnull CommandSender sender,
	                         final @Nonnull Command cmd,
	                         final @Nonnull String label,
	                         final String[] args)
	{
		// convert args array to list
		List<String> argsList = new ArrayList<>(Arrays.asList(args));

		String subcommandName;

		// get subcommand, remove from front of list
		if (!argsList.isEmpty())
		{
			subcommandName = argsList.removeFirst();
		}
		else
		{
			subcommandName = "help";
		}

		// get subcommand from map by name
		Optional<Subcommand> subcommand = subcommandRegistry.getSubcommand(subcommandName);

		// if subcommand is null, get help command from map
		if (subcommand.isEmpty())
		{
			subcommand = subcommandRegistry.getSubcommand("help");
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND).send();
			ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_INVALID);
		}

		// execute subcommand
		subcommand.ifPresent( sc -> sc.onCommand(sender, argsList) );

		return true;
	}


	/**
	 * Get matching list of subcommand names for which sender has permission
	 *
	 * @param sender the command sender
	 * @param matchString the string prefix to match against command names
	 * @return List of String - command names that match prefix and sender permission
	 */
	private List<String> matchingNames(final CommandSender sender, final String matchString)
	{
		return subcommandRegistry.getSubcommandNames().stream()
				.filter(hasPermission(sender))
				.filter(matchesPrefix(matchString))
				.toList();
	}


	private Predicate<String> hasPermission(final CommandSender sender)
	{
		return subcommandName -> subcommandRegistry.getSubcommand(subcommandName)
				.map(subcommand -> sender.hasPermission(subcommand.getPermissionNode()))
				.orElse(false);
	}


	private Predicate<String> matchesPrefix(final String prefix)
	{
		return subcommandName -> subcommandName.startsWith(prefix.toLowerCase());
	}
}
