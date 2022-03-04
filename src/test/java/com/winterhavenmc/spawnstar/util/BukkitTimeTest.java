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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.winterhavenmc.spawnstar.util.BukkitTime.*;


class BukkitTimeTest {

	// these are the smallest/largest numbers that will not cause an overflow for any timeunit
	private final static long minDays = Long.MIN_VALUE / DAYS.getMillis();
	private final static long maxDays = Long.MAX_VALUE / DAYS.getMillis();

	// an array of values that will be used as durations for tests
	private final static long[] testValues = { minDays, -86400001L, -3600001L, -60001L,  -1001L, -51L, -1L, 0L, 1L, 51L, 1001L, 60001L, 3600001L, 86400001L, maxDays };


	@ParameterizedTest
	@EnumSource(BukkitTime.class)
	@DisplayName("Overflow tests")
	void overflowTests(BukkitTime timeUnit) {

		// skip millisecond overflow tests, as they cannot overflow
		if (timeUnit != MILLISECONDS) {

			// calculate values that will cause overflow for the time unit being tested
			long minOverflowValue = Long.MIN_VALUE / timeUnit.getMillis() - 1;
			long maxOverflowValue = Long.MAX_VALUE / timeUnit.getMillis() + 1;

			// negative overflow tests
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toMillis(minOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toTicks(minOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toSeconds(minOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toMinutes(minOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toHours(minOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toDays(minOverflowValue));

			// test convert method for negative overflow
			for (BukkitTime innerTimeUnit : BukkitTime.values()) {
				Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.convert(minOverflowValue, innerTimeUnit));
			}

			// positive overflow tests
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toMillis(maxOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toTicks(maxOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toSeconds(maxOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toMinutes(maxOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toHours(maxOverflowValue));
			Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.toDays(maxOverflowValue));

			// test convert method for positive overflow
			for (BukkitTime innerTimeUnit : BukkitTime.values()) {
				Assertions.assertThrows(IllegalArgumentException.class, () -> timeUnit.convert(maxOverflowValue, innerTimeUnit));
			}
		}
	}


	@Test
	void toMillis() {
		// check that millis field holds correct value for milliseconds in a millisecond (1)
		Assertions.assertEquals(1, MILLISECONDS.getMillis());

		// iterate over test values array, testing each time unit conversion to milliseconds
		for (long duration : testValues) {
			Assertions.assertEquals(duration, MILLISECONDS.toMillis(duration), "MILLISECONDS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 50, TICKS.toMillis(duration), "TICKS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1000, SECONDS.toMillis(duration), "SECONDS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60000, MINUTES.toMillis(duration), "MINUTES to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 3600000, HOURS.toMillis(duration), "HOURS to millis failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 86400000, DAYS.toMillis(duration), "DAYS to millis failed with duration " + duration + ".");
		}
	}


	@Test
	void toTicks() {
		// check that millis field holds correct value for milliseconds in a tick (50)
		Assertions.assertEquals(50, TICKS.getMillis());

		// iterate over test values array, testing each time unit conversion to ticks
		for (long duration : testValues) {
			Assertions.assertEquals(duration / 50, MILLISECONDS.toTicks(duration), "MILLISECONDS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration, TICKS.toTicks(duration), "TICKS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 20, SECONDS.toTicks(duration), "SECONDS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1200, MINUTES.toTicks(duration), "MINUTES to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 72000, HOURS.toTicks(duration), "HOURS to ticks failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1728000, DAYS.toTicks(duration), "DAYS to ticks failed with duration " + duration + ".");
		}
	}


	@Test
	void toSeconds() {
		// check that millis field holds correct value for milliseconds in a second (1000)
		Assertions.assertEquals(1000, SECONDS.getMillis());

		// iterate over test values array, testing each time unit conversion to seconds
		for (long duration : testValues) {
			Assertions.assertEquals(duration / 1000, MILLISECONDS.toSeconds(duration), "MILLISECONDS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 20, TICKS.toSeconds(duration), "TICKS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration, SECONDS.toSeconds(duration), "SECONDS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60, MINUTES.toSeconds(duration), "MINUTES to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 3600, HOURS.toSeconds(duration), "HOURS to seconds failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 86400, DAYS.toSeconds(duration), "DAYS to seconds failed with duration " + duration + ".");
		}
	}


	@Test
	void toMinutes() {
		// check that millis field holds correct value for milliseconds in a minute (60000)
		Assertions.assertEquals(60 * 1000, MINUTES.getMillis());

		// iterate over test values array, testing each time unit conversion to minutes
		for (long duration : testValues) {
			Assertions.assertEquals(duration / 60000, MILLISECONDS.toMinutes(duration), "MILLISECONDS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1200, TICKS.toMinutes(duration), "TICKS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 60, SECONDS.toMinutes(duration), "SECONDS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration, MINUTES.toMinutes(duration), "MINUTES to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 60, HOURS.toMinutes(duration), "HOURS to minutes failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 1440, DAYS.toMinutes(duration), "DAYS to minutes failed with duration " + duration + ".");
		}
	}


	@Test
	void toHours() {
		// check that millis field holds correct value for milliseconds in an hour (3600000)
		Assertions.assertEquals(60 * 60 * 1000, HOURS.getMillis());

		// iterate over test values array, testing each time unit conversion to hours
		for (long duration : testValues) {
			Assertions.assertEquals(duration / 3600000, MILLISECONDS.toHours(duration), "MILLISECONDS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 72000, TICKS.toHours(duration), "TICKS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 3600, SECONDS.toHours(duration), "SECONDS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 60, MINUTES.toHours(duration), "MINUTES to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration, HOURS.toHours(duration), "HOURS to hours failed with duration " + duration + ".");
			Assertions.assertEquals(duration * 24, DAYS.toHours(duration), "DAYS to hours failed with duration " + duration + ".");
		}
	}


	@Test
	void toDays() {
		// check that millis field holds correct value for milliseconds in a day (86400000)
		Assertions.assertEquals(24 * 60 * 60 * 1000, DAYS.getMillis());

		// iterate over test values array, testing each time unit conversion to days
		for (long duration : testValues) {
			Assertions.assertEquals(duration / 86400000, MILLISECONDS.toDays(duration), "MILLISECONDS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1728000, TICKS.toDays(duration), "TICKS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 86400, SECONDS.toDays(duration), "SECONDS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 1440, MINUTES.toDays(duration), "MINUTES to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration / 24, HOURS.toDays(duration), "HOURS to days failed with duration " + duration + ".");
			Assertions.assertEquals(duration, DAYS.toDays(duration), "DAYS to days failed with duration " + duration + ".");
		}
	}


	@ParameterizedTest
	@EnumSource(BukkitTime.class)
	@DisplayName("convert method tests")
	void convert(BukkitTime timeUnit) {
		for (long duration : testValues) {
			Assertions.assertEquals(duration * timeUnit.getMillis(), timeUnit.convert(duration, MILLISECONDS), "convert " + timeUnit + " to milliseconds failed.");
			Assertions.assertEquals(duration * timeUnit.getMillis() / 50L, timeUnit.convert(duration, TICKS), "convert " + timeUnit + " to ticks failed.");
			Assertions.assertEquals(duration * timeUnit.getMillis() / 1000L, timeUnit.convert(duration, SECONDS), "convert " + timeUnit + " to seconds failed.");
			Assertions.assertEquals(duration * timeUnit.getMillis() / 60000L, timeUnit.convert(duration, MINUTES), "convert " + timeUnit + " to minutes failed.");
			Assertions.assertEquals(duration * timeUnit.getMillis() / 3600000L, timeUnit.convert(duration, HOURS), "convert " + timeUnit + " to hours failed.");
			Assertions.assertEquals(duration * timeUnit.getMillis() / 86400000L, timeUnit.convert(duration, DAYS), "convert " + timeUnit + " to days failed.");
		}
	}


	@Test
	void values() {
		BukkitTime[] bukkitTimes = { MILLISECONDS, TICKS, SECONDS, MINUTES, HOURS, DAYS };
		Assertions.assertArrayEquals(BukkitTime.values(), bukkitTimes, "BukkitTime values did not match.");
	}


	@Test
	void valueOf() {
		// test all valid member names
		Assertions.assertEquals(MILLISECONDS, BukkitTime.valueOf("MILLISECONDS"));
		Assertions.assertEquals(TICKS, BukkitTime.valueOf("TICKS"));
		Assertions.assertEquals(SECONDS, BukkitTime.valueOf("SECONDS"));
		Assertions.assertEquals(MINUTES, BukkitTime.valueOf("MINUTES"));
		Assertions.assertEquals(HOURS, BukkitTime.valueOf("HOURS"));
		Assertions.assertEquals(DAYS, BukkitTime.valueOf("DAYS"));

		// test invalid member name
		Exception exception = Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> BukkitTime.valueOf("invalid"));

		String expectedMessage = "No enum constant";
		String actualMessage = exception.getMessage();

		Assertions.assertTrue(actualMessage.startsWith(expectedMessage));
	}

}
