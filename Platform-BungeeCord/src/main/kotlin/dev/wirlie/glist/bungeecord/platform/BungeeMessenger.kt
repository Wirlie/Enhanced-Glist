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

package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.bungeecord.EnhancedGlistBungeeCord
import dev.wirlie.glist.common.messenger.NetworkMessenger
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.connection.Server
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class BungeeMessenger(
    val plugin: EnhancedGlistBungeeCord,
    private val platformBungee: BungeePlatform
): NetworkMessenger<ServerInfo>(
    platformBungee
), Listener {

    val generalChannelId = "enhanced-glist:general"

    override fun register() {
        val proxy = ProxyServer.getInstance()
        // Unregister channel (can happen if plugin is reloaded via a plugin manager)
        proxy.unregisterChannel(generalChannelId)
        // Register channel
        proxy.registerChannel(generalChannelId)
        // Events
        proxy.pluginManager.registerListener(plugin, this)
    }

    override fun sendObject(
        channel: String,
        subject: String,
        dataObject: String,
        server: PlatformServer<ServerInfo>,
        shouldQueue: Boolean
    ): Boolean {
        val platformServer = server as BungeePlatformServer

        val dataByteOut = ByteArrayOutputStream()
        val dataOut = DataOutputStream(dataByteOut)

        dataOut.writeUTF(subject)
        dataOut.writeUTF(dataObject)

        dataOut.close()
        dataByteOut.close()

        return platformServer.server.sendData(
            channel,
            dataByteOut.toByteArray(),
            shouldQueue
        )
    }

    @EventHandler
    fun onMessage(event: PluginMessageEvent) {
        if(event.tag == generalChannelId && event.receiver is ProxiedPlayer && event.sender is Server) {
            val dataByteIn = ByteArrayInputStream(event.data)
            val dataIn = DataInputStream(dataByteIn)

            val subject = dataIn.readUTF()
            val dataObject = dataIn.readUTF()

            receiveObject(
                generalChannelId,
                subject,
                dataObject,
                platformBungee.toPlatformExecutorPlayer(event.receiver as ProxiedPlayer),
                platformBungee.toPlatformServer((event.sender as Server).info)
            )
        } else {
            //TODO: REMOVE THIS!!!
            platform.logger.warning(
                Component.text("NOT HANDLED MESSAGE")
                    .append(Component.newline())
                    .append(Component.text("id = ${event.tag}"))
                    .append(Component.newline())
                    .append(Component.text("event.receiver = ${event.receiver}"))
                    .append(Component.newline())
                    .append(Component.text("event.sender = ${event.sender}"))
            )
        }
    }

}
