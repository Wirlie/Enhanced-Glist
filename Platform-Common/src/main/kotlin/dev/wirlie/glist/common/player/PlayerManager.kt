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

    fun getAFKState(executor: PlatformExecutor<*>): Boolean? {
        if(executor.isConsole()) throw IllegalArgumentException("Console executor is not allowed here.")
        return getAFKState(executor.getUUID())
    }

    fun getAFKState(uuid: UUID) = knowAFKState[uuid]

    fun setAFKState(executor: PlatformExecutor<*>, state: Boolean) {
        setAFKState(executor.getUUID(), state)
    }

    fun setAFKState(uuid: UUID, state: Boolean) {
        knowAFKState[uuid] = state
    }

    fun getVanishState(executor: PlatformExecutor<*>): Boolean? {
        if(executor.isConsole()) throw IllegalArgumentException("Console executor is not allowed here.")
        return getVanishState(executor.getUUID())
    }

    fun getVanishState(uuid: UUID) = knowVanishState[uuid]

    fun hasVanishState(uuid: UUID) = getVanishState(uuid) ?: false

    fun setVanishState(executor: PlatformExecutor<*>, state: Boolean) {
        setVanishState(executor.getUUID(), state)
    }

    fun setVanishState(uuid: UUID, state: Boolean) {
        knowVanishState[uuid] = state
    }

    fun handlePlayerDisconnect(uuid: UUID) {
        knowAFKState.remove(uuid)
        knowVanishState.remove(uuid)
    }

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
