package com.winterhavenmc.spawnstar.commands;

import com.winterhavenmc.spawnstar.PluginMain;


public enum SubcommandType {

	DESTROY() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			new DestroyCommand(plugin).register(subcommandRegistry);
		}
	},

	GIVE() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			new GiveCommand(plugin).register(subcommandRegistry);
		}
	},

	HELP() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			new HelpCommand(plugin).register(subcommandRegistry);
		}
	},

	RELOAD() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			new ReloadCommand(plugin).register(subcommandRegistry);
		}
	},

	STATUS() {
		@Override
		void register(final PluginMain plugin, final SubcommandRegistry subcommandRegistry) {
			new StatusCommand(plugin).register(subcommandRegistry);
		}
	};


	abstract void register(PluginMain plugin, SubcommandRegistry subcommandRegistry);

}
