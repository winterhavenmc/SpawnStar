package com.winterhaven_mc.spawnstar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Implements command executor for <code>CreativeNoNo</code> commands.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class CommandManager implements CommandExecutor {

	private final SpawnStarMain plugin; // reference to main class


	/**
	 * constructor method for <code>CommandManager</code> class
	 * 
	 * @param	plugin		A reference to this plugin's main class
	 */
	public CommandManager(SpawnStarMain plugin) {
		this.plugin = plugin;
	}

	
	/** command executor method for SpawnStar
	 * 
	 */	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		int maxArgs = 3;
		if (args.length > maxArgs) {
			sender.sendMessage((Object)ChatColor.RED + "[SpawnStar] Too many arguments.");
			return false;
		}
		if (args.length < 1) {
			String versionString = this.plugin.getDescription().getVersion();
			sender.sendMessage((Object)ChatColor.AQUA + "[SpawnStar] Version: " + (Object)ChatColor.RESET + versionString);
			sender.sendMessage((Object)ChatColor.GREEN + "Item: " + (Object)ChatColor.RESET + this.plugin.getConfig().getString("itemmaterial"));
			sender.sendMessage((Object)ChatColor.GREEN + "Item Data: " + (Object)ChatColor.RESET + this.plugin.getConfig().getString("itemdata"));
			sender.sendMessage((Object)ChatColor.GREEN + "Item Durability: " + (Object)ChatColor.RESET + this.plugin.getConfig().getString("itemdurability"));
			sender.sendMessage((Object)ChatColor.GREEN + "Minimum Distance: " + (Object)ChatColor.RESET + this.plugin.getConfig().getInt("mindistance"));
			sender.sendMessage((Object)ChatColor.GREEN + "Cooldown: " + (Object)ChatColor.RESET + this.plugin.getConfig().getInt("cooldown"));
			sender.sendMessage((Object)ChatColor.GREEN + "Lightning: " + (Object)ChatColor.RESET + this.plugin.getConfig().getBoolean("lightning"));
			return true;
		}
		String subcmd = args[0];
		if (cmd.getName().equalsIgnoreCase("spawnstar") && subcmd.equalsIgnoreCase("reload")) {
			String original_language = this.plugin.getConfig().getString("language", "en-US");
			this.plugin.reloadConfig();
			if (!original_language.equals(this.plugin.getConfig().getString("language", "en-US"))) {
				this.plugin.messages = new MessageManager(this.plugin);
			} else {
				this.plugin.messages.reloadMessages();
			}
			sender.sendMessage((Object)ChatColor.AQUA + "[SpawnStar] config reloaded.");
			return true;
		}
		if (!cmd.getName().equalsIgnoreCase("spawnstar") || !subcmd.equalsIgnoreCase("give")) return false;
		String playerstring = "";
		Player player = null;
		int quantity = 1;
		if (args.length > 1) {
			playerstring = args[1];
		}
		if (args.length > 2) {
			quantity = Integer.parseInt(args[2]);
		}
		if ((player = Bukkit.getPlayer((String)playerstring)) == null) {
			if (!(sender instanceof Player)) return true;
			this.plugin.messages.sendPlayerMessage((Player)sender, "player-not-found");
			return true;
		}
		if (!player.isOnline()) {
			if (!(sender instanceof Player)) return true;
			this.plugin.messages.sendPlayerMessage((Player)sender, "player-not-online");
			return true;
		}
		player.getInventory().addItem(new ItemStack[]{this.plugin.inventory_manager.createSpawnStarItem(quantity)});
		String plural = "";
		if (quantity > 1) {
			plural = "s";
		}
		String message = "You gave " + quantity + " " + this.plugin.getConfig().getString("itemname", "SpawnStar") + plural + (Object)ChatColor.RESET + " to " + player.getName() + ".";
		sender.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', (String)message));
		return true;
	}
}

