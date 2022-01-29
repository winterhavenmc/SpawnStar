package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;


enum SubcommandType {

	DESTROY() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new DestroyCommand(plugin);
		}
	},

	GIVE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new GiveCommand(plugin);
		}
	},

	RELOAD() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ReloadCommand(plugin);
		}
	},

	STATUS() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new StatusCommand(plugin);
		}
	};


	abstract Subcommand create(PluginMain plugin);

}
