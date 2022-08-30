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

package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.bungeecord.EnhancedGlistBungeeCord
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class BungeePlayerPlatformExecutor(
    val player: ProxiedPlayer
): PlatformExecutor<ServerInfo>() {

    override fun isPlayer(): Boolean {
        return true
    }

    override fun isConsole(): Boolean {
        return false
    }

    override fun asAudience(): Audience {
        return EnhancedGlistBungeeCord.getAdventure().player(player)
    }

    override fun getName(): String {
        return player.name
    }

    override fun getUUID(): UUID {
        return player.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<ServerInfo>? {
        val server = player.server ?: return null
        return BungeePlatformServer(server.info)
    }

}
