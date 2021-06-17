package com.winterhaven_mc.spawnstar.util;

import com.winterhaven_mc.spawnstar.PluginMain;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
	protected final Material defaultMaterial = Material.NETHER_STAR;
	protected final Material material;
	protected final int quantity;
	protected final ItemStack protoItem;
	protected final String itemStackName;
	protected final List<String> itemStackLore;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to the plugin main class instance
	 */
	public SpawnStarFactory(PluginMain plugin) {

		this.plugin = plugin;

		this.PERSISTENT_KEY = new NamespacedKey(plugin, "isSpawnStar");

		this.quantity = 1;
		this.material = getConfigItemMaterial();
		this.itemStackName = plugin.languageHandler.getItemName();
		this.itemStackLore = plugin.languageHandler.getItemLore();

		this.protoItem = new ItemStack(material, quantity);

		// get item metadata object
		final ItemMeta itemMeta = protoItem.getItemMeta();

		// set item metadata display name to value from language file
		//noinspection ConstantConditions
		itemMeta.setDisplayName(itemStackName);

		// set item metadata Lore to value from language file
		itemMeta.setLore(itemStackLore);

		// set persistent data in item metadata
		itemMeta.getPersistentDataContainer().set(PERSISTENT_KEY, PersistentDataType.BYTE, (byte) 1);

		// save new item metadata
		protoItem.setItemMeta(itemMeta);
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @return ItemStack of SpawnStar items
	 */
	public final ItemStack create() {
		return  this.protoItem.clone();
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public final ItemStack create(final int passedQuantity) {

		// get clone of proto item
		ItemStack clonedItem = this.protoItem.clone();

		// validate passed quantity
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
	 * Get material defined in config, or default material defined in class field
	 *
	 * @return Material
	 */
	protected final Material getConfigItemMaterial() {

		// get default material string from configuration file
		String configMaterialString = plugin.getConfig().getString("item-material");

		// if config material string is null, return default material
		if (configMaterialString == null) {
			return defaultMaterial;
		}

		// try to match material
		Material configMaterial = Material.matchMaterial(configMaterialString);

		// if no match, return default material
		if (configMaterial == null) {
			return defaultMaterial;
		}

		// return material
		return configMaterial;
	}


	/**
	 * Reload plugin's SpawnStarFactory. Replaces existing plugin.spawnStarFactory with new instance.
	 */
	public final void reload() {
		plugin.spawnStarFactory = new SpawnStarFactory(plugin);
		plugin.getLogger().info("SpawnStar Factory reloaded.");
	}

}
