package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;


public interface Subcommand {

	boolean onCommand(CommandSender sender, List<String> argsList);

	List<String> onTabComplete(final CommandSender sender, final Command command,
	                           final String alias, final String[] args);

	String getName();

	List<String> getAliases();

	String getUsage();

	void displayUsage(CommandSender sender);

	MessageId getDescription();

	String getPermission();

	int getMinArgs();

	int getMaxArgs();

	void register(SubcommandMap subcommandMap);

}
