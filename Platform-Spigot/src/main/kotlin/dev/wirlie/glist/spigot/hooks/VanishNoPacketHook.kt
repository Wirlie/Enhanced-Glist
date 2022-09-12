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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.kitteh.vanish.VanishPlugin
import org.kitteh.vanish.event.VanishStatusChangeEvent
import java.util.*

class VanishNoPacketHook(
    externalPlugin: Plugin,
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    private val api = externalPlugin as VanishPlugin

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, isVanished(it)) }
    }

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        return mapOf()
    }

    override fun unregister() {

    }

    private fun isVanished(player: Player): Boolean {
        return api.manager.isVanished(player)
    }

    @EventHandler
    fun event(event: VanishStatusChangeEvent) {
        plugin.networkMessenger.sendVanishStateToProxy(event.player, event.isVanishing)
    }

}
