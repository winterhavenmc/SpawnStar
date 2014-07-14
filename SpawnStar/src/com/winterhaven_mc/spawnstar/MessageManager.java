package com.winterhaven_mc.spawnstar;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Implements message manager for <code>SpawnStar</code>.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class MessageManager {
	
    private final SpawnStarMain plugin;	// reference to main class
    private ConfigAccessor messages;

    
    /**
     * Constructor method for class
     * @param plugin
     */
    public MessageManager(SpawnStarMain plugin) {
		
		// create pointer to main class
		this.plugin = plugin;

		// install localization files
		String[] localization_files = {"en-US", "es-ES", "de-DE"};
        this.installLocalizationFiles(localization_files);
		
		// get configured language
		String language = plugin.getConfig().getString("language","en-US");

		// check if localization file for configured language exists, if not then fallback to en-US
		if (!new File(plugin.getDataFolder() + "/language/" + language + ".yml").exists()) {
            plugin.getLogger().info("Language file for " + language + " not found. Defaulting to en-US.");
            language = "en-US";
        }
		
		// instantiate custom configuration manager
		messages = new ConfigAccessor(plugin, "language/" + language + ".yml");

    }

	/** Send message to player
	 * 
	 * @param player		Player to message
	 * @param messageID		Identifier of message to send form messages.yml
	 */
	public void sendPlayerMessage(Player player, String messageID) {

		if (messages.getConfig().getBoolean("messages." + messageID + ".enabled",false)) {
        
			// get message from file
			String message = messages.getConfig().getString("messages." + messageID + ".string");
	
			// get variable values and strip color codes
	        String itemname = messages.getConfig().getString("itemname", "Spawn Star").replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
	        String playername = player.getName().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
	        String playernickname = player.getPlayerListName().replaceAll("&[0-9A-Za-zK-Ok-oRr]", "");
	        String playerdisplayname = player.getDisplayName();
	        String worldname = player.getWorld().getName();
	        Long remainingtime = plugin.cooldown.getTimeRemaining(player);
	        
			// do variable substitutions
	        message = message.replaceAll("%itemname%", itemname);
	        message = message.replaceAll("%playername%", playername);
	        message = message.replaceAll("%playerdisplayname%", playerdisplayname);
	        message = message.replaceAll("%playernickname%", playernickname);
	        message = message.replaceAll("%worldname%", worldname);
	        message = message.replaceAll("%timeremaining%", remainingtime.toString());
	        
			// send message to player
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
		}
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
		

    public void reloadMessages() {
        messages.reloadConfig();
    }
    
    public String getItemName() {
    	String itemname = messages.getConfig().getString("itemname","SpawnStar");
    	return itemname;
    }
    
    public List<String> getItemLore() {
    	List<String> itemlore = messages.getConfig().getStringList("itemlore");
    	return itemlore;
    }
    
}

