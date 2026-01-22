package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    private static FAWEditDetector instance;

    private LuckPerms luckPerms;
    private MySQLManager mySQLManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // LuckPerms
        if (!setupLuckPerms()) {
            getLogger().severe("LuckPerms не найден! Плагин отключён.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // MySQL
        mySQLManager = new MySQLManager(this);
        mySQLManager.connect();
        mySQLManager.createTable();

        // Listener
        Bukkit.getPluginManager().registerEvents(
                new FaweListener(this, luckPerms),
                this
        );

        getLogger().info("FAWEditDetector успешно запущен.");
    }

    @Override
    public void onDisable() {
        if (mySQLManager != null) {
            mySQLManager.close();
        }
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider =
                Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider == null) return false;

        luckPerms = provider.getProvider();
        return true;
    }

    public static FAWEditDetector getInstance() {
        return instance;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }
}
