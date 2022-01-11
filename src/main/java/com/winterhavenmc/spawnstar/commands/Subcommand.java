package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;


public interface Subcommand {

	boolean onCommand(final CommandSender sender, final List<String> argsList);

	List<String> onTabComplete(final CommandSender sender, final Command command,
	                           final String alias, final String[] args);

	String getName();

	List<String> getAliases();

	String getUsage();

	void displayUsage(final CommandSender sender);

	MessageId getDescription();

	String getPermission();

	int getMinArgs();

	int getMaxArgs();

	void register(final SubcommandMap subcommandMap);

}
