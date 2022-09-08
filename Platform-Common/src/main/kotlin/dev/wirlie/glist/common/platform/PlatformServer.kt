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

package dev.wirlie.glist.common.platform

/**
 * Abstract representation of server, compatible with multiple Platforms.
 */
abstract class PlatformServer<S>(
    val server: S
) {

    /**
     * Get the name of this server.
     */
    abstract fun getName(): String

    /**
     * Get players connected to this server.
     * @param onlyReachableBy Optional executor to use to filter vanished players if executor does not have permission
     * to see vanished players, if no executor is provided then all players will be returned.
     */
    abstract fun getPlayers(onlyReachableBy: PlatformExecutor<S>? = null): List<PlatformExecutor<S>>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatformServer<*>) return false

        if (server != other.server) return false

        return true
    }

    override fun hashCode(): Int {
        return server.hashCode()
    }


}
