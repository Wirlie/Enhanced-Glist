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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import com.velocitypowered.api.scheduler.ScheduledTask
import dev.simplix.protocolize.api.chat.ChatElement
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.velocity.EnhancedGlistVelocity
import dev.wirlie.glist.velocity.api.events.AFKStateChangeEvent
import dev.wirlie.glist.velocity.api.events.VanishStateChangeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * Main Velocity implementation
 * @param server Velocity Proxy instance
 */
class VelocityPlatform(
    val plugin: EnhancedGlistVelocity,
    val server: ProxyServer,
    val reloadMessengerCallback: () -> Unit
): Platform<RegisteredServer, Player, ConsoleCommandSource>() {

    private var updaterCheckTask: ScheduledTask? = null
    private var consoleNotificationTask: ScheduledTask? = null

    override fun reloadMessenger() {
        reloadMessengerCallback()
    }

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

    override fun registerHooks() {
        val proxy = server
        val pluginManager = proxy.pluginManager

        if(pluginManager.isLoaded("luckperms")) {
            hookManager.enableLuckPermsHook()
        }

        if(pluginManager.isLoaded("protocolize")) {
            logger.info(Component.text("Protocolize plugin found! GUI feature enabled.", NamedTextColor.GREEN))
            enableGUISystem()
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

    override fun performCommandForPlayer(player: PlatformExecutor<RegisteredServer>, command: String) {
        val playerVelocity = (player as VelocityPlayerPlatformExecutor).executor
        server.commandManager.executeAsync(playerVelocity, command)
    }

    override fun toProtocolizeChatElement(component: Component): ChatElement<Any> {
        // Velocity already supports Adventure components
        return ChatElement.of(component)
    }

    override fun getAllPlayers(): List<PlatformExecutor<RegisteredServer>> {
        return server.allPlayers.map { VelocityPlayerPlatformExecutor(this, it) }
    }

    override fun scheduleUpdaterCheckTask(task: Runnable, periodSeconds: Int) {
        updaterCheckTask = server.scheduler.buildTask(plugin, task).delay(10, TimeUnit.SECONDS).repeat(periodSeconds.toLong(), TimeUnit.SECONDS).schedule()
    }

    override fun stopUpdaterCheckTask() {
        updaterCheckTask?.cancel()
    }

    override fun scheduleConsoleNotificationTask(task: Runnable, periodSeconds: Int) {
        consoleNotificationTask = server.scheduler.buildTask(plugin, task).delay(periodSeconds.toLong(), TimeUnit.SECONDS).repeat(periodSeconds.toLong(), TimeUnit.SECONDS).schedule()
    }

    override fun stopConsoleNotificationTask() {
        consoleNotificationTask?.cancel()
    }

    override fun scheduleLater(task: Runnable, time: Long, unit: TimeUnit) {
        server.scheduler.buildTask(plugin, task).delay(time, unit).schedule()
    }

    override fun getPlayerByName(name: String): PlatformExecutor<RegisteredServer>? {
        val player = server.getPlayer(name).orElse(null) ?: return null
        return toPlatformExecutorPlayer(player)
    }

    override fun getPlayerByUUID(uuid: UUID): PlatformExecutor<RegisteredServer>? {
        val player = server.getPlayer(uuid).orElse(null) ?: return null
        return toPlatformExecutorPlayer(player)
    }

}
