/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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

package dev.wirlie.glist.spigot.listeners

import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import dev.wirlie.glist.spigot.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.spigot.messenger.messages.VanishStateUpdateMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(
    val plugin: EnhancedGlistSpigot
): Listener {

    @EventHandler
    fun event(event: PlayerJoinEvent) {
        var isAFK = false
        var isVanish = false

        for(hook in plugin.hookManager.getHooks()) {
            if(hook.isAFK(event.player) == true) {
                isAFK = true
            }
            if(hook.isVanished(event.player) == true) {
                isVanish = true
            }
        }

        //Send to proxy
        plugin.messenger.sendMessage(
            AFKStateUpdateMessage(event.player.uniqueId, isAFK),
            event.player.name
        )
        plugin.messenger.sendMessage(
            VanishStateUpdateMessage(event.player.uniqueId, isVanish),
            event.player.name
        )
    }

}
