package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.sounds.SoundId;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhavenmc.spawnstar.messages.MessageId.*;


final class ReloadCommand extends AbstractSubcommand {

	private final PluginMain plugin;


	ReloadCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "reload";
		this.usage = "/spawnstar reload";
		this.description = COMMAND_HELP_RELOAD;
		this.permission = "spawnstar.reload";
		this.maxArgs = 0;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return
		if (!sender.hasPermission(permission)) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_RELOAD_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reinstall main configuration file if necessary
		plugin.saveDefaultConfig();

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		plugin.messageBuilder.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload item factory
		plugin.spawnStarFactory.reload();

		// send reloaded message
		plugin.messageBuilder.build(sender, COMMAND_SUCCESS_RELOAD).send();
		return true;
	}

}
