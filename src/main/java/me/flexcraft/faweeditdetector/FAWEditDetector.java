package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class FAWEditDetector extends JavaPlugin {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        getLogger().info("Запуск FAWEditDetector...");

        // config.yml
        saveDefaultConfig();

        // LuckPerms
        RegisteredServiceProvider<LuckPerms> provider =
                Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider == null) {
            getLogger().severe("LuckPerms НЕ НАЙДЕН! Плагин отключён.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        luckPerms = provider.getProvider();

        // Listener
        Bukkit.getPluginManager().registerEvents(
                new CommandListener(this, luckPerms),
                this
        );

        getLogger().info("FAWEditDetector успешно запущен.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FAWEditDetector выключен.");
    }
}
