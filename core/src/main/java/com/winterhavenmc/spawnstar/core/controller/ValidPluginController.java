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

package com.winterhavenmc.spawnstar.core.controller;

import com.winterhavenmc.spawnstar.core.context.CommandCtx;
import com.winterhavenmc.spawnstar.core.context.ListenerCtx;
import com.winterhavenmc.spawnstar.core.context.TeleportCtx;
import com.winterhavenmc.spawnstar.core.context.UtilityCtx;
import com.winterhavenmc.spawnstar.core.ports.commands.CommandDispatcher;
import com.winterhavenmc.spawnstar.core.ports.listeners.PlayerEventListener;
import com.winterhavenmc.spawnstar.core.teleport.TeleportHandler;
import com.winterhavenmc.spawnstar.core.util.MetricsHandler;
import com.winterhavenmc.spawnstar.core.util.SpawnStarUtility;

import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.library.soundconfig.SoundConfiguration;
import com.winterhavenmc.library.soundconfig.YamlSoundConfiguration;
import com.winterhavenmc.library.worldmanager.WorldManager;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * A Bukkit/Spigot plugin that allows the use of a custom inventory item to return to the
 * world spawn when used.<br>
 * An alternative to the /spawn command.
 *
 * @author Tim Savage
 */
public final class ValidPluginController implements PluginController
{
	private final JavaPlugin plugin;
	private final MessageBuilder messageBuilder;
	public final SoundConfiguration soundConfig;
	public final WorldManager worldManager;

	public TeleportHandler teleportHandler;
	public CommandDispatcher commandDispatcher;
	public PlayerEventListener playerEventListener;
	public SpawnStarUtility spawnStarUtility;


	public ValidPluginController(final JavaPlugin plugin)
	{
		this.plugin = plugin;

		// install default configuration file if not already present
		plugin.saveDefaultConfig();

		// instantiate message builder
		this.messageBuilder = MessageBuilder.create(plugin);

		// instantiate sound configuration
		this.soundConfig = new YamlSoundConfiguration(plugin);

		// instantiate world manager
		this.worldManager = new WorldManager(plugin);
	}


	public void startUp(final CommandDispatcher commandDispatcher, final PlayerEventListener playerEventListener)
	{
		// instantiate context containers
		CommandCtx commandCtx = new CommandCtx(plugin, messageBuilder, soundConfig, worldManager, spawnStarUtility);
		ListenerCtx listenerCtx = new ListenerCtx(plugin, messageBuilder, soundConfig, worldManager);
		TeleportCtx teleportCtx = new TeleportCtx(plugin, messageBuilder, soundConfig, worldManager);
		UtilityCtx utilityCtx = new UtilityCtx(plugin, messageBuilder);

		// instantiate command manager
		this.commandDispatcher = commandDispatcher.init(commandCtx);

		// instantiate player event listener
		this.playerEventListener = playerEventListener.init(listenerCtx, teleportHandler);

		// instantiate teleport manager
		this.teleportHandler = new TeleportHandler(teleportCtx);

		// instantiate SpawnStar item utility
		this.spawnStarUtility = new SpawnStarUtility(utilityCtx);

		// instantiate metrics handler
		new MetricsHandler(plugin);
	}

}
