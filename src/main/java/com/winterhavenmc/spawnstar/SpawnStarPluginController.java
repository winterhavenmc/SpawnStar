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

package com.winterhavenmc.spawnstar;

import com.winterhavenmc.spawnstar.commands.CommandManager;
import com.winterhavenmc.spawnstar.listeners.PlayerEventListener;
import com.winterhavenmc.spawnstar.teleport.TeleportHandler;
import com.winterhavenmc.spawnstar.util.MetricsHandler;
import com.winterhavenmc.spawnstar.util.SpawnStarUtility;
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
public final class SpawnStarPluginController implements PluginController
{
	private final JavaPlugin plugin;
	public MessageBuilder messageBuilder;
	public SoundConfiguration soundConfig;
	public TeleportHandler teleportHandler;
	public WorldManager worldManager;
	public CommandManager commandManager;
	public PlayerEventListener playerEventListener;
	public SpawnStarUtility spawnStarUtility;


	public SpawnStarPluginController(final JavaPlugin plugin)
	{
		this.plugin = plugin;
	}


	@Override
	public void startUp()
	{
		// install default configuration file if not already present
		plugin.saveDefaultConfig();

		// instantiate message builder
		messageBuilder = MessageBuilder.create(plugin);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(plugin);

		// instantiate world manager
		worldManager = new WorldManager(plugin);

		// instantiate SpawnStar item factory
		spawnStarUtility = new SpawnStarUtility(this);

		// instantiate context containers
		CommandContextContainer commandCtx = new CommandContextContainer(plugin, messageBuilder, soundConfig, worldManager, spawnStarUtility);
		TeleportContextContainer teleportCtx = new TeleportContextContainer(plugin, messageBuilder, soundConfig, worldManager);
		ListenerContextContainer listenerCtx = new ListenerContextContainer(plugin, messageBuilder, soundConfig, worldManager);

		// instantiate command manager
		commandManager = new CommandManager(commandCtx);

		// instantiate teleport manager
		teleportHandler = new TeleportHandler(teleportCtx);

		// instantiate player event listener
		playerEventListener = new PlayerEventListener(listenerCtx, teleportHandler);

		// instantiate metrics handler
		new MetricsHandler(plugin);
	}

}
