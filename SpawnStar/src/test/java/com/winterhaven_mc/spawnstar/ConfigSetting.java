package com.winterhaven_mc.spawnstar;


public enum ConfigSetting {

    LANGUAGE("en-US"),
    ENABLED_WORLDS("[]"),
    DISABLED_WORLDS("[disabled_world1, disabled_world2]"),
    ITEM_MATERIAL("NETHER_STAR"),
    MINIMUM_DISTANCE("10"),
    TELEPORT_COOLDOWN("60"),
    TELEPORT_WARMUP("5"),
    PARTICLE_EFFECTS("true"),
    SOUND_EFFECTS("true"),
    SHIFT_CLICK("true"),
    REMOVE_FROM_INVENTORY("on-success"),
    ALLOW_IN_RECIPES("false"),
    CANCEL_ON_DAMAGE("false"),
    CANCEL_ON_MOVEMENT("false"),
    CANCEL_ON_INTERACTION("false"),
    MAX_GIVE_AMOUNT("-1"),
    FROM_NETHER("true"),
    FROM_END("true"),
    LIGHTNING("false"),
    LOG_USE("true");


    private final String value;

    ConfigSetting(String value) {
        this.value = value;
    }

    public String getKey() {
        return this.name().toLowerCase().replace('_', '-');
    }
    public String getValue() {
        return this.value;
    }

}
