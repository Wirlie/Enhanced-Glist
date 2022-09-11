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

import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.velocity.api.events.AFKStateChangeEvent
import dev.wirlie.glist.velocity.api.events.VanishStateChangeEvent
import java.util.concurrent.CompletableFuture

/**
 * Main Velocity implementation
 * @param server Velocity Proxy instance
 */
class VelocityPlatform(
    val server: ProxyServer
): Platform<RegisteredServer, Player, ConsoleCommandSource>() {

    override fun toPlatformServer(server: RegisteredServer): PlatformServer<RegisteredServer> {
        return VelocityPlatformServer(this, server)
    }

    override fun toPlatformExecutorPlayer(executor: Player): PlatformExecutor<RegisteredServer> {
        return VelocityPlayerPlatformExecutor(this, executor)
    }

    override fun toPlatformExecutorConsole(executor: ConsoleCommandSource): PlatformExecutor<RegisteredServer> {
        return VelocityConsolePlatformExecutor(server.consoleCommandSource)
    }

    override fun getAllServers(): List<PlatformServer<RegisteredServer>> {
        return server.allServers.map { VelocityPlatformServer(this, it) }
    }

    override fun getServerByName(name: String): PlatformServer<RegisteredServer>? {
        return server.getServer(name).orElse(null)?.run { VelocityPlatformServer(this@VelocityPlatform, this) }
    }

    override fun getConnectedPlayersAmount(): Int {
        return server.allPlayers.size
    }

    override fun registerHooks() {
        val proxy = server
        val pluginManager = proxy.pluginManager

        if(pluginManager.isLoaded("luckperms")) {
            hookManager.enableLuckPermsHook()
        }
    }

    override fun callAFKStateChangeEvent(
        fromPlayer: PlatformExecutor<RegisteredServer>,
        state: Boolean
    ): CompletableFuture<Boolean> {
        return server.eventManager.fire(AFKStateChangeEvent((fromPlayer as VelocityPlayerPlatformExecutor).executor, state)).thenApply { it.getNewState() }
    }

    override fun callVanishStateChangeEvent(
        fromPlayer: PlatformExecutor<RegisteredServer>,
        state: Boolean
    ): CompletableFuture<Boolean> {
        return server.eventManager.fire(VanishStateChangeEvent((fromPlayer as VelocityPlayerPlatformExecutor).executor, state)).thenApply { it.getNewState() }
    }

}
