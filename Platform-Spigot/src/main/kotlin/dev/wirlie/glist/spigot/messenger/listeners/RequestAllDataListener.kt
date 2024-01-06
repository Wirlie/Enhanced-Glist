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

package dev.wirlie.glist.spigot.messenger.listeners

import dev.wirlie.glist.messenger.api.MessageListener
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import dev.wirlie.glist.spigot.messenger.messages.RequestAllDataMessage
import org.bukkit.Bukkit
import java.util.*

class RequestAllDataListener(
    val plugin: EnhancedGlistSpigot
): MessageListener<RequestAllDataMessage>(
    RequestAllDataMessage::class.java
) {

    override fun onAsyncMessage(message: RequestAllDataMessage, fromPlayerUUID: UUID?, fromServerId: String?) {
        plugin.logger.info("[Proxy] Received a request from Proxy to send all data.")
        plugin.logger.info("[Proxy] ${Bukkit.getOnlinePlayers().size} players will be sent to Proxy.")
        plugin.hookManager.sendAllPlayersToProxy()
    }

}
