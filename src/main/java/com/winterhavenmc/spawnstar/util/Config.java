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

package com.winterhavenmc.spawnstar.util;


public enum Config
{
	LANGUAGE("en-US"),
	ENABLED_WORLDS("[]"),
	DISABLED_WORLDS("[disabled_world1, disabled_world2]"),
	ITEM_MATERIAL("NETHER_STAR"),
	MINIMUM_DISTANCE("10"),
	TELEPORT_COOLDOWN("60"),
	TELEPORT_WARMUP("5"),
	PARTICLE_EFFECTS("true"),
	SOUND_EFFECTS("true"),
	TITLES_ENABLED("true"),
	SHIFT_CLICK("true"),
	REMOVE_FROM_INVENTORY("on-success"),
	ALLOW_IN_RECIPES("false"),
	CANCEL_ON_DAMAGE("false"),
	CANCEL_ON_MOVEMENT("false"),
	CANCEL_ON_INTERACTION("false"),
	MAX_GIVE_AMOUNT("-1"),
	FROM_NETHER("true"),
	FROM_END("true"),
	LIGHTNING("false"),
	LOG_USE("true");


	private final String value;

	Config(String value)
	{
		this.value = value;
	}

	public String getKey()
	{
		return this.name().toLowerCase().replace('_', '-');
	}

	public String getValue()
	{
		return this.value;
	}

}
