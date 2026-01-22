package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class FaweListener implements Listener {

    private final FAWEditDetector plugin;
    private final LuckPerms luckPerms;

    // группы, которые мы логируем
    private final List<String> watchedGroups = List.of(
            "ag",
            "vlastilin",
            "owner"
    );

    // команды FAWE
    private final List<String> faweCommands = List.of(
            "//set",
            "//replace",
            "//paste",
            "//regen",
            "//sphere",
            "//cyl",
            "//hcyl",
            "//brush",
            "//cut",
            "//copy"
    );

    public FaweListener(FAWEditDetector plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onFaweCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();

        boolean isFawe = faweCommands.stream().anyMatch(message::startsWith);
        if (!isFawe) return;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        String group = user.getPrimaryGroup();
        if (!watchedGroups.contains(group)) return;

        Location loc = player.getLocation();

        plugin.getMySQLManager().insertLog(
                player.getName(),
                group,
                message,
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );

        Bukkit.getLogger().info("[FAWEditDetector] ЛОГ: "
                + player.getName()
                + " | группа=" + group
                + " | команда=" + message
                + " | мир=" + loc.getWorld().getName()
                + " | x=" + loc.getBlockX()
                + " y=" + loc.getBlockY()
                + " z=" + loc.getBlockZ()
        );
    }
}
