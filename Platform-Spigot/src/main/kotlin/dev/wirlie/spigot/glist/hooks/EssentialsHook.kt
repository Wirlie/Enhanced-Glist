/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022 Josue Acevedo and the Enhanced Glist contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: wirlie.dev@gmail.com
 */

package dev.wirlie.spigot.glist.hooks

import com.earth2me.essentials.Essentials
import dev.wirlie.spigot.glist.EnhancedGlistSpigot
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

class EssentialsHook(private val bridge: EnhancedGlistSpigot, essentialsPlugin: Plugin) : AbstractHook, Listener {
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

    override fun registerListeners(bridge: EnhancedGlistSpigot) {
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
