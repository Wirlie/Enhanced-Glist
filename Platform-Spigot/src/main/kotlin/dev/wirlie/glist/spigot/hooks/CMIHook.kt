/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2023 Josue Acevedo and the Enhanced Glist contributors
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

import com.Zrips.CMI.CMI
import com.Zrips.CMI.events.CMIAfkEnterEvent
import com.Zrips.CMI.events.CMIAfkKickEvent
import com.Zrips.CMI.events.CMIAfkLeaveEvent
import com.Zrips.CMI.events.CMIPlayerUnVanishEvent
import com.Zrips.CMI.events.CMIPlayerVanishEvent
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import dev.wirlie.glist.spigot.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.spigot.messenger.messages.VanishStateUpdateMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

class CMIHook(
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, isAFK(it)) }
    }

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, isVanished(it)) }
    }

    override fun isAFK(player: Player): Boolean {
        return CMI.getInstance().playerManager.getUser(player).isAfk
    }

    override fun isVanished(player: Player): Boolean {
        // We do not have enough information to know if this is the appropriate way to do it...
        // What is the difference between [isVanished] and [isCMIVanished]?
        return CMI.getInstance().playerManager.getUser(player).isVanished
    }

    override fun unregister() {

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: CMIPlayerVanishEvent) {
        plugin.messenger.sendMessage(
            VanishStateUpdateMessage(event.player.uniqueId, true),
            event.player.name
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: CMIPlayerUnVanishEvent) {
        plugin.messenger.sendMessage(
            VanishStateUpdateMessage(event.player.uniqueId, false),
            event.player.name
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: CMIAfkEnterEvent) {
        plugin.messenger.sendMessage(
            AFKStateUpdateMessage(event.player.uniqueId, true),
            event.player.name
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: CMIAfkLeaveEvent) {
        plugin.messenger.sendMessage(
            AFKStateUpdateMessage(event.player.uniqueId, false),
            event.player.name
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun event(event: CMIAfkKickEvent) {
        plugin.messenger.sendMessage(
            AFKStateUpdateMessage(event.player.uniqueId, false),
            event.player.name
        )
    }

}
