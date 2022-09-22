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

package dev.wirlie.glist.bungeecord.platform.messenger

import dev.wirlie.glist.messenger.PlatformMessenger
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeePluginMessageMessenger: PlatformMessenger(), Listener {

    private val channelId = "enhanced-glist:general"

    override fun register() {
        ProxyServer.getInstance().registerChannel(channelId)
    }

    override fun unregister() {
        ProxyServer.getInstance().unregisterChannel(channelId)
    }

    override fun sendMessage(subject: String, data: ByteArray, targetSenderObject: String?) {
        val serversToSend = mutableListOf<ServerInfo>()
        if(targetSenderObject == null) {
            // send to all servers
            serversToSend.addAll(ProxyServer.getInstance().servers.map { it.value })
        } else {
            // send to specified server, if exists
            ProxyServer.getInstance().getServerInfo(targetSenderObject)?.run {
                serversToSend.add(this)
            }
        }

        if(serversToSend.isEmpty()) return

        for(server in serversToSend) {
            server.sendData(channelId, packMessage(subject, data), false)
        }
    }

    @EventHandler
    fun onMessage(event: PluginMessageEvent) {
        if(event.tag == channelId && event.receiver is ProxiedPlayer && event.sender is Server) {
            val unpacked = unpackMessage(event.data)

            receiveMessage(event.tag, unpacked.first, unpacked.second, (event.receiver as ProxiedPlayer).uniqueId, (event.sender as Server).info.name)
        }
    }

}
