package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    private SQLiteManager database;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        luckPerms = Bukkit.getServicesManager()
                .getRegistration(LuckPerms.class).getProvider();

        database = new SQLiteManager(this);
        database.connect();
        database.createTable();

        Bukkit.getPluginManager().registerEvents(
                new FaweListener(this, luckPerms), this
        );

        startCleanupTask();
        getLogger().info("FAWEditDetector запущен (SQLite)");
    }

    private void startCleanupTask() {
        if (!getConfig().getBoolean("cleanup.enabled")) return;

        int minutes = getConfig().getInt("cleanup.interval-minutes");
        int days = getConfig().getInt("cleanup.days");

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> database.cleanupOldLogs(days),
                20L,
                minutes * 60L * 20L
        );
    }

    public SQLiteManager getDatabase() {
        return database;
    }
}
