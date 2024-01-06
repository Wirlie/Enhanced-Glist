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

package dev.wirlie.glist.velocity.api

import com.velocitypowered.api.proxy.Player
import java.util.UUID

interface StateManager {

    /**
     * Check if player has "vanish" state.
     * @param player Player to check.
     * @return `true` if player has state, `false` if not.
     */
    fun hasVanishState(player: Player): Boolean

    /**
     * Check if player has "vanish" state.
     * @param player Player to check.
     * @return `true` if player has state, `false` if not.
     */
    fun hasVanishState(player: UUID): Boolean

    /**
     * Add "vanish" state to player.
     * @param player Player to add state.
     * @return `true` if state is added, `false` if player already has state.
     */
    fun addVanishState(player: Player): Boolean

    /**
     * Add "vanish" state to player.
     * @param player Player to add state.
     * @return `true` if state is added, `false` if player already has state.
     */
    fun addVanishState(player: UUID): Boolean

    /**
     * Remove "vanish" state from player.
     * @param player Player to remove state.
     * @return `true` if state is removed, `false` if player does not have state.
     */
    fun removeVanishState(player: Player): Boolean

    /**
     * Remove "vanish" state from player.
     * @param player Player to remove state.
     * @return `true` if state is removed, `false` if player does not have state.
     */
    fun removeVanishState(player: UUID): Boolean

    /**
     * Check if player has "afk" state.
     * @param player Player to check.
     * @return `true` if player has state, `false` if not.
     */
    fun hasAFKState(player: Player): Boolean

    /**
     * Check if player has "afk" state.
     * @param player Player to check.
     * @return `true` if player has state, `false` if not.
     */
    fun hasAFKState(player: UUID): Boolean

    /**
     * Add "afk" state to player.
     * @param player Player to add state.
     * @return `true` if state is added, `false` if player already has state.
     */
    fun addAFKState(player: Player): Boolean

    /**
     * Add "afk" state to player.
     * @param player Player to add state.
     * @return `true` if state is added, `false` if player already has state.
     */
    fun addAFKState(player: UUID): Boolean

    /**
     * Remove "afk" state from player.
     * @param player Player to remove state.
     * @return `true` if state is removed, `false` if player does not have state.
     */
    fun removeAFKState(player: Player): Boolean

    /**
     * Remove "afk" state from player.
     * @param player Player to remove state.
     * @return `true` if state is removed, `false` if player does not have state.
     */
    fun removeAFKState(player: UUID): Boolean

}
