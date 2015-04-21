package com.winterhaven_mc.spawnstar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Implements SpawnStarStack. Extends <code>ItemStack</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class SpawnStarItem extends ItemStack implements SpawnStarAPI {

	// reference ItemStack for making comparisons
	private static ItemStack standardSpawnStar;
	
	/**
	 * Class constructor method with no parameters
	 */
	public SpawnStarItem() {
		
		// set material type from config file
		Material configMaterial = Material.matchMaterial(SpawnStarMain.instance.getConfig().getString("item-material"));
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}
		this.setType(configMaterial);

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
		ItemMeta itemMeta = this.getItemMeta();
		
		// set item metadata DisplayName to value from config file
		itemMeta.setDisplayName(configItemName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// save new item metadata
		this.setItemMeta(itemMeta);

		// set stack amount
		this.setAmount(1);
		
	}


	/**
	 * Class constructor method with stack size
	 * @param quantity
	 */
	public SpawnStarItem(int quantity) {
		
		// set material type from config file
		Material configMaterial = Material.matchMaterial(SpawnStarMain.instance.getConfig().getString("item-material"));
		if (configMaterial == null) {
			configMaterial = Material.NETHER_STAR;
		}
		this.setType(configMaterial);

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
		ItemMeta itemMeta = this.getItemMeta();
		
		// set item metadata DisplayName to value from config file
		itemMeta.setDisplayName(configItemName);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredLore);

		// save new item metadata
		this.setItemMeta(itemMeta);

		// set stack amount
		this.setAmount(quantity);
		
	}

	public static ItemStack getStandard() {
		return SpawnStarItem.standardSpawnStar;
	}

	public static void setStandard(SpawnStarItem standard) {
		SpawnStarItem.standardSpawnStar = standard;
	}

	@Override
	public Material getItemMaterial() {
		return standardSpawnStar.getType();
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
	public Boolean isWarmingUp(Player player) {
		return SpawnStarMain.instance.warmupManager.isWarmingUp(player);
	}
	
	@Override
	public Boolean isCoolingDown(Player player) {
		return SpawnStarMain.instance.cooldownManager.getTimeRemaining(player) > 0;
	}
	
	@Override
	public long cooldownTimeRemaining(Player player) {
		return SpawnStarMain.instance.cooldownManager.getTimeRemaining(player);
	}
	
	@Override
	public List<String> getEnabledWorlds() {
		return SpawnStarMain.instance.commandManager.getEnabledWorlds();
	}
	
	@Override
	public void cancelTeleport(Player player) {
		SpawnStarMain.instance.warmupManager.cancelTeleport(player);
	}

}

