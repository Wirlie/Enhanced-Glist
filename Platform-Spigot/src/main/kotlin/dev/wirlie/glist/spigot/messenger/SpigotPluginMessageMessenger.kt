/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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

import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class SpigotPluginMessageMessenger(
    val plugin: EnhancedGlistSpigot
): PlatformMessenger(null), PluginMessageListener {

    private val channelId = "egl:general"

    override fun register() {
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, channelId)
        plugin.server.messenger.registerIncomingPluginChannel(plugin, channelId, this)
    }

    override fun unregister() {
        plugin.server.messenger.unregisterOutgoingPluginChannel(plugin, channelId)
        plugin.server.messenger.unregisterIncomingPluginChannel(plugin, channelId)
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        if(targetSenderObject == null) throw NullPointerException("targetSenderObject is required.")

        val player = plugin.server.getPlayer(targetSenderObject) ?: throw IllegalStateException("Player $targetSenderObject is not available to delivery message.")

        player.sendPluginMessage(
            plugin, channelId, packMessage(subject, data)
        )
    }

    override fun onPluginMessageReceived(channel: String, fromPlayer: Player, data: ByteArray) {
        if(channel == channelId) {
            val unpacked = unpackMessage(data)

            receiveMessage(
                channel,
                unpacked.first,
                unpacked.second,
                fromPlayer.uniqueId,
                "" // Spigot cannot know the server that have sent this message...
            )
        }
    }

}
