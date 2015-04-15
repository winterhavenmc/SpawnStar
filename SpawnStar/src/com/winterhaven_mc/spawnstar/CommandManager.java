package com.winterhaven_mc.spawnstar;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			sender.sendMessage(ChatColor.AQUA + "[SpawnStar] Version: " + ChatColor.RESET + versionString);
			sender.sendMessage(ChatColor.GREEN + "Item: " + ChatColor.RESET + this.plugin.getConfig().getString("itemmaterial"));
			sender.sendMessage(ChatColor.GREEN + "Minimum Distance: " + ChatColor.RESET + this.plugin.getConfig().getInt("mindistance"));
			sender.sendMessage(ChatColor.GREEN + "Shift-click required: " + ChatColor.RESET + this.plugin.getConfig().getString("shift-click","false"));
			sender.sendMessage(ChatColor.GREEN + "Allow crafting: " + ChatColor.RESET + this.plugin.getConfig().getString("allow-crafting","false"));
			sender.sendMessage(ChatColor.GREEN + "Warmup: " + ChatColor.RESET + this.plugin.getConfig().getInt("warmup") + " seconds");
			sender.sendMessage(ChatColor.GREEN + "Cooldown: " + ChatColor.RESET + this.plugin.getConfig().getInt("cooldown") + " seconds");
			sender.sendMessage(ChatColor.GREEN + "Lightning: " + ChatColor.RESET + this.plugin.getConfig().getBoolean("lightning"));
			return true;
		}
		String subcmd = args[0];

		// reload command
		if (cmd.getName().equalsIgnoreCase("spawnstar") &&
				subcmd.equalsIgnoreCase("reload")) {

			// get current language setting
			String original_language = this.plugin.getConfig().getString("language", "en-US");

			// relod config.yml
			plugin.reloadConfig();

			// if language setting has changed, instantiate new message manager with new language file
			if (!original_language.equals(this.plugin.getConfig().getString("language", "en-US"))) {
				plugin.messageManager = new MessageManager(this.plugin);
			}
			else {
				plugin.messageManager.reloadMessages();
			}
			
			// refresh reference item in case changes were made
			plugin.referenceItem = new SpawnStarStack(1);
			
			// send reloaded message to command sender
			sender.sendMessage((Object)ChatColor.AQUA + "[SpawnStar] config reloaded.");
			return true;
		}

		// give command
		if (cmd.getName().equalsIgnoreCase("spawnstar") && subcmd.equalsIgnoreCase("give")) {

			Player player;
			String playerstring = "";
			int quantity = 1;

			if (args.length > 1) {
				playerstring = args[1];
			}
			if (args.length > 2) {
				quantity = Integer.parseInt(args[2]);
			}
			
			// try to match a player from given string
			List<Player> playerList = plugin.getServer().matchPlayer(playerstring);
			
			// if only one matching player, use it, otherwise send error message (no match or more than 1 match)
			if (playerList.size() == 1) {
				player = playerList.get(0);
			}
			else {
				// if unique matching player is not found, send player-not-found message to sender
				plugin.messageManager.sendPlayerMessage((Player)sender, "player-not-found");
				return true;
			}

			// if player is not online, send player-not-online message to sender
			if (!player.isOnline()) {
				plugin.messageManager.sendPlayerMessage((Player)sender, "player-not-online");
				return true;
			}

			// add specified quantity of spawnstar(s) to player inventory
			player.getInventory().addItem(new SpawnStarStack(quantity));
			String plural = "";
			if (quantity > 1) {
				plural = "s";
			}

			// strip color codes from item name
			String itemname = plugin.getConfig().getString("itemname","SpawnStar").replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");

			// construct message
			String message = "You gave " + quantity + " " + itemname + plural + " to " + player.getName() + ".";

			// send message
			sender.sendMessage(ChatColor.translateAlternateColorCodes((char)'&', message));
			return true;
		}
		return false;
	}
}
