package dev.wirlie.spigot.glist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener  {

    private EnhancedBCLBridge bridge;

    public JoinListener(EnhancedBCLBridge bridge) {
        this.bridge = bridge;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                bridge.getHooks().forEach(h -> h.sendPlayerToBridge(e.getPlayer()));
            }
        }.runTask(bridge);
    }

}
