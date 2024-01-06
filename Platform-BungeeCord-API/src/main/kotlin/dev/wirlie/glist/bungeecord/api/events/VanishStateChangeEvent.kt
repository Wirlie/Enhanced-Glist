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

package dev.wirlie.glist.bungeecord.api.events

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Event

/**
 * Called when a player changes their Vanish state.
 */
class VanishStateChangeEvent(
    private val player: ProxiedPlayer,
    private var newState: Boolean
): Event() {

    /**
     * @return The player involved in this event.
     */
    fun getPlayer() = player

    /**
     * @return The new state, `true` if state will be added to player, `false` if state will be removed from player.
     */
    fun getNewState() = newState

    /**
     * Change the new state of this event.
     * @param state The state to set as result of this event, `true` if state should be added to player, `false` if state should be removed from player.
     */
    fun setNewState(state: Boolean) {
        newState = state
    }

}
