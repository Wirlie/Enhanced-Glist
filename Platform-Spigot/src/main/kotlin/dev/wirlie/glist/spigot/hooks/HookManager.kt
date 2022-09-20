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

package dev.wirlie.glist.spigot.hooks

import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import dev.wirlie.glist.spigot.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.spigot.messenger.messages.VanishStateUpdateMessage
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class HookManager(val plugin: EnhancedGlistSpigot) {

    private val hooks = mutableListOf<AbstractHook>()

    fun sendAllPlayersToProxy() {
        plugin.logger.info("[Bridge] Sending afk/vanish state of ${Bukkit.getOnlinePlayers().size} players to Proxy...")
        // Compute all player states from all available hooks
        val afkPlayersState = Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, false) }.toMutableMap()
        val vanishPlayersState = Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, false) }.toMutableMap()

        // Iterate hooks
        for(hook in hooks) {
            hook.computePlayersAfkState().filter { it.value }.forEach { (uuid, _) ->
                afkPlayersState[uuid] = true
            }

            hook.computePlayersVanishState().filter { it.value }.forEach { (uuid, _) ->
                vanishPlayersState[uuid] = true
            }
        }

        // Send to Proxy
        for(player in Bukkit.getOnlinePlayers()) {
            plugin.spigotPluginMessageMessenger.sendMessage(
                AFKStateUpdateMessage(afkPlayersState[player.uniqueId] ?: false),
                player.name
            )
            plugin.spigotPluginMessageMessenger.sendMessage(
                VanishStateUpdateMessage(vanishPlayersState[player.uniqueId] ?: false),
                player.name
            )
        }
        plugin.logger.info("[Bridge] Operation done.")
    }

    fun registerHooks() {
        val pluginManager = plugin.server.pluginManager
        val config = plugin.configurationManager.getConfiguration()

        // Essentials Hook
        pluginManager.getPlugin("Essentials")?.run {
            if(config.hooks.essentials.enable) {
                plugin.logger.info("[Hook] Essentials plugin found.")
                hooks.add(EssentialsHook(this, plugin).also { pluginManager.registerEvents(it, plugin) })
            } else {
                plugin.logger.info("[Hook] Essentials hook disabled by configuration.")
            }
        }

        // AntiAFKPro Hook
        pluginManager.getPlugin("JetsAntiAFKPro")?.run {
            if(config.hooks.jetsAntiAfkPro.enable) {
                plugin.logger.info("[Hook] JetsAntiAFKPro plugin found.")
                hooks.add(AntiAFKProHook(this, plugin).also { pluginManager.registerEvents(it, plugin) })
            } else {
                plugin.logger.info("[Hook] JetsAntiAFKPro hook disabled by configuration.")
            }
        }

        // SuperVanish Hook
        if (pluginManager.getPlugin("SuperVanish")?.run {
                if(config.hooks.superVanish.enable) {
                    plugin.logger.info("[Hook] SuperVanish plugin found.")
                    hooks.add(SuperVanishHook(plugin).also { pluginManager.registerEvents(it, plugin) })
                    this
                } else {
                    plugin.logger.info("[Hook] SuperVanish hook disabled by configuration.")
                    null
                }
            } == null) {

            if(config.hooks.superVanish.enable) {
                // PremiumVanish Hook
                pluginManager.getPlugin("PremiumVanish")?.run {
                    plugin.logger.info("[Hook] PremiumVanish plugin found.")
                    hooks.add(SuperVanishHook(plugin).also { pluginManager.registerEvents(it, plugin) })
                }
            } else {
                plugin.logger.info("[Hook] PremiumVanish hook disabled by configuration.")
            }
        }

        pluginManager.getPlugin("VanishNoPacket")?.run {
            if(config.hooks.vanishNoPacket.enable) {
                plugin.logger.info("[Hook] VanishNoPacket plugin found.")
                hooks.add(VanishNoPacketHook(this, plugin).also { pluginManager.registerEvents(it, plugin) })
            } else {
                plugin.logger.info("[Hook] VanishNoPacket hook disabled by configuration.")
            }
        }

        pluginManager.getPlugin("StaffFacilities")?.run {
            if(config.hooks.staffFacilities.enable) {
                plugin.logger.info("[Hook] StaffFacilities plugin found.")
                hooks.add(StaffFacilitiesHook(plugin).also { pluginManager.registerEvents(it, plugin) })
            } else {
                plugin.logger.info("[Hook] StaffFacilities hook disabled by configuration.")
            }
        }
    }

    fun getHooks() = hooks.toList()

    fun reload() {
        plugin.logger.info("Reloading hooks...")
        hooks.forEach { hook ->
            if(hook is Listener) {
                HandlerList.unregisterAll(hook)
            }
            hook.unregister()
        }
        hooks.clear()
        registerHooks()
        sendAllPlayersToProxy()
    }

}
