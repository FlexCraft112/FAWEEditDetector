package me.flexcraft.faweeditdetector;

import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("FAWEditDetector enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("FAWEditDetector disabled");
    }
}
