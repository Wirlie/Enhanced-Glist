package dev.wirlie.spigot.glist.hooks;

import com.earth2me.essentials.Essentials;
import de.myzelyam.api.vanish.VanishAPI;
import dev.wirlie.spigot.glist.EnhancedBCLBridge;
import net.ess3.api.IUser;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EssentialsHook implements AbstractHook, Listener {

    private final Essentials essentials;
    private final EnhancedBCLBridge bridge;

    public EssentialsHook(EnhancedBCLBridge bridge, Plugin essentialsPlugin) {
        this.bridge = bridge;
        this.essentials = (Essentials) essentialsPlugin;
    }

    @Override
    public void sendStateToBridge(Player player, StateNotificationType type,  boolean newValue) {
        if(type == StateNotificationType.AFK) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bout);
                out.writeUTF("update_afk_state");
                out.writeUTF("Essentials");
                out.writeUTF(player.getUniqueId().toString());
                out.writeBoolean(newValue);
                out.close();
                bout.close();
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(bout);
                out.writeUTF("update_vanish_state");
                out.writeUTF("Essentials");
                out.writeUTF(player.getUniqueId().toString());
                out.writeBoolean(newValue);
                out.close();
                bout.close();
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void sendAllPlayersStateToBridge() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            sendPlayerToBridge(player);
        }
    }

    public void sendPlayerToBridge(Player player) {
        IUser essentialsPlayer = essentials.getUser(player);
        sendStateToBridge(player, StateNotificationType.AFK, essentialsPlayer.isAfk());
        sendStateToBridge(player, StateNotificationType.VANISH, essentialsPlayer.isVanished());
    }

    @Override
    public void registerListeners(EnhancedBCLBridge bridge) {
        Bukkit.getPluginManager().registerEvents(this, bridge);
    }

    @EventHandler
    public void event(VanishStatusChangeEvent event) {
        sendStateToBridge(event.getAffected().getBase(), StateNotificationType.VANISH, event.getValue());
    }

    @EventHandler
    public void event(AfkStatusChangeEvent event) {
        sendStateToBridge(event.getAffected().getBase(), StateNotificationType.AFK, event.getValue());
    }

}
