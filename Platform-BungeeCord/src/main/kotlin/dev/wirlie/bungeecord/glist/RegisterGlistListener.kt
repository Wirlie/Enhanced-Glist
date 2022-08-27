package dev.wirlie.bungeecord.glist

import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class RegisterGlistListener : Listener {

    @EventHandler
    fun event(e: PostLoginEvent) {
        if (EnhancedBCL.registerGlistCommandTask != null) {
            EnhancedBCL.registerGlistCommandTask?.cancel()
            EnhancedBCL.registerGlistCommandTask = null
            BungeeCord.getInstance().getPluginManager().registerCommand(EnhancedBCL.INSTANCE, EnhancedBCL.commandExecutor)
            EnhancedBCL.INSTANCE.logger.info("Command /glist registered...")
        }
        ProxyServer.getInstance().pluginManager.unregisterListener(this)
    }

}
