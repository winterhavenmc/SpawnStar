package com.winterhaven_mc.spawnstar.util;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.util.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


public class SpawnStar {

	// reference to main class
	private final static PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

	// reference to language manager
	private final static LanguageManager languageManager = LanguageManager.getInstance();

	// name spaced key for persistent data
	public final static NamespacedKey PERSISTENT_KEY = new NamespacedKey(plugin, "isSpawnStar");


	/**
	 * Private constructor to prevent instantiation
	 *
	 * @throws AssertionError on attempt to instantiate
	 */
	private SpawnStar() {
		throw new AssertionError();
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public static ItemStack create(final int passedQuantity) {

		// validate quantity
		int quantity = Math.max(passedQuantity, 1);

		// create item stack with configured material and data
		final ItemStack newItem = getDefaultItem();

		// set quantity
		newItem.setAmount(quantity);

		// set item display name and lore
		setMetaData(newItem);

		// return new item
		return newItem;
	}


	/**
	 * Check if itemStack is a SpawnStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a SpawnStar item, {@code false} if not
	 */
	public static boolean isItem(final ItemStack itemStack) {

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
	public static ItemStack getDefaultItem() {

		// try to match material
		@SuppressWarnings("ConstantConditions")
		Material configMaterial = Material.matchMaterial(plugin.getConfig().getString("item-material"));

		// if no match default to nether star
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}

		// return item stack with configured material and quantity 1
		return new ItemStack(configMaterial, 1);
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a SpawnStar item.
	 *
	 * @param itemStack the ItemStack on which to set SpawnStar MetaData
	 */
	public static void setMetaData(final ItemStack itemStack) {

		// check for null parameter
		if (itemStack == null) {
			return;
		}

		// retrieve item name and lore from language file
		String displayName = languageManager.getItemName();
		List<String> configLore = languageManager.getItemLore();

		// allow for '&' character for color codes in name and lore
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);

		ArrayList<String> coloredLore = new ArrayList<>();

		for (String line : configLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}

		// get item metadata object
		final ItemMeta itemMeta = itemStack.getItemMeta();

		// set item metadata display name to value from config file
		//noinspection ConstantConditions
		itemMeta.setDisplayName(ChatColor.RESET + displayName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// set persistent data in item metadata
		itemMeta.getPersistentDataContainer().set(PERSISTENT_KEY, PersistentDataType.BYTE, (byte) 1);

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}

}
