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

abstract class NetworkMessenger<S>(
    val platform: Platform<S, *, *>
) {

    private val listeners = mutableListOf<NetworkMessageListener<S>>()

    abstract fun register()

    abstract fun sendObject(channel: String, subject: String, dataObject: String, server: PlatformServer<S>, shouldQueue: Boolean): Boolean

    fun receiveObject(channel: String, subject: String, dataObject: String, fromPlayer: PlatformExecutor<S>, fromServer: PlatformServer<S>) {
        for(listener in listeners) {
            if(listener.channel == channel && listener.subject == subject) {
                listener.onObjectReceive(dataObject, fromPlayer, fromServer)
            }
        }
    }

    fun addListener(listener: NetworkMessageListener<S>) {
        listeners.add(listener)
    }

    fun removeListener(listener: NetworkMessageListener<S>) {
        listeners.remove(listener)
    }

    fun registerListeners() {
        addListener(AfkStateChangeListener(platform))
        addListener(VanishStateChangeListener(platform))

        // Plugin initialized, we need the afk/vanish state of all players.
        requestDataFromAllServers()
    }

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
