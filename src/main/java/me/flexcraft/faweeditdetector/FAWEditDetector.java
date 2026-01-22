package me.flexcraft.faweeditdetector;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FAWEditDetector extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // создаём папку logs
        File logsDir = new File(getDataFolder(), "logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }

        getLogger().info("FAWEditDetector enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("FAWEditDetector disabled");
    }
}
