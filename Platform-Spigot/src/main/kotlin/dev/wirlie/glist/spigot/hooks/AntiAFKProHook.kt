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
import dev.wirlie.glist.spigot.configuration.PluginConfiguration
import me.jet315.antiafkpro.AntiAFKProAPI
import me.jet315.antiafkpro.JetsAntiAFKPro
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * Class to provide compatibility with AntiAFKPro.
 * @param externalPlugin AntiAFKPro plugin instance.
 * @param plugin EnhancedGlist plugin instance.
 */
class AntiAFKProHook(
    externalPlugin: Plugin,
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    private var task: BukkitTask? = null
    val api: AntiAFKProAPI = (externalPlugin as JetsAntiAFKPro).antiAFKProAPI

    // We need to track afk players manually due to AntiAFKPro limitations.
    private val knowAFKPlayers = mutableListOf<UUID>()

    init {
        detectAFKPlayers()
        startLookupTask()
    }

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, knowAFKPlayers.contains(it.uniqueId)) }
    }

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        // This plugin does not have "vanish" feature.
        return mutableMapOf()
    }

    @EventHandler
    fun event(event: PlayerQuitEvent) {
        // Untrack disconnected players to prevent a memory leak.
        knowAFKPlayers.remove(event.player.uniqueId)
    }

    private fun detectAFKPlayers() {
        for(player in Bukkit.getOnlinePlayers()) {
            if(isAFK(player)) {
                knowAFKPlayers.add(player.uniqueId)
            }
        }
    }

    private fun startLookupTask() {
        val config = plugin.configurationManager.getConfiguration().hooks.jetsAntiAfkPro

        // Start a task that lookup players periodically
        task = object: BukkitRunnable() {
            override fun run() {
                for(player in Bukkit.getOnlinePlayers()) {
                    val storedAFK = knowAFKPlayers.contains(player.uniqueId)
                    val currentAFK = isAFK(player)

                    if(storedAFK && !currentAFK) {
                        // Remove AFK state
                        knowAFKPlayers.remove(player.uniqueId)
                        plugin.networkMessenger.sendAfkStateToProxy(player, false)
                    } else if(!storedAFK && currentAFK) {
                        // Add AFK state
                        knowAFKPlayers.add(player.uniqueId)
                        plugin.networkMessenger.sendAfkStateToProxy(player, true)
                    }
                }
            }
        }.runTaskTimer(plugin, config.settings.checkPeriod.toLong(), config.settings.checkPeriod.toLong())
    }

    private fun isAFK(player: Player): Boolean {
        val config = plugin.configurationManager.getConfiguration().hooks.jetsAntiAfkPro
        val settings = config.settings

        if(player.hasPermission(settings.permissionToTreatPlayerAsAfk)) {
            return true
        }

        val afkTime = api.getAFKPlayer(player)?.secondsAFK ?: 0

        if(afkTime >= settings.timeToTreatPlayerAsAfk) {
            return true
        }

        return false
    }

    override fun unregister() {
        task?.cancel()
    }

}
