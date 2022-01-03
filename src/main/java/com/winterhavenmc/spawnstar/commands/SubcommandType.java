package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;


public enum SubcommandType {

	DESTROY() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			new DestroyCommand(plugin).register(subcommandMap);
		}
	},

	GIVE() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			new GiveCommand(plugin).register(subcommandMap);
		}
	},

	HELP() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			new HelpCommand(plugin).register(subcommandMap);
		}
	},

	RELOAD() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			new ReloadCommand(plugin).register(subcommandMap);
		}
	},

	STATUS() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			new StatusCommand(plugin).register(subcommandMap);
		}
	};


	abstract void register(PluginMain plugin, SubcommandMap subcommandMap);

}
