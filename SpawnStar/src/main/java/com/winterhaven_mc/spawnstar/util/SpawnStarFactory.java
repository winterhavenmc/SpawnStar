package com.winterhaven_mc.spawnstar.util;

import com.winterhaven_mc.spawnstar.PluginMain;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;


/**
 * Factory class for creating and testing SpawnStar item stacks
 */
public final class SpawnStarFactory {

	// reference to main class
	private final PluginMain plugin;

	// name spaced key for persistent data
	protected final NamespacedKey PERSISTENT_KEY;

	// item metadata fields
	protected int quantity;
	protected ItemStack itemStack;
	protected String itemStackName;
	protected List<String> itemStackLore;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to the plugin main class instance
	 * @throws AssertionError on attempt to instantiate
	 */
	public SpawnStarFactory(PluginMain plugin) {

		this.plugin = plugin;

		this.PERSISTENT_KEY = new NamespacedKey(plugin, "isSpawnStar");

		this.quantity = 1;
		this.itemStack = getDefaultItemStack();
		this.itemStackName = plugin.languageHandler.getItemName();
		this.itemStackLore = plugin.languageHandler.getItemLore();

		setMetaData(this.itemStack);
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @return ItemStack of SpawnStar items
	 */
	public final ItemStack create() {

		ItemStack clonedItem = this.itemStack.clone();

		// set quantity
		clonedItem.setAmount(quantity);

		// return cloned item
		return clonedItem;
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public final ItemStack create(final int passedQuantity) {

		ItemStack clonedItem = this.itemStack.clone();

		// validate quantity
		int quantity = Math.max(passedQuantity, 1);

		// set quantity
		clonedItem.setAmount(quantity);

		// return cloned item
		return clonedItem;
	}


	/**
	 * Check if itemStack is a SpawnStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a SpawnStar item, {@code false} if not
	 */
	public final boolean isItem(final ItemStack itemStack) {

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
	 * Get item name as configured in language file
	 *
	 * @return String - the item name as currently configured
	 */
	public final String getItemName() {
		return plugin.languageHandler.getItemName();
	}


	/**
	 * Create an itemStack with default material and data from config
	 *
	 * @return ItemStack
	 */
	public final ItemStack getDefaultItemStack() {

		// get default material string from configuration file
		String configMaterialString = plugin.getConfig().getString("item-material");

		// if config material string is null, set to NETHER_STAR
		if (configMaterialString == null) {
			configMaterialString = "NETHER_STAR";
		}

		// try to match material
		Material configMaterial = Material.matchMaterial(configMaterialString);

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
	public final void setMetaData(final ItemStack itemStack) {

		// check for null parameter
		if (itemStack == null) {
			return;
		}

		// retrieve item name and lore from language file
		String itemName = plugin.languageHandler.getItemName();
		List<String> configLore = plugin.languageHandler.getItemLore();

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

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}

}
