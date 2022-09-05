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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.messenger.NetworkMessenger
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.velocity.EnhancedGlistVelocity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class VelocityMessenger(
    val plugin: EnhancedGlistVelocity,
    private val platformVelocity: VelocityPlatform
): NetworkMessenger<RegisteredServer>(
    platformVelocity
) {

    private val generalChannelIdentifier: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create("enhanced-glist", "general")

    override fun register() {
        val proxy = plugin.proxyServer

        // Unregister channel (can happen if plugin is reloaded via a plugin manager)
        proxy.channelRegistrar.unregister(generalChannelIdentifier)
        // Register channel
        proxy.channelRegistrar.register(generalChannelIdentifier)
        // Events
        proxy.eventManager.register(plugin, this)
    }

    override fun sendObject(
        channel: String,
        subject: String,
        dataObject: String,
        server: PlatformServer<RegisteredServer>,
        shouldQueue: Boolean
    ): Boolean {
        val platformServer = server as VelocityPlatformServer

        val dataByteOut = ByteArrayOutputStream()
        val dataOut = DataOutputStream(dataByteOut)

        dataOut.writeUTF(subject)
        dataOut.writeUTF(dataObject)

        dataOut.close()
        dataByteOut.close()

        return platformServer.server.sendPluginMessage(
            generalChannelIdentifier,
            dataByteOut.toByteArray()
        )
    }

    @Subscribe
    fun onMessage(event: PluginMessageEvent) {
        if(event.identifier == generalChannelIdentifier && event.source is ServerConnection && event.target is Player) {
            val dataByteIn = ByteArrayInputStream(event.data)
            val dataIn = DataInputStream(dataByteIn)

            val subject = dataIn.readUTF()
            val dataObject = dataIn.readUTF()

            receiveObject(
                event.identifier.id,
                subject,
                dataObject,
                platformVelocity.toPlatformExecutorPlayer(event.target as Player),
                platformVelocity.toPlatformServer((event.source as ServerConnection).server)
            )
        }
    }

}
