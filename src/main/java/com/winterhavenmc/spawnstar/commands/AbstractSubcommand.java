package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractSubcommand implements Subcommand {

	protected String name;
	protected List<String> aliases = new ArrayList<>();
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
	public final List<String> getAliases() {
		return aliases;
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
