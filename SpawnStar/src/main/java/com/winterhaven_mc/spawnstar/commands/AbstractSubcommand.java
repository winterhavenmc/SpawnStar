package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.messages.MessageId;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractSubcommand implements Subcommand {

	private String name;
	private List<String> aliases = new ArrayList<>();
	private String usageString;
	private MessageId description;
	private int minArgs;
	private int maxArgs;


	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(final String name) {
		this.name = name;
	}

	@Override
	public final List<String> getAliases() {
		return aliases;
	}

	@Override
	public final void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public final void addAlias(String alias) {
		this.aliases.add(alias);
	}

	@Override
	public final String getUsage() {
		return usageString;
	}

	@Override
	public final void displayUsage(CommandSender sender) {
		sender.sendMessage(usageString);
	}

	@Override
	public final void setUsage(String usageString) {
		this.usageString = usageString;
	}

	@Override
	public final MessageId getDescription() {
		return description;
	}

	@Override
	public final void setDescription(final MessageId description) {
		this.description = description;
	}

	@Override
	public final int getMinArgs() { return minArgs; }

	@Override
	public final void setMinArgs(int minArgs) {
		this.minArgs = minArgs;
	}

	@Override
	public final int getMaxArgs() { return maxArgs; }

	@Override
	public final void setMaxArgs(int maxArgs) {
		this.maxArgs = maxArgs;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		return Collections.emptyList();
	}

}
