package com.winterhaven_mc.spawnstar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple static API for SpawnStar
 * @author      Tim Savage
 * @version		1.0
 *
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class SimpleAPI {

	private final static PluginMain plugin = PluginMain.instance;
	private final static String itemTag = hiddenString("SpawnStarV1");


	/**
	 * Private class constructor to prevent instantiation
	 */
	private SimpleAPI() {
		throw new AssertionError();
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 * @param quantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public static ItemStack createItem(int quantity) {

		// validate quantity
		quantity = Math.max(quantity, 1);

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
	 * @param itemStack the ItemStack to check
	 * @return {@code true} if itemStack is a SpawnStar item, {@code false} if not
	 */
	public static boolean isSpawnStar(ItemStack itemStack) {

		// if item stack is empty (null or air) return false
		if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
			return false;
		}

		// if item stack does not have display name return false
		if (! itemStack.getItemMeta().hasDisplayName()) {
			return false;
		}

		// get item display name
		String itemDisplayName = itemStack.getItemMeta().getDisplayName();

		// check that name contains hidden token
		return !itemDisplayName.isEmpty() && itemDisplayName.startsWith(itemTag);
	}

	public static Boolean isValidIngredient() {
		return plugin.getConfig().getBoolean("allow-in-recipes");
	}

	public static int getCooldownTime() {
		return plugin.getConfig().getInt("cooldown-time");
	}

	public static int getWarmupTime() {
		return plugin.getConfig().getInt("warmup-time");
	}


	public static int getMinSpawnDistance() {
		return plugin.getConfig().getInt("minimum-distance");
	}


	public static Boolean isCancelledOnDamage() {
		return plugin.getConfig().getBoolean("cancel-on-damage");
	}


	public static Boolean isCancelledOnMovement() {
		return plugin.getConfig().getBoolean("cancel-on-movement");
	}


	public static Boolean isCancelledOnInteraction() {
		return plugin.getConfig().getBoolean("cancel-on-interaction");
	}

	public static Boolean isWarmingUp(Player player) {
		return plugin.teleportManager.isWarmingUp(player);
	}

	public static Boolean isCoolingDown(Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player) > 0;
	}

	public static long cooldownTimeRemaining(Player player) {
		return plugin.teleportManager.getCooldownTimeRemaining(player);
	}

	public static List<String> getEnabledWorldNames() {
		return plugin.worldManager.getEnabledWorldNames();
	}

	public static void cancelTeleport(Player player) {
		plugin.teleportManager.cancelTeleport(player);
	}


	/**
	 * Create an itemStack with default material and data from config
	 * @return ItemStack
	 */
	public static ItemStack getDefaultItem() {

		// try to match material
		Material configMaterial = Material.matchMaterial(plugin.getConfig().getString("item-material"));

		// if no match default to nether star
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}

		// return item stack with configured material and data
		return new ItemStack(configMaterial,1);
	}


	public static String getItemName() {
		return plugin.messageManager.getItemName();
	}


	public static String getItemNamePlural() {
		return plugin.messageManager.getItemNamePlural();
	}


	public static Location getBlockCenteredLocation(final Location location) {

		// if location is null, return null
		if (location == null) {
			return null;
		}

		final World world = location.getWorld();
		int x = location.getBlockX();
		int y = (int)Math.round(location.getY());
		int z = location.getBlockZ();
		return new Location(world, x + 0.5, y, z + 0.5, location.getYaw(), location.getPitch());
	}


	/**
	 * Set ItemMetaData on ItemStack using custom display name and lore from language file.<br>
	 * Display name additionally has hidden itemTag to make it identifiable as a SpawnStar item.
	 * @param itemStack the ItemStack on which to set SpawnStar MetaData
	 */
	private static void setMetaData(ItemStack itemStack) {

		// retrieve item name and lore from language file file
		String displayName = plugin.messageManager.getItemName();
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
		itemMeta.setDisplayName(itemTag + displayName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// save new item metadata
		itemStack.setItemMeta(itemMeta);
	}


	@SuppressWarnings("SameParameterValue")
	private static String hiddenString(String s) {
		StringBuilder hidden = new StringBuilder();
		for (char c : s.toCharArray())
			hidden.append(ChatColor.COLOR_CHAR + "").append(c);
		return hidden.toString();
	}

}

