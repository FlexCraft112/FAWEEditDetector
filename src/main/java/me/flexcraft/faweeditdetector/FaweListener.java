package me.flexcraft.faweeditdetector;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.fastasyncworldedit.core.event.extent.EditSessionEvent;

public class FaweListener implements Listener {

    private final FAWEditDetector plugin;
    private final LuckPerms lp;

    public FaweListener(FAWEditDetector plugin, LuckPerms lp) {
        this.plugin = plugin;
        this.lp = lp;
    }

    @EventHandler
    public void onFawe(EditSessionEvent e) {
        if (!(e.getActor() instanceof com.fastasyncworldedit.core.actor.PlayerActor actor)) return;

        String name = actor.getName();
        User user = lp.getUserManager().getUser(actor.getUniqueId());
        if (user == null) return;

        String group = user.getPrimaryGroup();
        if (!plugin.getConfig().getStringList("watch-groups").contains(group)) return;

        int blocks = e.getExtent().getBlockCount();
        if (blocks < plugin.getConfig().getInt("alert.min-blocks")) return;

        plugin.getDatabase().insertLog(
                name,
                actor.getUniqueId().toString(),
                group,
                actor.getWorld().getName(),
                blocks,
                actor.getLocation().getBlockX(),
                actor.getLocation().getBlockY(),
                actor.getLocation().getBlockZ()
        );

        plugin.getLogger().warning(
                "[FAWE] " + name + " (" + group + ") изменил " + blocks +
                        " блоков в мире " + actor.getWorld().getName()
        );
    }
}
