package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.messages.Message;
import com.winterhaven_mc.spawnstar.sounds.SoundId;
import com.winterhaven_mc.util.LanguageManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.spawnstar.messages.MessageId.*;


public class ReloadCommand extends AbstractCommand {

	private final PluginMain plugin;


	ReloadCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("reload");
		this.setUsage("/spawnstar reload");
		this.setDescription(COMMAND_HELP_RELOAD);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if sender does not have permission to reload config, send error message and return
		if (!sender.hasPermission("spawnstar.reload")) {
			Message.create(sender, COMMAND_FAIL_RELOAD_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.size() > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
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
		LanguageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send();
		return true;
	}

}
