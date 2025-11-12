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

package com.winterhavenmc.spawnstar.adapters.commands.bukkit;


public enum SubcommandType
{
	DESTROY()
			{
				@Override
				public Subcommand create(final CommandCtx ctx)
				{
					return new DestroySubcommand(ctx);
				}
			},

	GIVE()
			{
				@Override
				public Subcommand create(final CommandCtx ctx)
				{
					return new GiveSubcommand(ctx);
				}
			},

	RELOAD()
			{
				@Override
				public Subcommand create(final CommandCtx ctx)
				{
					return new ReloadSubcommand(ctx);
				}
			},

	STATUS()
			{
				@Override
				public Subcommand create(final CommandCtx ctx)
				{
					return new StatusSubcommand(ctx);
				}
			};


	public abstract Subcommand create(final CommandCtx ctx);

}
