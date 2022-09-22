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
import dev.wirlie.glist.spigot.messenger.messages.VanishStateUpdateMessage
import me.xtomyserrax.StaffFacilities.SFAPI
import me.xtomyserrax.StaffFacilities.api.events.PlayerStaffvanishEvent
import me.xtomyserrax.StaffFacilities.api.events.PlayerVanishEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

class StaffFacilitiesHook(
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        return mapOf()
    }

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, isVanished(it)) }
    }

    override fun isAFK(player: Player): Boolean? {
        return null
    }

    override fun isVanished(player: Player): Boolean {
        return SFAPI.isPlayerVanished(player)
    }

    override fun unregister() {

    }

    // TODO: I do not know what is the difference between PlayerVanishEvent and PlayerStaffvanishEvent, PENDING TEST BECAUSE THIS IS A PAID RESOURCE!!
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: PlayerVanishEvent) {
        plugin.spigotPluginMessageMessenger.sendMessage(
            VanishStateUpdateMessage(event.player.uniqueId, event.isVanishing),
            event.player.name
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: PlayerStaffvanishEvent) {
        plugin.spigotPluginMessageMessenger.sendMessage(
            VanishStateUpdateMessage(event.player.uniqueId, event.isStaffvanishing),
            event.player.name
        )
    }

}
