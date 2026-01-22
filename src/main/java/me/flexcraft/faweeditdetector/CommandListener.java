package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    private final FAWEditDetector plugin;
    private final LuckPerms luckPerms;

    public CommandListener(FAWEditDetector plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage().toLowerCase();

        if (!cmd.startsWith("//")) return;

        // FAWE команды
        if (!(cmd.startsWith("//set")
                || cmd.startsWith("//replace")
                || cmd.startsWith("//paste")
                || cmd.startsWith("//cut")
                || cmd.startsWith("//regen")
                || cmd.startsWith("//sphere")
                || cmd.startsWith("//cyl")
                || cmd.startsWith("//brush"))) {
            return;
        }

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        List<String> watchGroups = plugin.getConfig().getStringList("watch-groups");

        boolean allowed = user.getInheritedGroups(user.getQueryOptions())
                .stream()
                .anyMatch(g -> watchGroups.contains(g.getName()));

        if (!allowed) return;

        World world = player.getWorld();

        plugin.getLogger().warning(
                "[FAWE] " + player.getName()
                        + " использовал команду: " + cmd
                        + " | Мир: " + world.getName()
                        + " | X:" + player.getLocation().getBlockX()
                        + " Y:" + player.getLocation().getBlockY()
                        + " Z:" + player.getLocation().getBlockZ()
        );

        // сюда позже можно:
        // - SQLite лог
        // - Discord webhook
        // - авто-алерт
    }
}
