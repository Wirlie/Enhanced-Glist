/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022  Josue Acevedo
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

package dev.wirlie.spigot.glist

import dev.wirlie.spigot.glist.hooks.AbstractHook
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer

class JoinListener(private val bridge: EnhancedGlistSpigot) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun event(e: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                bridge.getHooks().forEach(Consumer { h: AbstractHook -> h.sendPlayerToBridge(e.player) })
            }
        }.runTask(bridge)
    }
}
