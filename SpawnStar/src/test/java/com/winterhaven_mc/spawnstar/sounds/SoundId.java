package com.winterhaven_mc.spawnstar.sounds;

/**
 * Enum that contains entries for configured sounds
 */
public enum SoundId {

	COMMAND_FAIL("ENTITY_VILLAGER_NO", true, true, 1, 1),
	COMMAND_INVALID("ENTITY_WANDERING_TRADER_AMBIENT", true, true, 1, 1),
	COMMAND_SUCCESS_GIVE_SENDER("ENTITY_ARROW_SHOOT", true, true, 0.5, 1),
	COMMAND_SUCCESS_DESTROY("ENTITY_ITEM_PICKUP", true, true, 1, 1),
	COMMAND_SUCCESS_GIVE_TARGET("ENTITY_ITEM_PICKUP", true, true, 1, 1),
	TELEPORT_CANCELLED("ENTITY_GENERIC_EXTINGUISH_FIRE", true, true, 1, 1),
	TELEPORT_DENIED_PERMISSION("BLOCK_NOTE_BLOCK_BASS", true, true, 1, 1),
	TELEPORT_DENIED_WORLD_DISABLED("BLOCK_NOTE_BLOCK_BASS", true, true, 1, 1),
	TELEPORT_CANCELLED_NO_ITEM("ENTITY_DONKEY_ANGRY", true, true, 1, 1),
	TELEPORT_WARMUP("BLOCK_PORTAL_TRAVEL", true, true, 0.3, 1),
	TELEPORT_SUCCESS_DEPARTURE("TELEPORT_SUCCESS_DEPARTURE", true, true, 1, 1),
	TELEPORT_SUCCESS_ARRIVAL("ENTITY_ENDERMAN_TELEPORT", true, true, 1, 1),
    ;

	protected boolean enabled;
	protected boolean playerOnly;
	protected String bukkitSoundName;
	protected double volume;
	protected double pitch;

	SoundId(String bukkitSoundName, boolean enabled, boolean playerOnly, double volume, double pitch) {
		this.bukkitSoundName = bukkitSoundName;
		this.enabled = enabled;
		this.playerOnly = playerOnly;
		this.volume = volume;
		this.pitch = pitch;
	}

	public String getBukkitSoundName() {
		return bukkitSoundName;
	}

}
