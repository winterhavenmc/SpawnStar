/*
 * Copyright (c) 2025 Tim Savage.
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

package com.winterhavenmc.spawnstar.plugin;

import com.winterhavenmc.spawnstar.adapters.commands.bukkit.BukkitCommandDispatcher;
import com.winterhavenmc.spawnstar.adapters.listeners.bukkit.BukkitPlayerEventListener;
import com.winterhavenmc.spawnstar.core.controller.InvalidPluginController;
import com.winterhavenmc.spawnstar.core.controller.PluginController;
import com.winterhavenmc.spawnstar.core.controller.ValidPluginController;
import com.winterhavenmc.spawnstar.core.ports.commands.CommandDispatcher;
import com.winterhavenmc.spawnstar.core.ports.listeners.PlayerEventListener;
import org.bukkit.plugin.java.JavaPlugin;


public class Bootstrap extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		final CommandDispatcher commandDispatcher = new BukkitCommandDispatcher(); // adapter
		final PlayerEventListener playerEventListener = BukkitPlayerEventListener.create(); // adapter

		final PluginController pluginController = PluginController.create(this); // core controller

		switch (pluginController)
		{
			case ValidPluginController valid -> valid.startUp(commandDispatcher, playerEventListener);
			case InvalidPluginController invalid ->
			{
				this.getLogger().severe("Cannot create valid plugin controller: " + invalid.reason().getDefaultMessage());
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}
}
