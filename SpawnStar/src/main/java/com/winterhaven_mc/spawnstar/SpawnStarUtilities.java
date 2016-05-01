package com.winterhaven_mc.spawnstar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;


/**
 * Implements SpawnStarAPI.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class SpawnStarUtilities implements SpawnStarAPI {

	// reference ItemStack for making comparisons
	private static ItemStack standardSpawnStar;
	
	
	static ItemStack createItem(final int qty) {
		
		final int quantity = Math.max(qty, 1);
		
		// set material type from config file
		String[] configMaterialElements = SpawnStarMain.instance.getConfig().getString("item-material").split("\\s*:\\s*");
		Material configMaterial = Material.matchMaterial(configMaterialElements[0]);
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}
		
		// parse material data from config file if present
		byte configMaterialDataByte;
		
		if (configMaterialElements.length > 1) {
			try {
				configMaterialDataByte = Byte.parseByte(configMaterialElements[1]);
			}
			catch (NumberFormatException e) {
				configMaterialDataByte = (byte) 0;
			}
		}
		else {
			configMaterialDataByte = (byte) 0;
		}
		
		// create item stack with configured material and data
		ItemStack newItem = new ItemStack(configMaterial,quantity,configMaterialDataByte);
		
		// retrieve item name and lore from language file file
		String configItemName = SpawnStarMain.instance.messageManager.getItemName();
		List<String> configItemLore = SpawnStarMain.instance.messageManager.getItemLore();

		// allow for '&' character for color codes in name and lore
		configItemName = ChatColor.translateAlternateColorCodes('&', configItemName);
		ArrayList<String> coloredLore = new ArrayList<String>();
		for (String line : configItemLore) {
			coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		// get item metadata object
		ItemMeta itemMeta = newItem.getItemMeta();
		
		// set item metadata DisplayName to value from config file
		itemMeta.setDisplayName(configItemName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);
		
		// save new item metadata
		newItem.setItemMeta(itemMeta);

		return newItem;
	}

	static ItemStack getStandard() {
		return SpawnStarUtilities.standardSpawnStar;
	}

	static void setStandard(final ItemStack itemStack) {
		SpawnStarUtilities.standardSpawnStar = itemStack;
	}

	@SuppressWarnings("static-access")
	@Override
	public ItemStack getItem() {
		return this.createItem(1);
	}
	
	@Override
	public Material getItemMaterial() {
		return standardSpawnStar.getType();
	}
	
	@Override
	public MaterialData getItemData() {
		return standardSpawnStar.getData();
	}
	
	@Override
	public String getItemName() {
		return standardSpawnStar.getItemMeta().getDisplayName();
	}

	@Override
	public List<String> getItemLore() {
		return standardSpawnStar.getItemMeta().getLore();
	}

	@Override
	public Boolean isValidIngredient() {
		return SpawnStarMain.instance.getConfig().getBoolean("allow-in-recipes");
	}
	
	@Override
	public int getCooldownTime() {
		return SpawnStarMain.instance.getConfig().getInt("cooldown-time");
	}

	@Override
	public int getWarmupTime() {
		return SpawnStarMain.instance.getConfig().getInt("warmup-time");
	}


	@Override
	public int getMinSpawnDistance() {
		return SpawnStarMain.instance.getConfig().getInt("minimum-distance");
	}


	@Override
	public Boolean isCancelledOnDamage() {
		return SpawnStarMain.instance.getConfig().getBoolean("cancel-on-damage");
	}


	@Override
	public Boolean isCancelledOnMovement() {
		return SpawnStarMain.instance.getConfig().getBoolean("cancel-on-movement");
	}


	@Override
	public Boolean isCancelledOnInteraction() {
		return SpawnStarMain.instance.getConfig().getBoolean("cancel-on-interaction");
	}
	
	@Override
	public Boolean isWarmingUp(final Player player) {
		return SpawnStarMain.instance.warmupManager.isWarmingUp(player);
	}
	
	@Override
	public Boolean isCoolingDown(final Player player) {
		return SpawnStarMain.instance.cooldownManager.getTimeRemaining(player) > 0;
	}
	
	@Override
	public long cooldownTimeRemaining(final Player player) {
		return SpawnStarMain.instance.cooldownManager.getTimeRemaining(player);
	}
	
	@Override
	public List<String> getEnabledWorlds() {
		return SpawnStarMain.instance.commandManager.getEnabledWorlds();
	}
	
	@Override
	public void cancelTeleport(final Player player) {
		SpawnStarMain.instance.warmupManager.cancelTeleport(player);
	}

}

