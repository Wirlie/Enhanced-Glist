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
import dev.wirlie.glist.bungeecord.commands.EglExecutor
import dev.wirlie.glist.bungeecord.commands.GlistExecutor
import dev.wirlie.glist.bungeecord.commands.SlistExecutor
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformCommandManager
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.PluginManager
import net.md_5.bungee.command.ConsoleCommandSender

/**
 * BungeeCord implementation for command management.
 * @param platformInstance BungeeCord platform instance.
 * @param pluginManager BungeeCord plugin manager.
 * @param plugin Plugin instance.
 */
class BungeePlatformCommandManager(
    private val platformInstance: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>,
    private val pluginManager: PluginManager,
    private val plugin: EnhancedGlistBungeeCord
): PlatformCommandManager<ServerInfo>(
    platformInstance
) {

    override fun registerCommands() {
        pluginManager.registerCommand(plugin, GlistExecutor(platformInstance, glistCommand))
        pluginManager.registerCommand(plugin, SlistExecutor(platformInstance, slistCommand))
        pluginManager.registerCommand(plugin, EglExecutor(platformInstance, eglCommand))
    }

    override fun unregisterCommands() {
        pluginManager.unregisterCommands(plugin)
    }

}
