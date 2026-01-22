package me.flexcraft.faweeditdetector;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
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
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String cmd = e.getMessage().toLowerCase();

        if (!cmd.startsWith("//")) return;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        List<String> watchGroups = plugin.getConfig().getStringList("watch-groups");
        String group = user.getPrimaryGroup();

        if (!watchGroups.contains(group)) return;

        long volume = getSelectionVolume(player);

        plugin.getLogger().warning(
                "[FAWE] " +
                "Игрок=" + player.getName() +
                " | Группа=" + group +
                " | Мир=" + player.getWorld().getName() +
                " | Команда=" + e.getMessage() +
                " | Объём≈" + volume
        );
    }

    private long getSelectionVolume(Player player) {
        try {
            BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
            var session = WorldEdit.getInstance()
                    .getSessionManager()
                    .get(wePlayer);

            BlockVector3 pos1 = session.getSelectionWorld() == null
                    ? null
                    : session.getSelection(session.getSelectionWorld()).getMinimumPoint();

            BlockVector3 pos2 = session.getSelectionWorld() == null
                    ? null
                    : session.getSelection(session.getSelectionWorld()).getMaximumPoint();

            if (pos1 == null || pos2 == null) return -1;

            long x = Math.abs(pos1.getX() - pos2.getX()) + 1;
            long y = Math.abs(pos1.getY() - pos2.getY()) + 1;
            long z = Math.abs(pos1.getZ() - pos2.getZ()) + 1;

            return x * y * z;

        } catch (Exception ex) {
            return -1;
        }
    }
}
