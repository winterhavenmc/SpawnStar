/*
 * Copyright (c) 2022-2025 Tim Savage.
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

package com.winterhavenmc.spawnstar.core.commands;

import com.winterhavenmc.library.messagebuilder.resources.configuration.LocaleProvider;
import com.winterhavenmc.spawnstar.core.context.CommandCtx;
import com.winterhavenmc.spawnstar.core.util.Macro;
import com.winterhavenmc.spawnstar.core.util.MessageId;
import com.winterhavenmc.spawnstar.core.util.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;


final class StatusSubcommand extends AbstractSubcommand
{
	private final CommandCtx ctx;
	private final LocaleProvider localeProvider;


	StatusSubcommand(final CommandCtx ctx)
	{
		this.ctx = Objects.requireNonNull(ctx);
		this.name = "status";
		this.usage = "/spawnstar status";
		this.description = MessageId.COMMAND_HELP_STATUS;
		this.permissionNode = "spawnstar.status";
		this.localeProvider = LocaleProvider.create(ctx.plugin());
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args)
	{
		// if command sender does not have permission to view status, output error message and return
		if (!sender.hasPermission(permissionNode))
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_STATUS_PERMISSION).send();
			ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check max arguments
		if (args.size() > getMaxArgs())
		{
			ctx.messageBuilder().compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			ctx.soundConfiguration().playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
			return true;
		}

		// output config settings
		displayStatusHeader(sender);
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
		displayCancelOnDamageSetting(sender);
		displayCancelOnMovementSetting(sender);
		displayCancelOnInteractionSetting(sender);
		displayRemoveFromInventorySetting(sender);
		displayAllowInRecipesSetting(sender);
		displayLightningSetting(sender);
		displayEnabledWorlds(sender);
		displayStatusFooter(sender);

		return true;
	}


	private void displayStatusHeader(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_HEADER).send();
	}


	private void displayPluginVersion(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_PLUGIN_VERSION).send();
	}


	private void displayDebugSetting(final CommandSender sender)
	{
		if (ctx.plugin().getConfig().getBoolean("debug"))
		{
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void displayLanguageSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_LANGUAGE_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("language"))
				.send();
	}


	private void displayLocaleSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_LOCALE_SETTING)
				.setMacro(Macro.SETTING, localeProvider.getLocale().toLanguageTag())
				.send();
	}


	private void displayTimezoneSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_TIMEZONE_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("timezone", ZoneId.systemDefault().toString()))
				.send();
	}


	private void displayDefaultMaterialSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_DEFAULT_MATERIAL_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("item-material"))
				.send();
	}


	private void displayMinimumDistanceSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_MINIMUM_DISTANCE_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("minimum-distance"))
				.send();
	}


	private void displayTeleportWarmupSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_TELEPORT_WARMUP_SETTING)
				.setMacro(Macro.SETTING, Duration.ofSeconds(ctx.plugin().getConfig().getInt("teleport-warmup")))
				.send();
	}


	private void displayTeleportCooldownSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_TELEPORT_COOLDOWN_SETTING)
				.setMacro(Macro.SETTING, Duration.ofSeconds(ctx.plugin().getConfig().getInt("teleport-cooldown")))
				.send();
	}


	private void displayShiftClickSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_SHIFT_CLICK_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getBoolean("shift-click"))
				.send();
	}


	private void displayCancelOnDamageSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_DAMAGE_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getBoolean("cancel-on-damage"))
				.send();
	}

	private void displayCancelOnMovementSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_MOVEMENT_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getBoolean("cancel-on-movement"))
				.send();
	}

	private void displayCancelOnInteractionSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_CANCEL_ON_INTERACTION_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getBoolean("cancel-on-interaction"))
				.send();
	}


	private void displayRemoveFromInventorySetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_INVENTORY_REMOVAL_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("remove-from-inventory"))
				.send();
	}


	private void displayAllowInRecipesSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_ALLOW_IN_RECIPES_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("allow-in-recipes"))
				.send();
	}


	private void displayLightningSetting(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_DISPLAY_LIGHTNING_SETTING)
				.setMacro(Macro.SETTING, ctx.plugin().getConfig().getString("lightning"))
				.send();
	}


	private void displayEnabledWorlds(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_ENABLED_WORLDS_SETTING)
				.setMacro(Macro.SETTING, ctx.worldManager().getEnabledWorldNames().toString())
				.send();
	}


	private void displayStatusFooter(final CommandSender sender)
	{
		ctx.messageBuilder().compose(sender, MessageId.COMMAND_STATUS_FOOTER).send();
	}

}
