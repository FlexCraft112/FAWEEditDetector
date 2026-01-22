package me.flexcraft.faweeditdetector;

import com.fastasyncworldedit.core.event.extent.EditSessionEvent;
import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

public class FaweListener implements Listener {

    private final FAWEditDetector plugin;
    private final LuckPerms luckPerms;

    public FaweListener(FAWEditDetector plugin) {
        this.plugin = plugin;
        this.luckPerms = Bukkit.getServicesManager()
                .getRegistration(LuckPerms.class)
                .getProvider();
    }

    @EventHandler
    public void onFaweEdit(EditSessionEvent event) {

        // Берём только финальный этап
        if (event.getStage() != EditSessionEvent.Stage.AFTER_CHANGE) return;

        EditSession session = event.getEditSession();
        if (session == null) return;

        if (event.getActor() == null) return;
        UUID uuid = event.getActor().getUniqueId();

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        User lpUser = luckPerms.getUserManager().getUser(uuid);
        if (lpUser == null) return;

        String group = lpUser.getPrimaryGroup();
        List<String> watchedGroups = plugin.getConfig().getStringList("watch-groups");

        // Если группа не отслеживается — игнор
        if (!watchedGroups.contains(group)) return;

        long blocks = session.getBlockChangeCount();
        int minBlocks = plugin.getConfig().getInt("alert.min-blocks");

        if (blocks < minBlocks) return;

        BlockVector3 min = session.getMinimumPoint();
        BlockVector3 max = session.getMaximumPoint();
        World weWorld = session.getWorld();
        String worldName = weWorld != null ? weWorld.getName() : "unknown";

        // -------- ЛОГ В ФАЙЛ --------
        String logLine =
                "Игрок: " + player.getName() +
                " | Группа: " + group +
                " | Мир: " + worldName +
                " | Блоков: " + blocks +
                " | X: " + min.getX() + " → " + max.getX() +
                " | Y: " + min.getY() + " → " + max.getY() +
                " | Z: " + min.getZ() + " → " + max.getZ();

        LogWriter.write(plugin.getDataFolder(), logLine);

        // -------- ЗАПИСЬ В MYSQL (АСИНХРОННО) --------
        TaskManager.async(() -> {
            try {
                PreparedStatement ps = plugin.getMySQL().getConnection()
                        .prepareStatement(
                                "INSERT INTO fawe_edits " +
                                        "(player, uuid, group_name, world, blocks, min_x, min_y, min_z, max_x, max_y, max_z) " +
                                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        );

                ps.setString(1, player.getName());
                ps.setString(2, uuid.toString());
                ps.setString(3, group);
                ps.setString(4, worldName);
                ps.setLong(5, blocks);

                ps.setInt(6, min.getX());
                ps.setInt(7, min.getY());
                ps.setInt(8, min.getZ());
                ps.setInt(9, max.getX());
                ps.setInt(10, max.getY());
                ps.setInt(11, max.getZ());

                ps.executeUpdate();
                ps.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
