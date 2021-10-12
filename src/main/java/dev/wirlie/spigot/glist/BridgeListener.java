package dev.wirlie.spigot.glist;

import dev.wirlie.spigot.glist.hooks.AbstractHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BridgeListener implements PluginMessageListener {

    private final EnhancedBCLBridge bridge;

    public BridgeListener(EnhancedBCLBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] data) {
        if(s.equals("ebcl:bridge")) {
            try {
                ByteArrayInputStream bin = new ByteArrayInputStream(data);
                DataInputStream in = new DataInputStream(bin);
                String action = in.readUTF();
                if(action.equals("send_all_players_to_bungeecord")) {
                    for(AbstractHook hook : bridge.getHooks()) {
                        hook.sendAllPlayersStateToBridge();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
