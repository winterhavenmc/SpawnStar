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

import com.winterhavenmc.spawnstar.PluginMain;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

import static com.winterhavenmc.library.TimeUnit.SECONDS;


final class StatusSubcommand extends AbstractSubcommand
{
	private final PluginMain plugin;


	StatusSubcommand(final PluginMain plugin)
	{
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "status";
		this.usage = "/spawnstar status";
		this.description = MessageId.COMMAND_HELP_STATUS;
		this.permissionNode = "spawnstar.status";
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if command sender does not have permission to view status, output error message and return
		if (!sender.hasPermission(permissionNode))
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_STATUS_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// output config settings
		showPluginVersion(sender);
		showDebugSetting(sender);
		showLanguageSetting(sender);
		showDefaultMaterialSetting(sender);
		showMinimumDistanceSetting(sender);
		showTeleportWarmupSetting(sender);
		showTeleportCooldownSetting(sender);
		showShiftClickSetting(sender);
		showCancelOnMovementSetting(sender);
		showRemoveFromInventorySetting(sender);
		showAllowInRecipesSetting(sender);
		showLightningSetting(sender);
		showEnabledWorlds(sender);

		return true;
	}


	private void showPluginVersion(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA + "[SpawnStar] "
				+ ChatColor.AQUA + "Version: " + ChatColor.RESET + plugin.getDescription().getVersion());
	}


	private void showDebugSetting(final CommandSender sender)
	{
		if (plugin.getConfig().getBoolean("debug"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void showLanguageSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));
	}


	private void showDefaultMaterialSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Default material: "
				+ ChatColor.RESET + plugin.getConfig().getString("item-material"));
	}


	private void showMinimumDistanceSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Minimum distance: "
				+ ChatColor.RESET + plugin.getConfig().getInt("minimum-distance"));
	}


	private void showTeleportWarmupSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Warmup: "
				+ ChatColor.RESET
				+ plugin.messageBuilder.getTimeString(SECONDS.toMillis(plugin.getConfig().getInt("teleport-warmup"))));
	}


	private void showTeleportCooldownSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Cooldown: "
				+ ChatColor.RESET
				+ plugin.messageBuilder.getTimeString(SECONDS.toMillis(plugin.getConfig().getInt("teleport-cooldown"))));
	}


	private void showShiftClickSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Shift-click required: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("shift-click"));
	}


	private void showCancelOnMovementSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN
				+ "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");
	}


	private void showRemoveFromInventorySetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Remove from inventory: "
				+ ChatColor.RESET + plugin.getConfig().getString("remove-from-inventory"));
	}


	private void showAllowInRecipesSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Allow in recipes: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("allow-in-recipes"));
	}


	private void showLightningSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Lightning: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("lightning"));

	}


	private void showEnabledWorlds(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN + "Enabled Worlds: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());
	}

}
