package com.winterhaven_mc.spawnstar;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple static API for SpawnStar
 *
 * @author Tim Savage
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class SimpleAPI {

	private final static PluginMain plugin = PluginMain.instance;
	private final static String itemTag = plugin.messageManager.createHiddenString("SpawnStarV1");


	/**
	 * Private class constructor to prevent instantiation
	 */
	private SimpleAPI() {
		throw new AssertionError();
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public static ItemStack createItem(final int passedQuantity) {

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
	public static boolean isSpawnStar(final ItemStack itemStack) {

		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return false;
		}

		// if item stack does not have metadata return false
		if (!itemStack.hasItemMeta()) {
			return false;
		}

		// if item stack does not have display name return false
		//noinspection ConstantConditions
		if (!itemStack.getItemMeta().hasDisplayName()) {
			return false;
		}

		// get item display name
		String itemDisplayName = itemStack.getItemMeta().getDisplayName();

		// check that name contains hidden token
		return !itemDisplayName.isEmpty() && itemDisplayName.startsWith(itemTag);
	}


	/**
	 * Check configuration setting allow-in-recipes
	 *
	 * @return configuration setting true or false
	 */
	public static Boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}


	/**
	 * Get configured cooldown time
	 *
	 * @return int configured cooldown time in seconds
	 */
	public static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}


	/**
	 * Get configured warmup time
	 *
	 * @return int configured warmup time in seconds
	 */
	public static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	/**
	 * Get configured minimum distance from spawn for SpawnStar use
	 *
	 * @return int minimum distance in blocks
	 */
	public static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	/**
	 * Get configured cancel on damage setting
	 *
	 * @return boolean config value
	 */
	public static boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	/**
	 * Get configured cancel on movement setting
	 *
	 * @return boolean config value
	 */
	public static boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	/**
	 * Get configured cancel on interaction setting
	 *
	 * @return boolean config setting
	 */
	public static boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}


	/**
	 * Check if player is currently warming up for pending teleport
	 *
	 * @param player the player to check for pending teleport
	 * @return boolean true if player is pending teleport, false if not
	 */
	public static boolean isWarmingUp(Player player) {

		//check for null parameter
		if (player == null) {
			return false;
		}

		return plugin.teleportManager.isWarmingUp(player);
	}


	/**
	 * Check if player is currently cooling down before being allowed to
	 * use a SpawnStar item for teleporting
	 *
	 * @param player the player to check for cooldown
	 * @return boolean true if player is cooling down, false if ready to use SpawnStar item
	 */
	public static boolean isCoolingDown(final Player player) {

		//check for null parameter
		if (player == null) {
			return false;
		}

		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}


	/**
	 * Get time remaining before player is allowed to use another SpawnStar item
	 *
	 * @param player the player for which to fetch cooldown time
	 * @return long the time remaining before SpawnStar use will be allowed
	 */
	public static long cooldownTimeRemaining(final Player player) {

		//check for null parameter
		if (player == null) {
			return 0L;
		}

		return plugin.teleportManager.getCooldownTimeRemaining(player);
	}


	/**
	 * Get list of enabled worlds configured
	 *
	 * @return List of String - enabled world names
	 */
	public static List<String> getEnabledWorldNames() {
		return plugin.worldManager.getEnabledWorldNames();
	}


	/**
	 * Cancel a pending teleport for player
	 *
	 * @param player the player to cancel pending teleport
	 */
	public static void cancelTeleport(final Player player) {

		//check for null parameter
		if (player == null) {
			return;
		}

		plugin.teleportManager.cancelTeleport(player);
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
	 * Get item name as configured in language file
	 *
	 * @return String - the item name as currently configured
	 */
	public static String getItemName() {
		return plugin.messageManager.getItemName();
	}


	/**
	 * Get item plural name as configured in language file
	 *
	 * @return String - the item plural name as currently configured
	 */
	public static String getItemNamePlural() {
		return plugin.messageManager.getItemNamePlural();
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a SpawnStar item.
	 *
	 * @param itemStack the ItemStack on which to set SpawnStar MetaData
	 */
	private static void setMetaData(final ItemStack itemStack) {

		// check for null parameter
		if (itemStack == null) {
			return;
		}

		// retrieve item name and lore from language file
		String displayName = plugin.messageManager.getItemName();
		//noinspection unchecked
		List<String> configLore = plugin.messageManager.getItemLore();

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
		itemMeta.setDisplayName(itemTag + displayName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}

}
