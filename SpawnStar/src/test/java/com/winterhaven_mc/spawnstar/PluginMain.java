package com.winterhaven_mc.spawnstar;

import com.winterhaven_mc.spawnstar.commands.CommandManager;
import com.winterhaven_mc.spawnstar.listeners.PlayerEventListener;
import com.winterhaven_mc.spawnstar.teleport.TeleportManager;
import com.winterhaven_mc.util.LanguageManager;
import com.winterhaven_mc.util.SoundConfiguration;
import com.winterhaven_mc.util.WorldManager;
import com.winterhaven_mc.util.YamlSoundConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public final class PluginMain extends JavaPlugin {

    protected LanguageManager languageManager;
    protected SoundConfiguration soundConfig;
    protected TeleportManager teleportManager;
    protected WorldManager worldManager;
    protected CommandManager commandManager;
    protected PlayerEventListener playerEventListener;


    /**
     *
     */
    @SuppressWarnings("unused")
    public PluginMain() {
        super();
    }


    @SuppressWarnings("unused")
    protected PluginMain(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
    }


    @Override
    public void onEnable() {

        // install default configuration file if not already present
        saveDefaultConfig();

        // initialize language manager
        languageManager = new LanguageManager(this);

        // instantiate sound configuration
        soundConfig = new YamlSoundConfiguration(this);

        // instantiate teleport manager
        teleportManager = new TeleportManager(this);

        // instantiate world manager
        worldManager = new WorldManager(this);

        // instantiate command manager
        commandManager = new CommandManager(this);

        // instantiate player event listener
        playerEventListener = new PlayerEventListener(this);
    }

}
