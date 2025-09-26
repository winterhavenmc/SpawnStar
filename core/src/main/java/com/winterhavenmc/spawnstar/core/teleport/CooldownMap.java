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

package com.winterhavenmc.spawnstar.core.teleport;

import com.winterhavenmc.library.time.TimeUnit;
import com.winterhavenmc.spawnstar.core.context.TeleportCtx;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


class CooldownMap
{
	private final TeleportCtx ctx;

	// hashmap to store player UUID and cooldown expire time in milliseconds
	private final ConcurrentHashMap<UUID, Instant> cooldownMap;


	CooldownMap(final TeleportCtx ctx)
	{
		this.ctx = ctx;
		cooldownMap = new ConcurrentHashMap<>();
	}


	/**
	 * Insert player uuid into cooldown hashmap with {@code expiretime} as value.<br>
	 * Schedule task to remove player uuid from cooldown hashmap when time expires.
	 *
	 * @param player the player being inserted into the cooldown map
	 */
	void startPlayerCooldown(final Player player)
	{
		int cooldownSeconds = ctx.plugin().getConfig().getInt("teleport-cooldown");
		Duration expTime = Duration.ofSeconds(cooldownSeconds);
		cooldownMap.put(player.getUniqueId(), Instant.now().plus(expTime));
		new CooldownTask(player).runTaskLater(ctx.plugin(), TimeUnit.SECONDS.toTicks(cooldownSeconds));
	}


	/**
	 * Get time remaining for player cooldown
	 *
	 * @param player the player whose cooldown time remaining is being retrieved
	 * @return remaining time as {@link Duration}
	 */
	Duration getCooldownTimeRemaining(final Player player)
	{
		if (cooldownMap.containsKey(player.getUniqueId()))
		{
			Instant expInstant = cooldownMap.get(player.getUniqueId());
			if (expInstant.isAfter(Instant.now()))
			{
				return Duration.between(Instant.now(), expInstant);
			}
		}

		return Duration.ZERO;
	}


	/**
	 * Test if player is currently cooling down after item use
	 *
	 * @param player the player to check for cooldown
	 * @return boolean - {@code true} if player is cooling down after item use, {@code false} if not
	 */
	boolean isCoolingDown(final Player player)
	{
		return getCooldownTimeRemaining(player).isPositive();
	}


	private class CooldownTask extends BukkitRunnable
	{
		private final Player player;

		public CooldownTask(Player player)
		{
			this.player = player;
		}

		public void run()
		{
			cooldownMap.remove(player.getUniqueId());
		}
	}

}
