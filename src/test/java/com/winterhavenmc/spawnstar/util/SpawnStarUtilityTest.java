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

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhavenmc.spawnstar.PluginMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpawnStarUtilityTest {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private ServerMock server;
	private PluginMain plugin;

	@BeforeAll
	public void setUp() {
		// Start the mock server
		server = MockBukkit.mock();

		// start the mock plugin
		plugin = MockBukkit.load(PluginMain.class);
	}

	@AfterAll
	public void tearDown() {
		// Stop the mock server
		MockBukkit.unmock();
	}


	@Test
	void createTest() {

		// create test item
		ItemStack testItem = plugin.spawnStarUtility.create();

		// assert test item is not null
		assertNotNull(testItem, "create produced null item.");
	}

	@Test
	void isItemTest() {

		// create test item
		ItemStack testItem = plugin.spawnStarUtility.create();

		// assert test item passes isItem tests
		assertTrue(plugin.spawnStarUtility.isItem(testItem), "testItem is not a proper spawn star item.");

		// assert null ItemStack is does not pass isItem test
		assertFalse(plugin.spawnStarUtility.isItem(null), "null ItemStack passed isItem test.");

		// create item without metadata
		ItemStack itemStack = new ItemStack(Material.DIRT);
		assertFalse(plugin.spawnStarUtility.isItem(itemStack), "item stack without metadata passed isItem test.");
	}

	@Test
	void getDefaultItemStackTest() {

		// get item of default material
		ItemStack testItem = plugin.spawnStarUtility.getDefaultItemStack();

		assertEquals(testItem.getType(), Material.NETHER_STAR, "item stack is not NETHER_STAR.");
	}

	@Test
	void setMetaDataTest() {

		// create test item and set metadata
		ItemStack testItem = new ItemStack(Material.DIRT);

		plugin.spawnStarUtility.setMetaData(testItem);

		ItemMeta itemMeta = testItem.getItemMeta();

		assertNotNull(itemMeta, "test item metadata is null");
		assertNotNull(itemMeta.getDisplayName(), "item metadata display name is null.");
		assertFalse(itemMeta.getDisplayName().isBlank(), "item metadata display name is blank.");
		assertEquals("SpawnStar", ChatColor.stripColor(testItem.getItemMeta().getDisplayName()), "new item display name is not SpawnStar.");
		assertNotNull(itemMeta.getLore(), "item lore is null.");
		assertFalse(itemMeta.getLore().isEmpty(), "item lore is empty.");
		assertEquals("Use to Return to World Spawn", ChatColor.stripColor(String.join(" ", itemMeta.getLore())), "new item stack lore does not match default lore.");
	}

}
