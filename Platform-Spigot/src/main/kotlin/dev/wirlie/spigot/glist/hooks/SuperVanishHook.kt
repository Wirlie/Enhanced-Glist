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

import de.myzelyam.api.vanish.PostPlayerHideEvent
import de.myzelyam.api.vanish.PostPlayerShowEvent
import de.myzelyam.api.vanish.VanishAPI
import dev.wirlie.spigot.glist.EnhancedGlistSpigot
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class SuperVanishHook(private val bridge: EnhancedGlistSpigot) : AbstractHook, Listener {
    override fun sendStateToBridge(player: Player, type: StateNotificationType, newValue: Boolean) {
        if (type == StateNotificationType.VANISH) {
            try {
                val bout = ByteArrayOutputStream()
                val out = DataOutputStream(bout)
                out.writeUTF("update_vanish_state")
                out.writeUTF("SuperVanish")
                out.writeUTF(player.uniqueId.toString())
                out.writeBoolean(newValue)
                out.close()
                bout.close()
                player.sendPluginMessage(bridge, "ebcl:bridge", bout.toByteArray())
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } else {
            throw IllegalArgumentException("This hook cannot handle an event of type $type, please report this bug to the developer.")
        }
    }

    override fun sendAllPlayersStateToBridge() {
        for (player in Bukkit.getOnlinePlayers()) {
            sendPlayerToBridge(player)
        }
    }

    override fun sendPlayerToBridge(player: Player) {
        sendStateToBridge(player, StateNotificationType.VANISH, VanishAPI.isInvisible(player))
    }

    override fun registerListeners(bridge: EnhancedGlistSpigot) {
        Bukkit.getPluginManager().registerEvents(this, bridge)
    }

    @EventHandler
    fun event(event: PostPlayerShowEvent) {
        sendStateToBridge(event.player, StateNotificationType.VANISH, false)
    }

    @EventHandler
    fun event(event: PostPlayerHideEvent) {
        sendStateToBridge(event.player, StateNotificationType.VANISH, true)
    }
}
