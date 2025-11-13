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

import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.spawnstar.core.ports.commands.CommandDispatcher;
import com.winterhavenmc.spawnstar.core.util.MessageId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;


/**
 * Implements command executor and tab completer for SpawnStar commands.
 */
public final class BukkitCommandDispatcher implements CommandDispatcher
{
	private final MessageBuilder messageBuilder;
	private final SubcommandRegistry subcommandRegistry = new SubcommandRegistry();


	/**
	 * Class constructor method for BukkitCommandDispatcher
	 */
	public BukkitCommandDispatcher(final JavaPlugin plugin, final MessageBuilder messageBuilder)
	{
		this.messageBuilder = messageBuilder;

		// register this class as command executor
		Objects.requireNonNull(plugin.getCommand("spawnstar")).setExecutor(this);

		// create context container for use in subcommand constructors
		CommandCtx ctx = new CommandCtx(plugin, messageBuilder);

		// register subcommands
		for (SubcommandType subcommandType : SubcommandType.values())
		{
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
			messageBuilder.compose(sender, MessageId.COMMAND_FAIL_INVALID_COMMAND).send();
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
