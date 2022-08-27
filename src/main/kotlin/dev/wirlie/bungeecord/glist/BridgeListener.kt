package dev.wirlie.bungeecord.glist

import dev.wirlie.bungeecord.glist.activity.ActivityType
import dev.wirlie.bungeecord.glist.config.Config
import net.md_5.bungee.ServerConnection
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.util.*

class BridgeListener(private val plugin: EnhancedBCL) : Listener {

    @EventHandler
    fun event(e: PluginMessageEvent) {
        if (e.tag != "ebcl:bridge") {
            return
        }

        if (e.sender !is ServerConnection) {
            e.isCancelled = true
            return
        }

        try {
            val bin = ByteArrayInputStream(e.data)
            val inputStream = DataInputStream(bin)
            val action = inputStream.readUTF()

            if (action == "update_afk_state") {
                val pluginName = inputStream.readUTF()
                val playerID = UUID.fromString(inputStream.readUTF())
                val newState = inputStream.readBoolean()

                if (newState) {
                    plugin.activityManager.addActivity(playerID, ActivityType.AFK)
                } else {
                    plugin.activityManager.removeActivity(playerID, ActivityType.AFK)
                }
            } else if (action == "update_vanish_state") {
                val pluginName = inputStream.readUTF()

                if (!Config.BEHAVIOUR__PLAYER_STATUS__VANISH__VANISH_PLUGIN.get()
                        .equals(pluginName, ignoreCase = true)
                ) {
                    //ignore state changes from other plugins
                    return
                }

                if (pluginName.equals("PremiumVanish", ignoreCase = true) || pluginName.equals(
                        "SuperVanish",
                        ignoreCase = true
                    )
                ) {
                    if (plugin.isPremiumVanishHooked) {
                        return
                    }
                }

                val playerID = UUID.fromString(inputStream.readUTF())
                val newState = inputStream.readBoolean()

                if (newState) {
                    plugin.activityManager.addActivity(playerID, ActivityType.VANISH)
                } else {
                    plugin.activityManager.removeActivity(playerID, ActivityType.VANISH)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @EventHandler
    fun event(event: ServerSwitchEvent) {
        plugin.activityManager.removeActivity(event.player.uniqueId, ActivityType.AFK)
    }

}
