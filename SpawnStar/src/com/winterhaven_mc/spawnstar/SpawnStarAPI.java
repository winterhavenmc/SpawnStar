package com.winterhaven_mc.spawnstar;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Public API to access SpawnStar attributes 
 * @author Tim Savage
 *
 */
public interface SpawnStarAPI {

	/**
	 * Get material type of SpawnStar
	 * @return Material
	 */
	public Material getItemMaterial();
	
	/**
	 * Get display name of SpawnStar with formatting intact
	 * @return String
	 */
	public String getItemName();
	
	/**
	 * Get lore of SpawnStar with formatting intact
	 * @return List of Strings
	 */
	public List<String> getItemLore();
	
	/**
	 * check if SpawnStar items are allowed in crafting recipes
	 * @return Boolean
	 */
	public Boolean isValidIngredient();
	
	/**
	 * SpawnStar teleport cooldown time in seconds
	 * @return int
	 */
	public int getCooldownTime();
	
	/**
	 * SpawnStar teleport warmup time in seconds
	 * @return int
	 */
	public int getWarmupTime();
	
	/**
	 * Minimum distance from spawn to use a SpawnStar item
	 * @return int
	 */
	public int getMinSpawnDistance();
	
	/**
	 * Is teleport cancellation on player damage during warmup configured
	 * @return Boolean
	 */
	public Boolean isCancelledOnDamage();

	/**
	 * Is teleport cancellation on player movement during warmup configured
	 * @return Boolean
	 */
	public Boolean isCancelledOnMovement();
	
	/**
	 * Is teleport cancellation on player interaction with blocks during warmup configured
	 * @return Boolean
	 */
	public Boolean isCancelledOnInteraction();

	/**
	 * Is player teleportation pending
	 * @param player
	 * @return Boolean
	 */
	public Boolean isWarmingUp(Player player);

	/**
	 * Is player teleport cooldown in effect for this player
	 * @param player
	 * @return Boolean
	 */
	public Boolean isCoolingDown(Player player);

	/**
	 * Time remaining for player cooldown in seconds for this player
	 * @param player
	 * @return long
	 */
	public long cooldownTimeRemaining(Player player);
	
	/**
	 * Get list of worlds in which the SpawnStar plugin is enabled
	 * @return List of Strings
	 */
	public List<String> getEnabledWorlds();
	
	/**
	 * Cancel pending teleport for this player
	 * @param player
	 */
	public void cancelTeleport(Player player);
	
}
