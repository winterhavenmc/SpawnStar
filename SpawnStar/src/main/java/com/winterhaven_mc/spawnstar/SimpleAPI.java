package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.util.SpawnStar;
import com.winterhaven_mc.util.LanguageManager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


/**
 * A simple static API for SpawnStar
 *
 * @author Tim Savage
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class SimpleAPI {

	// reference to main class
	private final static PluginMain plugin = JavaPlugin.getPlugin(PluginMain.class);

	// persistent metadata name spaced key
	private final static NamespacedKey itemKey = new NamespacedKey(plugin, "isSpawnStar");


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
	 * @deprecated use SpawnStar.create(quantity) method
	 */
	public static ItemStack createItem(final int passedQuantity) {
		return SpawnStar.create(passedQuantity);
	}


	/**
	 * Check if itemStack is a SpawnStar item
	 *
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a SpawnStar item, {@code false} if not
	 * @deprecated use SpawnStar.isItem(itemStack) method
	 */
	public static boolean isSpawnStar(final ItemStack itemStack) {
		return SpawnStar.isItem(itemStack);
	}


	/**
	 * Check configuration setting allow-in-recipes
	 *
	 * @return configuration setting true or false
	 * @deprecated plugin config values are accessible through server's plugin manager
	 */
	public static Boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}


	/**
	 * Get configured cooldown time
	 *
	 * @return int configured cooldown time in seconds
	 * @deprecated config values are accessible through server's plugin manager
	 */
	public static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}


	/**
	 * Get configured warmup time
	 *
	 * @return int configured warmup time in seconds
	 * @deprecated config values are accessible through server's plugin manager
	 */
	public static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	/**
	 * Get configured minimum distance from spawn for SpawnStar use
	 *
	 * @return int minimum distance in blocks
	 * @deprecated config values are accessible through server's plugin manager
	 */
	public static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	/**
	 * Get configured cancel on damage setting
	 *
	 * @return boolean config value
	 * @deprecated config values are accessible through server's plugin manager
	 */
	public static boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	/**
	 * Get configured cancel on movement setting
	 *
	 * @return boolean config value
	 * @deprecated config values are accessible through server's plugin manager
	 */
	public static boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	/**
	 * Get configured cancel on interaction setting
	 *
	 * @return boolean config setting
	 * @deprecated config values are accessible through server's plugin manager
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
		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}


	/**
	 * Get time remaining before player is allowed to use another SpawnStar item
	 *
	 * @param player the player for which to fetch cooldown time
	 * @return long the time remaining before SpawnStar use will be allowed
	 */
	public static long cooldownTimeRemaining(final Player player) {
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
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Create an itemStack with default material and data from config
	 *
	 * @return ItemStack
	 * @deprecated use SpawnStar.getDefaultItem() method
	 */
	public static ItemStack getDefaultItem() {
		return SpawnStar.getDefaultItem();
	}


	/**
	 * Get item name as configured in language file
	 *
	 * @return String - the item name as currently configured
	 * @deprecated use LanguageManager getItemName() method
	 */
	public static String getItemName() {
		return LanguageManager.getInstance().getItemName();
	}


	/**
	 * Get item plural name as configured in language file
	 *
	 * @return String - the item plural name as currently configured
	 * @deprecated use LanguageManager getItemNamePlural() method
	 */
	public static String getItemNamePlural() {
		return LanguageManager.getInstance().getItemNamePlural();
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a SpawnStar item.
	 *
	 * @param itemStack the ItemStack on which to set SpawnStar MetaData
	 * @deprecated use SpawnStar.setMetaData(itemStack) method
	 */
	private static void setMetaData(final ItemStack itemStack) {
		SpawnStar.setMetaData(itemStack);
	}

}
