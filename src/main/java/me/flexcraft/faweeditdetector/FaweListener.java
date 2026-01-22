package me.flexcraft.faweeditdetector;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;

public class FaweListener implements Listener {

    private final FAWEditDetector plugin;

    public FaweListener(FAWEditDetector plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();

        if (!cmd.startsWith("//") && !cmd.startsWith("/fawe")) return;

        // проверка группы
        String group = getGroup(player);
        if (group == null) return;

        long blocks = 100000; // временно, дальше привяжем к FAWE API

        if (blocks < plugin.getConfig().getInt("alert.min-blocks")) return;

        plugin.getMySQLManager().insertLog(
                player.getName(),
                group,
                player.getWorld().getName(),
                blocks,
                player.getLocation().getBlockX(),
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ(),
                player.getLocation().getBlockX(),
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ()
        );
    }

    private String getGroup(Player player) {
        for (String group : plugin.getConfig().getStringList("watch-groups")) {
            if (player.hasPermission("group." + group)) {
                return group;
            }
        }
        return null;
    }
}
