package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    private final FAWEditDetector plugin;
    private final SQLiteManager sqlite;
    private final LuckPerms luckPerms;

    public CommandListener(FAWEditDetector plugin, SQLiteManager sqlite) {
        this.plugin = plugin;
        this.sqlite = sqlite;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage().toLowerCase();

        if (!msg.startsWith("//")) return;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        String group = user.getPrimaryGroup();

        List<String> watched = plugin.getConfig().getStringList("watch-groups");
        if (!watched.contains(group)) return;

        String world = player.getWorld().getName();
        String command = event.getMessage();

        sqlite.insertLog(
                player.getName(),
                group,
                world,
                command
        );

        Bukkit.getLogger().warning(
                "[FAWEditDetector] FAWE: Игрок=" + player.getName() +
                        " | Группа=" + group +
                        " | Мир=" + world +
                        " | Команда=" + command
        );
    }
}
