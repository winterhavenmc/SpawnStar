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

import com.winterhavenmc.spawnstar.PluginMain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


/**
 * Factory class for creating and testing SpawnStar item stacks
 */
public final class SpawnStarUtility {

	// reference to main class
	private final PluginMain plugin;

	// name spaced key for persistent data
	private final NamespacedKey PERSISTENT_KEY;

	// item metadata flags
	private static final Set<ItemFlag> itemFlagSet = Set.of(
					ItemFlag.HIDE_ATTRIBUTES,
					ItemFlag.HIDE_ENCHANTS,
					ItemFlag.HIDE_UNBREAKABLE );


	/**
	 * Class constructor
	 *
	 * @param plugin reference to the plugin main class instance
	 */
	public SpawnStarUtility(final PluginMain plugin) {
		this.plugin = plugin;
		this.PERSISTENT_KEY = new NamespacedKey(plugin, "isSpawnStar");
	}


	/**
	 * Create a SpawnStar item stack with custom display name and lore
	 *
	 * @return ItemStack of single SpawnStar item
	 */
	public ItemStack create() {
		return create(1);
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public ItemStack create(final int passedQuantity) {

		// get clone of proto item
		ItemStack itemStack = getDefaultItemStack();

		// validate passed quantity (between 1 and material max stack size)
		int quantity = passedQuantity;
		quantity = Math.max(1, quantity);
		quantity = Math.min(quantity, itemStack.getType().getMaxStackSize());

		// set quantity
		itemStack.setAmount(quantity);

		// set item meta data
		setMetaData(itemStack);

		// return cloned item
		return itemStack;
	}


	/**
	 * Check if itemStack is a SpawnStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a SpawnStar item, {@code false} if not
	 */
	public boolean isItem(final ItemStack itemStack) {

		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return false;
		}

		// if item stack does not have metadata return false
		if (!itemStack.hasItemMeta()) {
			return false;
		}

		// if item stack has persistent data tag, return true; otherwise return false
		//noinspection ConstantConditions
		return itemStack.getItemMeta().getPersistentDataContainer().has(PERSISTENT_KEY, PersistentDataType.BYTE);
	}


	/**
	 * Create an itemStack with default material and data from config
	 *
	 * @return ItemStack
	 */
	public ItemStack getDefaultItemStack() {

		// try to match material
		Material configMaterial = Material.matchMaterial(
				Objects.requireNonNull(plugin.getConfig().getString("item-material")));

		// if no match default to nether star
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}

		// return item stack with configured material
		return new ItemStack(configMaterial, 1);
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a HomeStar item.
	 *
	 * @param itemStack the ItemStack on which to set HomeStar MetaData
	 */
	public void setMetaData(final ItemStack itemStack) {

		// retrieve item name and lore from language file
		String itemName = plugin.messageBuilder.getItemName().orElse("SpawnStar");
		List<String> configLore = plugin.messageBuilder.getItemLore();

		// allow for '&' character for color codes in name and lore
		itemName = ChatColor.translateAlternateColorCodes('&', itemName);

		ArrayList<String> coloredLore = new ArrayList<>();

		for (String line : configLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}

		// get item metadata object
		final ItemMeta itemMeta = itemStack.getItemMeta();

		// set item metadata display name to value from config file
		//noinspection ConstantConditions
		itemMeta.setDisplayName(itemName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// set persistent data in item metadata
		itemMeta.getPersistentDataContainer().set(PERSISTENT_KEY, PersistentDataType.BYTE, (byte) 1);

		// set item metadata flags
		for (ItemFlag itemFlag : itemFlagSet) {
			itemMeta.addItemFlags(itemFlag);
		}

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}

}
