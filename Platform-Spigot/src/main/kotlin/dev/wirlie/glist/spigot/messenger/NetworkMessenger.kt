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

package dev.wirlie.glist.spigot.messenger

import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class NetworkMessenger(
    val plugin: EnhancedGlistSpigot
) {

    private val gsonInstance = GsonBuilder().create()

    fun sendAfkStateToProxy(player: Player, state: Boolean) {
        sendToProxy(
            player,
            "enhanced-glist:general",
            "afk-state-update",
            gsonInstance.toJson(JsonPrimitive(state))
        )
    }

    fun sendVanishStateToProxy(player: Player, state: Boolean) {
        sendToProxy(
            player,
            "enhanced-glist:general",
            "vanish-state-update",
            gsonInstance.toJson(JsonPrimitive(state))
        )
    }

    fun sendToProxy(
        sender: Player,
        channel: String,
        subject: String,
        dataObject: String
    ) {
        val dataByteOut = ByteArrayOutputStream()
        val dataOut = DataOutputStream(dataByteOut)

        dataOut.writeUTF(subject)
        dataOut.writeUTF(dataObject)

        dataOut.close()
        dataByteOut.close()

        sender.sendPluginMessage(
            plugin,
            channel,
            dataByteOut.toByteArray()
        )
    }

}
