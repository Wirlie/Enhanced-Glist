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
import dev.wirlie.glist.bungeecord.api.events.AFKStateChangeEvent
import dev.wirlie.glist.bungeecord.api.events.VanishStateChangeEvent
import dev.wirlie.glist.bungeecord.hooks.PremiumVanishListener
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.command.ConsoleCommandSender
import java.util.concurrent.CompletableFuture

/**
 * Main BungeeCord implementation
 */
class BungeePlatform(
    val plugin: EnhancedGlistBungeeCord
): Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>() {

    override fun toPlatformServer(server: ServerInfo): PlatformServer<ServerInfo> {
        return BungeePlatformServer(this, server)
    }

    override fun toPlatformExecutorPlayer(executor: ProxiedPlayer): PlatformExecutor<ServerInfo> {
        return BungeePlayerPlatformExecutor(this, executor)
    }

    override fun toPlatformExecutorConsole(executor: ConsoleCommandSender): PlatformExecutor<ServerInfo> {
        return BungeeConsolePlatformExecutor()
    }

    override fun getAllServers(): List<PlatformServer<ServerInfo>> {
        return ProxyServer.getInstance().servers.values.map { BungeePlatformServer(this, it) }
    }

    override fun getServerByName(name: String): PlatformServer<ServerInfo>? {
        return ProxyServer.getInstance().getServerInfo(name)?.run { BungeePlatformServer(this@BungeePlatform, this) }
    }

    override fun getConnectedPlayersAmount(): Int {
        return ProxyServer.getInstance().players.size
    }

    override fun registerHooks() {
        val proxy = ProxyServer.getInstance()
        val pluginManager = proxy.pluginManager

        if(pluginManager.getPlugin("LuckPerms") != null) {
            hookManager.enableLuckPermsHook()
        }

        if(pluginManager.getPlugin("PremiumVanish") != null) {
            pluginManager.registerListener(plugin, PremiumVanishListener(this))
        }

        if(pluginManager.getPlugin("Protocolize") != null) {
            logger.info(Component.text("Protocolize plugin found! GUI feature enabled.", NamedTextColor.GREEN))
            enableGUISystem()
        }
    }

    override fun callAFKStateChangeEvent(fromPlayer: PlatformExecutor<ServerInfo>, state: Boolean): CompletableFuture<Boolean> {
        val event = AFKStateChangeEvent((fromPlayer as BungeePlayerPlatformExecutor).player, state)
        ProxyServer.getInstance().pluginManager.callEvent(event)
        return CompletableFuture.completedFuture(event.getNewState())
    }

    override fun callVanishStateChangeEvent(fromPlayer: PlatformExecutor<ServerInfo>, state: Boolean): CompletableFuture<Boolean> {
        val event = VanishStateChangeEvent((fromPlayer as BungeePlayerPlatformExecutor).player, state)
        ProxyServer.getInstance().pluginManager.callEvent(event)
        return CompletableFuture.completedFuture(event.getNewState())
    }

    override fun performCommandForPlayer(player: PlatformExecutor<ServerInfo>, command: String) {
        val playerBungee = (player as BungeePlayerPlatformExecutor).player
        ProxyServer.getInstance().pluginManager.dispatchCommand(playerBungee, command)
    }

    override fun toPlatformComponent(component: Component): Any {
        // Adventure to legacy string
        val legacyString = AdventureUtil.legacySectionSerialize(component)
        // Really unsafe and inconsistent, but we do not have another option...
        return TextComponent.fromLegacyText(legacyString).also {
            for(com in it) {
                // Unfortunately, we cannot track what components are italic by the user and what components are italic due BungeeCord implementation...
                com.isItalic = false
            }
        }
    }

}
