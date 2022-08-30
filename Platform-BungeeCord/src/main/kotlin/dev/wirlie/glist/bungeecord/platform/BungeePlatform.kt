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

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.command.ConsoleCommandSender

class BungeePlatform: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>() {

    override fun toPlatformServer(server: ServerInfo): PlatformServer<ServerInfo> {
        return BungeePlatformServer(server)
    }

    override fun toPlatformExecutorPlayer(executor: ProxiedPlayer): PlatformExecutor<ServerInfo> {
        return BungeePlayerPlatformExecutor(executor)
    }

    override fun toPlatformExecutorConsole(executor: ConsoleCommandSender): PlatformExecutor<ServerInfo> {
        return BungeeConsolePlatformExecutor()
    }

    override fun getAllServers(): List<PlatformServer<ServerInfo>> {
        return ProxyServer.getInstance().servers.values.map { BungeePlatformServer(it) }
    }

    override fun getConnectedPlayersAmount(): Int {
        return ProxyServer.getInstance().players.size
    }

}
