package com.winterhaven_mc.spawnstar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Implements SpawnStarStack. Extends <code>ItemStack</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class SpawnStarStack extends ItemStack {

	/**
	 * Class constructor method
	 * @param quantity
	 */
	public SpawnStarStack(int quantity) {
		
		// set material type from config file
		this.setType(Material.matchMaterial(SpawnStarMain.instance.getConfig().getString("itemmaterial", "NETHER_STAR")));

		// retrieve item name and lore from language file file
		String config_itemname = SpawnStarMain.instance.messageManager.getItemName();
		List<String> config_itemlore = SpawnStarMain.instance.messageManager.getItemLore();

		// allow for '&' character for color codes in name and lore
		config_itemname = ChatColor.translateAlternateColorCodes('&', config_itemname);
		ArrayList<String> coloredlore = new ArrayList<String>();
		for (String line : config_itemlore) {
			coloredlore.add(ChatColor.translateAlternateColorCodes('&', line));
		}
		
		// get item metadata
		ItemMeta itemMeta = this.getItemMeta();
		
		// set item metadata DisplayName to value from config file
		itemMeta.setDisplayName(config_itemname);

		// set item metadata Lore to value from config file
		itemMeta.setLore(coloredlore);

		// save new item metadata
		this.setItemMeta(itemMeta);

		// set stack amount
		this.setAmount(quantity);
		
	}


	public Boolean isCraftable() {
		return SpawnStarMain.instance.getConfig().getBoolean("allow-crafting",false);
	}

}

