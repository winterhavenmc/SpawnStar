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

package com.winterhavenmc.spawnstar.util;

public enum BukkitTime {

	MILLISECONDS(1L),
	TICKS(50L),
	SECONDS(1000L),
	MINUTES(60000L),
	HOURS(3600000L),
	DAYS(86400000L);

	private final long millis;


 BukkitTime(final long millis) {
		this.millis = millis;
	}

	public long toMillis(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / MILLISECONDS.millis;
	}

	public long toTicks(final long duration) throws IllegalArgumentException {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / TICKS.millis;
	}

	public long toSeconds(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / SECONDS.millis;
	}

	public long toMinutes(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / MINUTES.millis;
	}

	public long toHours(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / HOURS.millis;
	}

	public long toDays(final long duration) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / DAYS.millis;
	}

	public long convert(long duration, BukkitTime unit) {
		if (duration < Long.MIN_VALUE / this.millis) throw new IllegalArgumentException("duration would cause underflow.");
		if (duration > Long.MAX_VALUE / this.millis) throw new IllegalArgumentException("duration would cause overflow.");
		return duration * this.millis / unit.millis;
	}

	/**
	 * Get the number of milliseconds for each time unit.
	 * @return the number of milliseconds equal to each time unit
	 */
	long getMillis() {
		return this.millis;
	}

}
