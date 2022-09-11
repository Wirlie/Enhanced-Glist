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

package dev.wirlie.glist.common.player

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.hooks.HookType
import dev.wirlie.glist.common.platform.PlatformExecutor
import net.kyori.adventure.text.Component
import java.util.UUID

class PlayerManager(
    val platform: Platform<*, *, *>
) {

    private val knowAFKState = mutableMapOf<UUID, Boolean>()
    private val knowVanishState = mutableMapOf<UUID, Boolean>()

    /**
     * Check if player has "AFK" state.
     * @return true if player has state, false if not or if player state is not know.
     */
    fun hasAFKState(uuid: UUID) = getAFKState(uuid) ?: false

    /**
     * Get AFK state of player.
     * @param executor Executor to get current AFK state.
     * @return true if executor is AFK, null if no state is know for provided player.
     */
    fun getAFKState(executor: PlatformExecutor<*>): Boolean? {
        if(executor.isConsole()) throw IllegalArgumentException("Console executor is not allowed here.")
        return getAFKState(executor.getUUID())
    }

    /**
     * Get AFK state of player.
     * @param uuid Player UUID.
     * @return true if executor is AFK, null if no state is know for provided player.
     */
    fun getAFKState(uuid: UUID) = knowAFKState[uuid]

    /**
     * Set AFK state of player.
     * @param executor Player to set state.
     * @param state New state to set.
     */
    fun setAFKState(executor: PlatformExecutor<*>, state: Boolean) {
        setAFKState(executor.getUUID(), state)
    }

    /**
     * Set AFK state of player.
     * @param uuid Player UUID.
     * @param state New state to set.
     */
    fun setAFKState(uuid: UUID, state: Boolean) {
        knowAFKState[uuid] = state
    }

    /**
     * Get vanish state of player.
     * @param executor Player to get state.
     * @return true if executor is in Vanish mode, null if no state is know for provided player.
     */
    fun getVanishState(executor: PlatformExecutor<*>): Boolean? {
        if(executor.isConsole()) throw IllegalArgumentException("Console executor is not allowed here.")
        return getVanishState(executor.getUUID())
    }

    /**
     * Get vanish state of player.
     * @param uuid Player UUID.
     * @return true if executor is in Vanish mode, null if no state is know for provided player.
     */
    fun getVanishState(uuid: UUID) = knowVanishState[uuid]

    /**
     * Check if player has "Vanish" state.
     * @return true if player has state, false if not or if player state is not know.
     */
    fun hasVanishState(uuid: UUID) = getVanishState(uuid) ?: false

    /**
     * Set Vanish state of player.
     * @param executor Player to set state.
     * @param state New state to set.
     */
    fun setVanishState(executor: PlatformExecutor<*>, state: Boolean) {
        setVanishState(executor.getUUID(), state)
    }

    /**
     * Set Vanish state of player.
     * @param uuid Player UUID
     * @param state New state to set.
     */
    fun setVanishState(uuid: UUID, state: Boolean) {
        knowVanishState[uuid] = state
    }

    /**
     * Utility function to handle player disconnection. This will remove all data related to the player to prevent
     * a memory leak.
     */
    fun handlePlayerDisconnect(uuid: UUID) {
        knowAFKState.remove(uuid)
        knowVanishState.remove(uuid)
    }

    /**
     * @return Current prefix of player.
     */
    fun getPrefix(executor: PlatformExecutor<*>): Component {
        if(executor.isConsole()) {
            throw IllegalArgumentException("Console executor is not allowed here.")
        }

        if(platform.hookManager.isHookEnabled(HookType.LUCKPERMS)) {
            // Use LuckPerms for prefix
            return platform.hookManager.getLuckPermsHook()!!.getPlayerPrefix(executor)
        }

        // No prefix...
        return Component.empty()
    }

}
