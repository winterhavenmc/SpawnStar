package com.winterhaven_mc.spawnstar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Implements inventory manager for <code>SpawnStar</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class InventoryManager {

	private final SpawnStarMain plugin; // reference to main class

	
	/**
	 * Constructor method for class
	 * @param plugin
	 */
	public InventoryManager(SpawnStarMain plugin) {
		this.plugin = plugin;
	}

	
	/**
	 * Create SpawnStar item with attributes from config
	 * @param quantity
	 * @return
	 */
	public ItemStack createSpawnStarItem(int quantity) {

		// retrieve item name and lore, material and durability from config file
		String config_itemname = plugin.getConfig().getString("itemname", "SpawnStar");
		List<String> config_itemlore = plugin.getConfig().getStringList("itemlore");
		Material config_itemmaterial = Material.matchMaterial(plugin.getConfig().getString("itemmaterial", "NETHER_STAR"));
		Short item_durability = (short)plugin.getConfig().getInt("itemdurability", 0);

		// create itemstack of itemmaterial from config with specified quantity
		ItemStack new_item = new ItemStack(config_itemmaterial, 1);
		
		// allow for '&' character for color codes in name and lore
		config_itemname = ChatColor.translateAlternateColorCodes('&', config_itemname);
		ArrayList<String> coloredlore = new ArrayList<String>();
		for (String line : config_itemlore) {
			coloredlore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		// get item metadata
		ItemMeta new_itemmeta = new_item.getItemMeta();
		
		// set item metadata DisplayName to value from config file
		new_itemmeta.setDisplayName(config_itemname);

		// set item metadata Lore to value from config file
		new_itemmeta.setLore(coloredlore);

		// save new item metadata
		new_item.setItemMeta(new_itemmeta);
		
		// set item durability to value from config file
		new_item.setDurability(item_durability.shortValue());
		
		// set itemstack quantity
		new_item.setAmount(quantity);
		
		// return itemstack
		return new_item;
	}

}

