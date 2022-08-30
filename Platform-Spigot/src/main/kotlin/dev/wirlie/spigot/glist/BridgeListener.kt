/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022  Josue Acevedo
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
