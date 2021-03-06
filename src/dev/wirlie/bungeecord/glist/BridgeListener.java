package dev.wirlie.bungeecord.glist;

import dev.wirlie.bungeecord.glist.activity.ActivityType;
import dev.wirlie.bungeecord.glist.config.Config;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class BridgeListener implements Listener {

    private final EnhancedBCL plugin;

    public BridgeListener(EnhancedBCL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void event(PluginMessageEvent e) {
        if(!e.getTag().equals("ebcl:bridge")) {
            return;
        }

        if (!(e.getSender() instanceof ServerConnection)) {
            e.setCancelled(true);
            return;
        }

        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(e.getData());
            DataInputStream in = new DataInputStream(bin);

            String action = in.readUTF();

            if(action.equals("update_afk_state")) {
                String pluginName = in.readUTF();
                UUID playerID = UUID.fromString(in.readUTF());
                boolean newState = in.readBoolean();
                if (newState) {
                    plugin.getActivityManager().addActivity(playerID, ActivityType.AFK);
                } else {
                    plugin.getActivityManager().removeActivity(playerID, ActivityType.AFK);
                }
            } else if(action.equals("update_vanish_state")) {
                String pluginName = in.readUTF();

                if(!Config.BEHAVIOUR__PLAYER_STATUS__VANISH__VANISH_PLUGIN.get().equalsIgnoreCase(pluginName)) {
                    //ignore state changes from other plugins
                    return;
                }


                UUID playerID = UUID.fromString(in.readUTF());
                boolean newState = in.readBoolean();
                if (newState) {
                    plugin.getActivityManager().addActivity(playerID, ActivityType.VANISH);
                } else {
                    plugin.getActivityManager().removeActivity(playerID, ActivityType.VANISH);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void event(ServerSwitchEvent event) {
        plugin.getActivityManager().removeActivity(event.getPlayer().getUniqueId(), ActivityType.AFK);
    }

}
