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

package dev.wirlie.glist.spigot

import dev.wirlie.glist.spigot.hooks.HookManager
import dev.wirlie.glist.spigot.messenger.NetworkMessenger
import dev.wirlie.glist.spigot.messenger.NetworkMessengerListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class EnhancedGlistSpigot: JavaPlugin() {

    lateinit var networkMessenger: NetworkMessenger
    lateinit var hookManager: HookManager

    override fun onEnable() {
        server.messenger.registerOutgoingPluginChannel(this, "enhanced-glist:general")
        server.messenger.registerIncomingPluginChannel(this, "enhanced-glist:general", NetworkMessengerListener(this))

        networkMessenger = NetworkMessenger(this)
        hookManager = HookManager(this)
        hookManager.registerHooks()

        logger.info("[Bridge] Sending afk/vanish state of ${Bukkit.getOnlinePlayers().size} players to Proxy...")
        hookManager.sendAllPlayersToProxy()
        logger.info("[Bridge] Operation done.")
    }

    override fun onDisable() {

    }

}
