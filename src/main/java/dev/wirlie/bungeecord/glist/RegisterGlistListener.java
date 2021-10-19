package dev.wirlie.bungeecord.glist;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RegisterGlistListener implements Listener {

    @EventHandler
    public void event(PostLoginEvent e) {
        synchronized (EnhancedBCL.registerGlistCommandTaskSyncObject) {
            if (EnhancedBCL.registerGlistCommandTask != null) {
                EnhancedBCL.registerGlistCommandTask.cancel();
                EnhancedBCL.registerGlistCommandTask = null;

                BungeeCord.getInstance().getPluginManager().registerCommand(EnhancedBCL.INSTANCE, EnhancedBCL.commandExecutor);
                EnhancedBCL.INSTANCE.getLogger().info("Command /glist registered...");
            }
        }

        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

}
