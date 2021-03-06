package dev.wirlie.spigot.glist.hooks;

import de.myzelyam.api.vanish.PostPlayerHideEvent;
import de.myzelyam.api.vanish.PostPlayerShowEvent;
import de.myzelyam.api.vanish.VanishAPI;
import dev.wirlie.spigot.glist.EnhancedBCLBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SuperVanishHook implements AbstractHook, Listener {

    private final EnhancedBCLBridge bridge;

    public SuperVanishHook(EnhancedBCLBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void sendStateToBridge(Player player, StateNotificationType type, boolean newValue) {
        if(type == StateNotificationType.VANISH) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bout);
                out.writeUTF("update_vanish_state");
                out.writeUTF("SuperVanish");
                out.writeUTF(player.getUniqueId().toString());
                out.writeBoolean(newValue);
                out.close();
                bout.close();
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("This hook cannot handle an event of type " + type + ", please report this bug to the developer.");
        }
    }

    @Override
    public void sendAllPlayersStateToBridge() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            sendPlayerToBridge(player);
        }
    }

    public void sendPlayerToBridge(Player player) {
        sendStateToBridge(player, StateNotificationType.VANISH, VanishAPI.isInvisible(player));
    }

    @Override
    public void registerListeners(EnhancedBCLBridge bridge) {
        Bukkit.getPluginManager().registerEvents(this, bridge);
    }

    @EventHandler
    public void event(PostPlayerShowEvent event) {
        sendStateToBridge(event.getPlayer(), StateNotificationType.VANISH, false);
    }

    @EventHandler
    public void event(PostPlayerHideEvent event) {
        sendStateToBridge(event.getPlayer(), StateNotificationType.VANISH, true);
    }

}
