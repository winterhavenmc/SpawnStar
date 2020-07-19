package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.PluginMain;


public enum SubcommandType {

	DESTROY() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new DestroyCommand(plugin));
		}
	},

	GIVE() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new GiveCommand(plugin));
		}
	},

	HELP() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new HelpCommand(plugin, subcommandMap));
		}
	},

	RELOAD() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ReloadCommand(plugin));
		}
	},

	STATUS() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new StatusCommand(plugin));
		}
	};


	abstract void register(final PluginMain plugin, final SubcommandMap subcommandMap);

}
