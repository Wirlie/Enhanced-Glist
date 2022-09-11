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

package dev.wirlie.glist.bungeecord.api.impl

import dev.wirlie.glist.bungeecord.api.StateManager
import dev.wirlie.glist.common.Platform
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.command.ConsoleCommandSender
import java.util.*

class StateManagerImpl(
    val platform: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>
): StateManager {

    override fun hasVanishState(player: ProxiedPlayer): Boolean {
        return hasVanishState(player.uniqueId)
    }

    override fun hasVanishState(player: UUID): Boolean {
        return platform.playerManager.hasVanishState(player)
    }

    override fun addVanishState(player: ProxiedPlayer): Boolean {
        return addVanishState(player.uniqueId)
    }

    override fun addVanishState(player: UUID): Boolean {
        if(hasVanishState(player)) return false

        platform.playerManager.setVanishState(player, true)
        return true
    }

    override fun removeVanishState(player: ProxiedPlayer): Boolean {
        return removeVanishState(player.uniqueId)
    }

    override fun removeVanishState(player: UUID): Boolean {
        if(!hasVanishState(player)) return false

        platform.playerManager.setVanishState(player, false)
        return true
    }

    override fun hasAFKState(player: ProxiedPlayer): Boolean {
        return hasAFKState(player.uniqueId)
    }

    override fun hasAFKState(player: UUID): Boolean {
        return platform.playerManager.hasAFKState(player)
    }

    override fun addAFKState(player: ProxiedPlayer): Boolean {
        return addAFKState(player.uniqueId)
    }

    override fun addAFKState(player: UUID): Boolean {
        if(hasAFKState(player)) return false

        platform.playerManager.setAFKState(player, true)
        return true
    }

    override fun removeAFKState(player: ProxiedPlayer): Boolean {
        return removeAFKState(player.uniqueId)
    }

    override fun removeAFKState(player: UUID): Boolean {
        if(!hasAFKState(player)) return false

        platform.playerManager.setAFKState(player, false)
        return true
    }

}
