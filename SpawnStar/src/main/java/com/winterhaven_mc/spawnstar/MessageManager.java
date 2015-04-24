package com.winterhaven_mc.spawnstar;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Implements message manager for <code>SpawnStar</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
class MessageManager {

	private final SpawnStarMain plugin; // reference to main class
	private ConfigAccessor messages;
	private ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> messageCooldownMap;

	/**
	 * Constructor method for class
	 * 
	 * @param plugin
	 */
	MessageManager(SpawnStarMain plugin) {
		
		// create pointer to main class
		this.plugin = plugin;

		// install localization files
		String[] localization_files = {"en-US", "es-ES", "de-DE"};
        this.installLocalizationFiles(localization_files);
		
		// get configured language
		String language = plugin.getConfig().getString("language");

		// check if localization file for configured language exists, if not then fallback to en-US
		if (!new File(plugin.getDataFolder() + "/language/" + language + ".yml").exists()) {
            plugin.getLogger().info("Language file for " + language + " not found. Defaulting to en-US.");
            language = "en-US";
        }
		
		// instantiate custom configuration manager
		messages = new ConfigAccessor(plugin, "language/" + language + ".yml");
		
		// initalize messageCooldownMap
		messageCooldownMap = new ConcurrentHashMap<UUID,ConcurrentHashMap<String,Long>>();

    }


	/** Send message to player
	 * 
	 * @param player		Player to message
	 * @param messageID		Identifier of message to send from messages.yml
	 */
    void sendPlayerMessage(CommandSender sender, String messageID) {
		this.sendPlayerMessage(sender, messageID, 1);
	}

    
	/** Send message to player
	 * 
	 * @param player		Player to message
	 * @param messageID		Identifier of message to send from messages.yml
	 * @param parameter1	Additional data
	 */
	void sendPlayerMessage(CommandSender sender, String messageID, Integer quantity) {
		
		// if message is set to enabled in messages file
		if (messages.getConfig().getBoolean("messages." + messageID + ".enabled")) {

			// set substitution variables defaults in case sender is not a player
			String playerName = "console";
			String playerNickname = "console";
			String playerDisplayName = "console";
			String worldName = "unknown world";
			Long remainingTime = 0L;

			// if sender is a player...
			if (sender instanceof Player) {
				
				Player player = (Player) sender;
				Long lastDisplayed = 0L;
				
				// check if player is in message cooldown hashmap
				if (messageCooldownMap.containsKey(player.getUniqueId())) {
					
					// check if messageID is in player's cooldown hashmap
					if (messageCooldownMap.get(player.getUniqueId()).containsKey(messageID)) {
						lastDisplayed = messageCooldownMap.get(player.getUniqueId()).get(messageID);
					}
				}
				
				// if message has repeat delay value and was displayed to player more recently, do nothing and return
				int messageRepeatDelay = messages.getConfig().getInt("messages." + messageID + ".repeat-delay");
				if (lastDisplayed > System.currentTimeMillis() - messageRepeatDelay * 1000) {
					return;
				}
		        
				// if repeat delay value is greater than zero, add entry to messageCooldownMap
		        if (messageRepeatDelay > 0) {
		        	ConcurrentHashMap<String, Long> tempMap = new ConcurrentHashMap<String, Long>();
		        	tempMap.put(messageID, System.currentTimeMillis());
		        	messageCooldownMap.put(player.getUniqueId(), tempMap);
		        }
				
				// assign player dependent variables
	        	playerName = player.getName().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
	        	playerNickname = player.getPlayerListName().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
	        	playerDisplayName = player.getDisplayName();
	        	worldName = player.getWorld().getName();
		        remainingTime = plugin.cooldownManager.getTimeRemaining(player);
			}
			
			// get message from file
			String message = messages.getConfig().getString("messages." + messageID + ".string");
	
			// get item name and strip color codes
	        String itemName = getItemName().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");

	        // get warmup value from config file
	        Integer warmupTime = plugin.getConfig().getInt("teleport-warmup");
	        
			String overworldname = worldName.replaceFirst("(_nether|_the_end)$", "");
			
			// if from-nether is enabled in config and player is in nether, get overworld name
			if (plugin.getConfig().getBoolean("from-nether") &&
					worldName.endsWith("_nether") &&
					plugin.getServer().getWorld(overworldname) != null) {
				worldName = overworldname;
			}
			
			// if from-end is enabled in config, and player is in end, get overworld name 
			if (plugin.getConfig().getBoolean("from-end") &&
					worldName.endsWith("_the_end") &&
					plugin.getServer().getWorld(overworldname) != null) {
				worldName = overworldname;
			}
			
			// if Multiverse is installed, use Multiverse world alias for world name
			if (plugin.mvEnabled && plugin.mvCore.getMVWorldManager().getMVWorld(worldName) != null) {
				
				// if Multiverse alias is not blank, set world name to alias
				if (!plugin.mvCore.getMVWorldManager().getMVWorld(worldName).getAlias().isEmpty()) {
					worldName = plugin.mvCore.getMVWorldManager().getMVWorld(worldName).getAlias();
				}
			}
	        
			// if quantity is greater than one, use plural item name
			if (quantity > 1) {
				// get plural item name and strip color codes
				itemName = getItemNamePlural().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
			}
			
			// do variable substitutions
	        message = message.replaceAll("%itemname%", itemName);
	        message = message.replaceAll("%playername%", playerName);
	        message = message.replaceAll("%playerdisplayname%", playerDisplayName);
	        message = message.replaceAll("%playernickname%", playerNickname);
	        message = message.replaceAll("%worldname%", worldName);
	        message = message.replaceAll("%timeremaining%", remainingTime.toString());
	        message = message.replaceAll("%warmuptime%", warmupTime.toString());
	        message = message.replaceAll("%quantity%", quantity.toString());
	        
			// send message to player
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
		}
    }
	
	
	/**
	 * Remove player from message cooldown map
	 * @param player
	 */
	void removePlayerCooldown(Player player) {
		messageCooldownMap.remove(player.getUniqueId());
	}

	
	/** Install list of embedded localization files
	 * 
	 * @param filelist	String list of embedded localizations files to install
	 */
	private void installLocalizationFiles(String[] filelist) {

		for (String filename : filelist) {
			if (!new File(plugin.getDataFolder() + "/language/" + filename + ".yml").exists()) {
				this.plugin.saveResource("language/" + filename + ".yml",false);
				plugin.getLogger().info("Installed localization files for " + filename + ".");
			}
		}
	}
		

    void reloadMessages() {
        messages.reloadConfig();
    }
    
    String getItemName() {
    	String itemName = messages.getConfig().getString("item-name");
    	return itemName;
    }
    
    String getItemNamePlural() {
    	String itemNamePlural = messages.getConfig().getString("item-name-plural");
    	return itemNamePlural;
    }
    
    List<String> getItemLore() {
    	List<String> itemLore = messages.getConfig().getStringList("item-lore");
    	return itemLore;
    }
    
}

