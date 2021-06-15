package com.winterhaven_mc.spawnstar;

public enum ConfigKey {


    LANGUAGE("language"),
//    ENABLED_WORLDS("enabled-worlds"),
    DISABLED_WORLDS("disabled-worlds"),
    ITEM_MATERIAL("item-material"),
    MINIMUM_DISTANCE("minimum-distance"),
    TELEPORT_COOLDOWN("teleport-cooldown"),
    TELEPORT_WARMUP("teleport-warmup"),
    PARTICLE_EFFECTS("particle-effects"),
    SOUND_EFFECTS("sound-effects"),
    SHIFT_CLICK("shift-click"),
    REMOVE_FROM_INVENTORY("remove-from-inventory"),
    ALLOW_IN_RECIPES("allow-in-recipes"),
    CANCEL_ON_DAMAGE("cancel-on-damage"),
    CANCEL_ON_MOVEMENT("cancel-on-movement"),
    CANCEL_ON_INTERACTION("cancel-on-interaction"),
    MAX_GIVE_AMOUNT("max-give-amount"),
    FROM_NETHER("from-nether"),
    FROM_END("from-end"),
    LIGHTNING("lightning"),
    LOG_USE("log-use");


    private final String key;

    ConfigKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return this.key;
    }

    public String getKey() {
        return this.key;
    }
}

