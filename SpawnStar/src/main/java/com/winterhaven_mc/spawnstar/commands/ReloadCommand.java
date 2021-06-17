package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.messages.Message;
import com.winterhaven_mc.spawnstar.sounds.SoundId;
import com.winterhaven_mc.spawnstar.util.SpawnStarFactory;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.spawnstar.messages.MessageId.*;


public class ReloadCommand extends AbstractSubcommand {

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
			Message.create(sender, COMMAND_FAIL_RELOAD_PERMISSION).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs()) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send(plugin.languageHandler);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// reinstall main configuration file if necessary
		plugin.saveDefaultConfig();

		// reload main configuration
		plugin.reloadConfig();

		// reload debug global
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		plugin.languageHandler.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload item factory
		plugin.spawnStarFactory.reload();

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send(plugin.languageHandler);
		return true;
	}

}
