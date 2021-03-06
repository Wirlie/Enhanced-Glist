package dev.wirlie.bungeecord.glist;

import de.myzelyam.api.vanish.BungeePlayerHideEvent;
import de.myzelyam.api.vanish.BungeePlayerShowEvent;
import de.myzelyam.api.vanish.BungeeVanishAPI;
import dev.wirlie.bungeecord.glist.activity.ActivityType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PremiumVanishListener implements Listener {

    private EnhancedBCL plugin;

    public PremiumVanishListener(EnhancedBCL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void event(BungeePlayerShowEvent e) {
        plugin.getActivityManager().removeActivity(e.getPlayer().getUniqueId(), ActivityType.VANISH);
    }

    @EventHandler
    public void event(BungeePlayerHideEvent e) {
        plugin.getActivityManager().addActivity(e.getPlayer().getUniqueId(), ActivityType.VANISH);
    }

    @EventHandler
    public void event(PostLoginEvent e) {
        if(BungeeVanishAPI.isInvisible(e.getPlayer())) {
            plugin.getActivityManager().addActivity(e.getPlayer().getUniqueId(), ActivityType.VANISH);
        }
    }

    public void initialHandle() {
        for(ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            if(BungeeVanishAPI.isInvisible(p)) {
                plugin.getActivityManager().addActivity(p.getUniqueId(), ActivityType.VANISH);
            }
        }
    }

}
