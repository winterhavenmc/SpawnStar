package com.winterhaven_mc.spawnstar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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

	// reference to main class
	private final SpawnStarMain plugin;

	// list of enabled world names
	private ArrayList<String> enabledWorlds;
	

	/**
	 * constructor method for <code>CommandManager</code> class
	 * 
	 * @param plugin reference to main class
	 */
	CommandManager(final SpawnStarMain plugin) {
		this.plugin = plugin;
		plugin.getCommand("spawnstar").setExecutor(this);
		updateEnabledWorlds();
	}


	/** command executor method for SpawnStar
	 * 
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, 
			final String label, final String[] args) {
		
		int maxArgs = 3;
		
		if (args.length > maxArgs) {
			plugin.messageManager.sendPlayerMessage(sender,"command-args-count-over");
			if (sender.hasPermission("spawnstar.status")) {
				sender.sendMessage(ChatColor.AQUA + "/spawnstar status");
			}
			if (sender.hasPermission("spawnstar.reload")) {
				sender.sendMessage(ChatColor.AQUA + "/spawnstar reload");
			}
			if (sender.hasPermission("spawnstar.destroy")) {
				sender.sendMessage(ChatColor.AQUA + "/spawnstar destroy");
			}
			if (sender.hasPermission("spawnstar.give")) {
				sender.sendMessage(ChatColor.AQUA + "/spawnstar give <player> [amount]");
			}
			return true;
		}
		
		if (args.length < 1 || args[0].equalsIgnoreCase("status")) {
			return statusCommand(sender,args);
		}
		
		String subcmd = args[0];

		if (subcmd.equalsIgnoreCase("reload")) {	
			return reloadCommand(sender,args);
		}

		if (subcmd.equalsIgnoreCase("give")) {
			return giveCommand(sender,args);
		}
		
		if (subcmd.equalsIgnoreCase("destroy")) {
			return destroyCommand(sender, args);			
		}
		
		return false;
	}

	
	@SuppressWarnings("deprecation")
	boolean giveCommand(final CommandSender sender, final String[] args) {
		
		// if command sender does not have permission to give SpawnStars, output error message and return true
		if (!sender.hasPermission("spawnstar.give")) {
			plugin.messageManager.sendPlayerMessage(sender, "permission-denied-give");
			return true;
		}

		// if too few arguments, send error usage message
		if (args.length < 2) {
			plugin.messageManager.sendPlayerMessage(sender, "command-args-count-under");
			sender.sendMessage(ChatColor.AQUA + "/spawnstar give <player> [quantity]");
			return true;
		}
		
		// if too many arguments, send error and usage message
		if (args.length > 3) {
			plugin.messageManager.sendPlayerMessage(sender, "command-args-count-over");
			sender.sendMessage(ChatColor.AQUA + "/spawnstar give <player> [quantity]");
			return true;				
		}
		
		Player targetPlayer = null;
		String targetPlayerName = "";
		int quantity = 1;

		if (args.length > 1) {
			targetPlayerName = args[1];
		}
		if (args.length > 2) {
			try {
				quantity = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				plugin.messageManager.sendPlayerMessage(sender, "command-fail-quantity-invalid");
				return true;
			}
		}
		
		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = plugin.getConfig().getInt("max-give-amount");
		if (maxQuantity < 0) {
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		// check exact match first
		targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
		
		// if no match, try substring match
		if (targetPlayer == null) {
			List<Player> playerList = plugin.getServer().matchPlayer(targetPlayerName);
			
			// if only one matching player, use it, otherwise send error message (no match or more than 1 match)
			if (playerList.size() == 1) {
				targetPlayer = playerList.get(0);
			}
		}
		
		// if match found, try to give item and return
		if (targetPlayer != null) {
			// add specified quantity of spawnstar(s) to player inventory
			HashMap<Integer,ItemStack> noFit = 
					targetPlayer.getInventory().addItem(SpawnStarUtilities.createItem(quantity));
			
			// count items that didn't fit in inventory
			int noFitCount = 0;
			for (int index : noFit.keySet()) {
				noFitCount += noFit.get(index).getAmount();
			}
			
			// if remaining items equals quantity given, send player-inventory-full message and return
			if (noFitCount == quantity) {
				plugin.messageManager.sendPlayerMessage(sender, "command-fail-give-inventory-full", quantity);
				return true;
			}
			
			// subtract noFitCount from quantity
			quantity = quantity - noFitCount;
			
			// send success message to player
			plugin.messageManager.sendPlayerMessage(sender, "command-success-give", quantity);
			return true;
		}
		
		// check if name matches known offline player
		HashSet<OfflinePlayer> matchedPlayers = new HashSet<OfflinePlayer>();
		for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
			if (targetPlayerName.equalsIgnoreCase(offlinePlayer.getName())) {
				matchedPlayers.add(offlinePlayer);
			}
		}
		if (matchedPlayers.isEmpty()) {
			plugin.messageManager.sendPlayerMessage(sender, "command-fail-player-not-found");
			return true;
		}
		else {
			plugin.messageManager.sendPlayerMessage(sender, "command-fail-player-not-online");
			return true;
		}
	}
	
	
	/**
	 * Destroy command sets item stack in hand amount to zero if it is a SpawnStar item stack
	 * @param sender
	 * @param args
	 * @return
	 */
	boolean destroyCommand(final CommandSender sender, final String[] args) {
		
		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageManager.sendPlayerMessage(sender, "command-fail-destroy-console");
			return true;
		}
		
		// sender must have spawnstar.destroy permission node
		if (!sender.hasPermission("spawnstar.destroy")) {
			plugin.messageManager.sendPlayerMessage(sender, "permission-denied-destroy");
			return true;
		}

		// get player from sender
		Player player = (Player) sender;
		
		// get item stack in player hand
		ItemStack playerItem = player.getInventory().getItemInHand();
		
		// check that held item stack is a SpawnStar item stack
		if (!SpawnStarUtilities.getStandard().isSimilar(playerItem)) {
			plugin.messageManager.sendPlayerMessage(sender, "command-fail-destroy-no-match");
			return true;
		}
		
		// get itemstack amount
		int quantity = playerItem.getAmount();
		
		// set itemstack amount to zero
		playerItem.setAmount(0);
		
		// replace item in hand with empty itemstack
		player.getInventory().setItemInHand(playerItem);
		
		// send player success message
		plugin.messageManager.sendPlayerMessage(sender, "command-success-destroy",quantity);
		
		return true;
	}
	
	
	/**
	 * Status command displays plugin settings
	 * @param sender
	 * @param args
	 * @return
	 */
	boolean statusCommand(final CommandSender sender, final String[] args) {

		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission("spawnstar.status")) {
			plugin.messageManager.sendPlayerMessage(sender, "permission-denied-status");
			return true;
		}

		// output config settings
		String versionString = this.plugin.getDescription().getVersion();
		sender.sendMessage(ChatColor.DARK_AQUA + "[SpawnStar] " + ChatColor.AQUA + "Version: " + ChatColor.RESET + versionString);
		if (plugin.debug) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
		sender.sendMessage(ChatColor.GREEN + "Language: " + ChatColor.RESET + plugin.getConfig().getString("language"));
		//			sender.sendMessage(ChatColor.GREEN + "Item: " + ChatColor.RESET + plugin.getConfig().getString("item-material"));
		sender.sendMessage(ChatColor.GREEN + "Item: " + ChatColor.RESET + SpawnStarUtilities.getStandard().getType().toString());
		sender.sendMessage(ChatColor.GREEN + "Data: " + ChatColor.RESET + SpawnStarUtilities.getStandard().getData().toString());
		sender.sendMessage(ChatColor.GREEN + "Minimum spawn distance: " + ChatColor.RESET + plugin.getConfig().getInt("minimum-distance"));
		sender.sendMessage(ChatColor.GREEN + "Warmup: " + ChatColor.RESET + plugin.getConfig().getInt("teleport-warmup") + " seconds");
		sender.sendMessage(ChatColor.GREEN + "Cooldown: " + ChatColor.RESET + plugin.getConfig().getInt("teleport-cooldown") + " seconds");
		sender.sendMessage(ChatColor.GREEN + "Shift-click required: " + ChatColor.RESET + plugin.getConfig().getBoolean("shift-click"));
		sender.sendMessage(ChatColor.GREEN + "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");
		sender.sendMessage(ChatColor.GREEN + "Remove from inventory: " + ChatColor.RESET + plugin.getConfig().getString("remove-from-inventory"));
		sender.sendMessage(ChatColor.GREEN + "Allow in recipes: " + ChatColor.RESET + plugin.getConfig().getBoolean("allow-in-recipes"));
		sender.sendMessage(ChatColor.GREEN + "From nether: " + ChatColor.RESET + plugin.getConfig().getBoolean("from-nether"));
		sender.sendMessage(ChatColor.GREEN + "From end: " + ChatColor.RESET + plugin.getConfig().getBoolean("from-end"));
		sender.sendMessage(ChatColor.GREEN + "Lightning: " + ChatColor.RESET + plugin.getConfig().getBoolean("lightning"));
		sender.sendMessage(ChatColor.GREEN + "Enabled Words: " + ChatColor.RESET + getEnabledWorlds().toString());
		return true;
	}

	
	boolean reloadCommand(final CommandSender sender, final String[] args) {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("spawnstar.reload")) {
			plugin.messageManager.sendPlayerMessage(sender, "permission-denied-reload");
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds field
		updateEnabledWorlds();

		// reload messages
		plugin.messageManager.reload();

		// set debug flag
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// refresh reference item in case changes were made
		SpawnStarUtilities.setStandard(SpawnStarUtilities.createItem(1));

		// send reloaded message to command sender
		plugin.messageManager.sendPlayerMessage(sender, "command-success-reload");
		return true;
	}

	
	/**
	 * update enabledWorlds ArrayList field from config file settings
	 */
	void updateEnabledWorlds() {
		
		// copy list of enabled worlds from config into enabledWorlds ArrayList field
		this.enabledWorlds = new ArrayList<String>(plugin.getConfig().getStringList("enabled-worlds"));
		
		// if enabledWorlds ArrayList is empty, add all worlds to ArrayList
		if (this.enabledWorlds.isEmpty()) {
			for (World world : plugin.getServer().getWorlds()) {
				enabledWorlds.add(world.getName());
			}
		}
		
		// remove each disabled world from enabled worlds field
		for (String disabledWorld : plugin.getConfig().getStringList("disabled-worlds")) {
			this.enabledWorlds.remove(disabledWorld);
		}
	}
	
	
	/**
	 * get list of enabled worlds
	 * @return ArrayList of String enabledWorlds
	 */
	ArrayList<String> getEnabledWorlds() {
		return this.enabledWorlds;
	}

}
