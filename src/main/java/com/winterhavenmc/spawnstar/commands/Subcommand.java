package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;


interface Subcommand {

	boolean onCommand(final CommandSender sender, final List<String> argsList);

	List<String> onTabComplete(final CommandSender sender, final Command command,
	                           final String alias, final String[] args);

	String getName();

	String getUsage();

	void displayUsage(final CommandSender sender);

	MessageId getDescription();

	String getPermission();

	int getMinArgs();

	int getMaxArgs();

}
