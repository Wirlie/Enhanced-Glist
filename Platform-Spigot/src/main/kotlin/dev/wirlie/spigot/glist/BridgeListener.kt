package dev.wirlie.spigot.glist

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException

class BridgeListener(private val bridge: EnhancedGlistSpigot) : PluginMessageListener {
    override fun onPluginMessageReceived(s: String, player: Player, data: ByteArray) {
        if (s == "ebcl:bridge") {
            try {
                val bin = ByteArrayInputStream(data)
                val `in` = DataInputStream(bin)
                val action = `in`.readUTF()
                if (action == "send_all_players_to_bungeecord") {
                    for (hook in bridge.getHooks()) {
                        hook.sendAllPlayersStateToBridge()
                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }
}
