package me.flexcraft.faweeditdetector;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FAWEditDetector extends JavaPlugin {

    private static FAWEditDetector instance;
    private MySQLManager mySQL;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        File logsDir = new File(getDataFolder(), "logs");
        if (!logsDir.exists()) logsDir.mkdirs();

        mySQL = new MySQLManager(this);
        if (!mySQL.connect()) {
            getLogger().severe("Плагин отключён: MySQL не подключена");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(
                new FaweListener(this), this
        );

        getLogger().info("FAWEditDetector запущен и готов к работе");
    }

    public static FAWEditDetector get() {
        return instance;
    }

    public MySQLManager getMySQL() {
        return mySQL;
    }
}
