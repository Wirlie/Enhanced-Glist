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

package dev.wirlie.glist.bungeecord

import dev.wirlie.glist.bungeecord.api.impl.EnhancedGlistAPIImpl
import dev.wirlie.glist.bungeecord.listener.PlayerDisconnectListener
import dev.wirlie.glist.bungeecord.platform.BungeeMessenger
import dev.wirlie.glist.bungeecord.platform.BungeePlatform
import dev.wirlie.glist.bungeecord.platform.BungeePlatformCommandManager
import dev.wirlie.glist.common.Platform
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

class EnhancedGlistBungeeCord: Plugin() {

    lateinit var platform: BungeePlatform

    override fun onEnable() {
        adventure = BungeeAudiences.create(this)
        platform = BungeePlatform()
        platform.pluginFolder = dataFolder
        platform.console = adventure.console()
        platform.setup(
            BungeePlatformCommandManager(platform, ProxyServer.getInstance().pluginManager, this),
            BungeeMessenger(this, platform)
        )

        val proxy = ProxyServer.getInstance()
        val pluginManager = proxy.pluginManager

        pluginManager.registerListener(this, PlayerDisconnectListener(platform))

        // Init API
        EnhancedGlistAPIImpl(platform)
    }

    override fun onDisable() {
        platform.disable()
    }

    companion object {
        private lateinit var adventure: BungeeAudiences

        fun getAdventure(): BungeeAudiences {
            if (!this::adventure.isInitialized) {
                throw IllegalStateException("Cannot retrieve audience provider when plugin is not enabled")
            }
            return adventure
        }
    }

}
