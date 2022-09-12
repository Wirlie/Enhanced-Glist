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

import dev.wirlie.glist.spigot.configuration.ConfigurationManager
import dev.wirlie.glist.spigot.hooks.HookManager
import dev.wirlie.glist.spigot.messenger.NetworkMessenger
import dev.wirlie.glist.spigot.messenger.NetworkMessengerListener
import dev.wirlie.glist.spigot.util.AdventureUtil
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class EnhancedGlistSpigot: JavaPlugin() {

    lateinit var networkMessenger: NetworkMessenger
    lateinit var hookManager: HookManager
    lateinit var configurationManager: ConfigurationManager

    override fun onEnable() {
        AdventureUtil.adventure = BukkitAudiences.create(this)

        configurationManager = ConfigurationManager(this)

        server.messenger.registerOutgoingPluginChannel(this, "enhanced-glist:general")
        server.messenger.registerIncomingPluginChannel(this, "enhanced-glist:general", NetworkMessengerListener(this))

        networkMessenger = NetworkMessenger(this)
        hookManager = HookManager(this)

        hookManager.registerHooks()
        hookManager.sendAllPlayersToProxy()

        getCommand("egls")!!.executor = GlistExecutor(this)
    }

    override fun onDisable() {

    }

    fun performReload() {
        configurationManager.reload()
        hookManager.reload()
    }

}
