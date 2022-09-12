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
import org.bukkit.Bukkit

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
            plugin.networkMessenger.sendAfkStateToProxy(
                player,
                afkPlayersState[player.uniqueId] ?: false
            )
            plugin.networkMessenger.sendVanishStateToProxy(
                player,
                vanishPlayersState[player.uniqueId] ?: false
            )
        }
        plugin.logger.info("[Bridge] Operation done.")
    }

    fun registerHooks() {
        val pluginManager = plugin.server.pluginManager
        val config = plugin.configurationManager.getConfiguration()

        // Essentials Hook
        if(config.hooks.essentials.enable) {
            pluginManager.getPlugin("Essentials")?.run {
                plugin.logger.info("[Hook] Essentials plugin found.")
                hooks.add(EssentialsHook(this, plugin).also { pluginManager.registerEvents(it, plugin) })
            }
        }

        if(config.hooks.jetsAntiAfkPro.enable) {
            // AntiAFKPro Hook
            pluginManager.getPlugin("JetsAntiAFKPro")?.run {
                plugin.logger.info("[Hook] JetsAntiAFKPro plugin found.")
                hooks.add(AntiAFKProHook(this, plugin).also { pluginManager.registerEvents(it, plugin) })
            }
        }

        if(config.hooks.superVanish.enable) {
            // SuperVanish Hook
            if (pluginManager.getPlugin("SuperVanish")?.run {
                    plugin.logger.info("[Hook] SuperVanish plugin found.")
                    hooks.add(SuperVanishHook(plugin).also { pluginManager.registerEvents(it, plugin) })
                    this
                } == null) {
                // PremiumVanish Hook
                pluginManager.getPlugin("PremiumVanish")?.run {
                    plugin.logger.info("[Hook] PremiumVanish plugin found.")
                    hooks.add(SuperVanishHook(plugin).also { pluginManager.registerEvents(it, plugin) })
                }
            }
        }
    }

    fun reload() {
        plugin.logger.info("Reloading hooks...")
        hooks.clear()
        registerHooks()
        sendAllPlayersToProxy()
    }

}
