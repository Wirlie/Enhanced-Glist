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

import com.earth2me.essentials.Essentials
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import net.ess3.api.events.AfkStatusChangeEvent
import net.ess3.api.events.VanishStatusChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.*

class EssentialsHook(
    essentialsPlugin: Plugin,
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    private val essentials = essentialsPlugin as Essentials

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, getAfkState(it)) }
    }

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, getVanishState(it)) }
    }

    private fun getAfkState(player: Player): Boolean {
        return essentials.getUser(player).isAfk
    }

    private fun getVanishState(player: Player): Boolean {
        return essentials.getUser(player).isVanished
    }

    @EventHandler
    fun event(event: VanishStatusChangeEvent) {
        plugin.networkMessenger.sendVanishStateToProxy(event.affected.base, event.value)
    }

    @EventHandler
    fun event(event: AfkStatusChangeEvent) {
        plugin.networkMessenger.sendAfkStateToProxy(event.affected.base, event.value)
    }

}