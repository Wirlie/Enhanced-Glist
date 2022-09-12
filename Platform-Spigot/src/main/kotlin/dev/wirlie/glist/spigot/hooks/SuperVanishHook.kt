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

import de.myzelyam.api.vanish.PostPlayerHideEvent
import de.myzelyam.api.vanish.PostPlayerShowEvent
import de.myzelyam.api.vanish.VanishAPI
import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class SuperVanishHook(
    val plugin: EnhancedGlistSpigot
): AbstractHook, Listener {

    override fun computePlayersAfkState(): Map<UUID, Boolean> {
        // SuperVanish does not have AFK feature
        return mutableMapOf()
    }

    override fun computePlayersVanishState(): Map<UUID, Boolean> {
        return Bukkit.getOnlinePlayers().associate { Pair(it.uniqueId, VanishAPI.isInvisible(it)) }
    }

    @EventHandler
    fun event(event: PostPlayerShowEvent) {
        plugin.networkMessenger.sendVanishStateToProxy(event.player, false)
    }

    @EventHandler
    fun event(event: PostPlayerHideEvent) {
        plugin.networkMessenger.sendVanishStateToProxy(event.player, true)
    }

    override fun unregister() {

    }

}
