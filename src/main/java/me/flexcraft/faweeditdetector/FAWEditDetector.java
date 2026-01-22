package me.flexcraft.faweeditdetector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    private MySQLManager mySQLManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("Запуск FAWEditDetector...");

        mySQLManager = new MySQLManager(this);

        if (!mySQLManager.connect()) {
            getLogger().severe("MySQL НЕ ПОДКЛЮЧЕНА! Плагин будет отключён.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(
                new FaweListener(this),
                this
        );

        getLogger().info("FAWEditDetector успешно запущен.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FAWEditDetector выключен.");
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }
}
