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
import com.winterhavenmc.spawnstar.messages.Macro;
import com.winterhavenmc.spawnstar.messages.MessageId;
import com.winterhavenmc.spawnstar.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;


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
		displayPluginVersion(sender);
		displayDebugSetting(sender);
		displayLanguageSetting(sender);
		displayLocaleSetting(sender);
		displayTimezoneSetting(sender);
		displayDefaultMaterialSetting(sender);
		displayMinimumDistanceSetting(sender);
		displayTeleportWarmupSetting(sender);
		displayTeleportCooldownSetting(sender);
		displayShiftClickSetting(sender);
		displayCancelOnMovementSetting(sender);
		displayRemoveFromInventorySetting(sender);
		displayAllowInRecipesSetting(sender);
		displayLightningSetting(sender);
		displayEnabledWorlds(sender);
		displayStatusFooter(sender);

		return true;
	}


	private void displayStatusBanner(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_BANNER)
				.setMacro(Macro.PLUGIN, plugin.getDescription().getName())
				.send();
	}


	private void displayPluginVersion(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_PLUGIN_VERSION)
				.setMacro(Macro.VERSION, plugin.getDescription().getVersion())
				.send();
	}


	private void displayDebugSetting(final CommandSender sender)
	{
		if (plugin.getConfig().getBoolean("debug"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void displayLanguageSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_LANGUAGE)
				.setMacro(Macro.LANGUAGE, plugin.getConfig().getString("language"))
				.send();
	}


	private void displayLocaleSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_LOCALE)
				.setMacro(Macro.LOCALE, plugin.getConfig().getString("locale"))
				.send();
	}


	private void displayTimezoneSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TIMEZONE)
				.setMacro(Macro.TIMEZONE, plugin.getConfig().getString("timezone", ZoneId.systemDefault().toString()))
				.send();
	}


	private void displayDefaultMaterialSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_DEFAULT_MATERIAL)
				.setMacro(Macro.MATERIAL, plugin.getConfig().getString("item-material", ZoneId.systemDefault().toString()))
				.send();
	}


	private void displayMinimumDistanceSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_MINIMUM_DISTANCE)
				.setMacro(Macro.MINIMUM_DISTANCE, plugin.getConfig().getString("minimum-distance"))
				.send();
	}


	private void displayTeleportWarmupSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TELEPORT_WARMUP)
				.setMacro(Macro.DURATION, plugin.getConfig().getString("teleport-warmup"))
				.send();
	}


	private void displayTeleportCooldownSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_TELEPORT_COOLDOWN)
				.setMacro(Macro.DURATION, plugin.getConfig().getString("teleport-cooldown"))
				.send();
	}


	private void displayShiftClickSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_SHIFT_CLICK)
				.setMacro(Macro.BOOLEAN, plugin.getConfig().getString("shift-click"))
				.send();
	}


	private void displayCancelOnMovementSetting(final CommandSender sender)
	{
		sender.sendMessage(ChatColor.GREEN
				+ "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");
	}


	private void displayRemoveFromInventorySetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_INVENTORY_REMOVAL)
				.setMacro(Macro.INVENTORY_REMOVAL, plugin.getConfig().getString("remove-from-inventory"))
				.send();
	}


	private void displayAllowInRecipesSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_ALLOW_IN_RECIPES)
				.setMacro(Macro.BOOLEAN, plugin.getConfig().getString("allow-in-recipes"))
				.send();
	}


	private void displayLightningSetting(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_DISPLAY_LIGHTNING)
				.setMacro(Macro.BOOLEAN, plugin.getConfig().getString("lightning"))
				.send();
	}


	private void displayEnabledWorlds(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_ENABLED_WORLDS)
				.setMacro(Macro.ENABLED_WORLDS, plugin.worldManager.getEnabledWorldNames().toString())
				.send();
	}


	private void displayStatusFooter(final CommandSender sender)
	{
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_STATUS_FOOTER)
				.setMacro(Macro.PLUGIN, plugin.getDescription().getName())
				.setMacro(Macro.URL, "https://github.com/winterhavenmc/MessageBuilderLib")
				.send();
	}

}
