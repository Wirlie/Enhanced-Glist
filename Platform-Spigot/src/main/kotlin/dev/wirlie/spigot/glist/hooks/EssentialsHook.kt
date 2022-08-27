package dev.wirlie.spigot.glist.hooks

import com.earth2me.essentials.Essentials
import dev.wirlie.spigot.glist.EnhancedBCLBridge
import net.ess3.api.IUser
import net.ess3.api.events.AfkStatusChangeEvent
import net.ess3.api.events.VanishStatusChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class EssentialsHook(private val bridge: EnhancedBCLBridge, essentialsPlugin: Plugin) : AbstractHook, Listener {
    private val essentials: Essentials

    init {
        essentials = essentialsPlugin as Essentials
    }

    override fun sendStateToBridge(player: Player, type: StateNotificationType, newValue: Boolean) {
        if (type == StateNotificationType.AFK) {
            try {
                val bout = ByteArrayOutputStream()
                val out = DataOutputStream(bout)
                out.writeUTF("update_afk_state")
                out.writeUTF("Essentials")
                out.writeUTF(player.uniqueId.toString())
                out.writeBoolean(newValue)
                out.close()
                bout.close()
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } else {
            try {
                val bout = ByteArrayOutputStream()
                val out = DataOutputStream(bout)
                out.writeUTF("update_vanish_state")
                out.writeUTF("Essentials")
                out.writeUTF(player.uniqueId.toString())
                out.writeBoolean(newValue)
                out.close()
                bout.close()
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    override fun sendAllPlayersStateToBridge() {
        for (player in Bukkit.getOnlinePlayers()) {
            sendPlayerToBridge(player)
        }
    }

    override fun sendPlayerToBridge(player: Player) {
        val essentialsPlayer: IUser = essentials.getUser(player)
        sendStateToBridge(player, StateNotificationType.AFK, essentialsPlayer.isAfk)
        sendStateToBridge(player, StateNotificationType.VANISH, essentialsPlayer.isVanished)
    }

    override fun registerListeners(bridge: EnhancedBCLBridge) {
        Bukkit.getPluginManager().registerEvents(this, bridge)
    }

    @EventHandler
    fun event(event: VanishStatusChangeEvent) {
        sendStateToBridge(event.affected.base, StateNotificationType.VANISH, event.value)
    }

    @EventHandler
    fun event(event: AfkStatusChangeEvent) {
        sendStateToBridge(event.affected.base, StateNotificationType.AFK, event.value)
    }
}
