package me.flexcraft.faweeditdetector;

import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    private SQLiteManager sqlite;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Запуск FAWEditDetector...");

        sqlite = new SQLiteManager(this);
        sqlite.init();

        getServer().getPluginManager().registerEvents(
                new CommandListener(this, sqlite),
                this
        );

        getLogger().info("FAWEditDetector успешно запущен.");
    }

    @Override
    public void onDisable() {
        if (sqlite != null) {
            sqlite.close();
        }
        getLogger().info("FAWEditDetector выключен.");
    }
}
