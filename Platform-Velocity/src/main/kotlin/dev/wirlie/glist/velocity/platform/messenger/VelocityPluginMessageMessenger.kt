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

package dev.wirlie.glist.velocity.platform.messenger

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.messenger.PlatformMessenger

class VelocityPluginMessageMessenger(
    val server: ProxyServer
): PlatformMessenger(null) {

    private val channelId = MinecraftChannelIdentifier.create("enhanced-glist", "general")

    override fun register() {
        server.channelRegistrar.register(channelId)
    }

    override fun unregister() {
        server.channelRegistrar.unregister(channelId)
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        val serversToSend = mutableListOf<RegisteredServer>()
        if(targetSenderObject == null) {
            // send to all servers
            serversToSend.addAll(server.allServers)
        } else {
            // send to specified server, if exists
            server.getServer(targetSenderObject).orElse(null)?.run {
                serversToSend.add(this)
            }
        }

        if(serversToSend.isEmpty()) return

        for(server in serversToSend) {
            server.sendPluginMessage(channelId, packMessage(subject, data))
        }
    }

    @Subscribe
    fun onMessage(event: PluginMessageEvent) {
        if(event.identifier == channelId && event.source is ServerConnection && event.target is Player) {
            val unpacked = unpackMessage(event.data)

            receiveMessage(
                event.identifier.id,
                unpacked.first,
                unpacked.second,
                (event.target as Player).uniqueId,
                (event.source as ServerConnection).server.serverInfo.name
            )
        }
    }

}
