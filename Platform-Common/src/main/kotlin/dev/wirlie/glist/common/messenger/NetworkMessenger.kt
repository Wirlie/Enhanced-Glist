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

package dev.wirlie.glist.common.messenger

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.messenger.listeners.AfkStateChangeListener
import dev.wirlie.glist.common.messenger.listeners.VanishStateChangeListener
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Network Messenger to send/receive messages across the Network using Plugin Messages.
 * @param platform Platform instance.
 */
abstract class NetworkMessenger<S>(
    val platform: Platform<S, *, *>
) {

    private val listeners = mutableListOf<NetworkMessageListener<S>>()

    /**
     * Register messenger (platform-dependent implementation)
     */
    abstract fun register()

    /**
     * Send object using Plugin Message (platform-dependent implementation)
     * @param channel Channel to use to send message.
     * @param subject Subject to use to send message.
     * @param dataObject Data to send.
     * @param server Target server to delivery message.
     * @param shouldQueue If message should be queued if delivery fails, not all Platforms can handle this feature and
     * if message cannot be delivered instantly then message will be discarded.
     */
    abstract fun sendObject(channel: String, subject: String, dataObject: String, server: PlatformServer<S>, shouldQueue: Boolean): Boolean

    /**
     * Utility function to handle object received using Plugin Message.
     * @param channel Channel in which the object was received.
     * @param subject Subject in which the object was received.
     * @param dataObject Received object.
     * @param fromPlayer Player that have delivered this message to the Proxy.
     * @param fromServer Server that have delivered this message to the Proxy.
     */
    fun receiveObject(channel: String, subject: String, dataObject: String, fromPlayer: PlatformExecutor<S>, fromServer: PlatformServer<S>) {
        for(listener in listeners) {
            if(listener.channel == channel && listener.subject == subject) {
                listener.onObjectReceive(dataObject, fromPlayer, fromServer)
            }
        }
    }

    /**
     * Add a listener for incoming messages.
     * @param listener Listener to add.
     */
    fun addListener(listener: NetworkMessageListener<S>) {
        listeners.add(listener)
    }

    /**
     * Remove a listener for incoming messages.
     * @param listener Listener to remove.
     */
    fun removeListener(listener: NetworkMessageListener<S>) {
        listeners.remove(listener)
    }

    /**
     * Register default listeners.
     */
    fun registerListeners() {
        addListener(AfkStateChangeListener(platform))
        addListener(VanishStateChangeListener(platform))

        // Plugin initialized, we need the afk/vanish state of all players.
        requestDataFromAllServers()
    }

    /**
     * Utility function to request players from all servers.
     */
    private fun requestDataFromAllServers() {
        for(server in platform.getAllServers()) {
            val status = sendObject(
                "enhanced-glist:general",
                "request-all-data",
                "",
                server,
                true
            )

            if (status) {
                platform.logger.info(
                    Component.text("Requested afk/vanish state of all players for server ", NamedTextColor.WHITE)
                        .append(Component.text(server.getName(), NamedTextColor.AQUA))
                )
            }
        }
    }

}
