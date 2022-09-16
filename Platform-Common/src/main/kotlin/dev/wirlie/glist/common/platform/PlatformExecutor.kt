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

import net.kyori.adventure.audience.Audience
import java.util.UUID

/**
 * Abstract representation of command executor, compatible with multiple Platforms.
 */
abstract class PlatformExecutor<S> {

    /**
     * If this executor is the console.
     * @return true if this executor is the console.
     */
    abstract fun isConsole(): Boolean

    /**
     * If this executor is a player.
     * @return true if this executor is a player.
     */
    abstract fun isPlayer(): Boolean

    /**
     * Convenience function to get an Audience instance from this executor.
     * @return Audience instance from this executor.
     */
    abstract fun asAudience(): Audience

    /**
     * @return Name of this executor.
     */
    abstract fun getName(): String

    /**
     * @return UUID of this executor (console will return a random UUID because console does not have a UUID, use [isConsole] to check if this executor is the console)
     */
    abstract fun getUUID(): UUID

    /**
     * Test if this executor has a certain permission.
     * @param permission Permission to test.
     * @return If executor have the provided permission (console will always have permission)
     */
    abstract fun hasPermission(permission: String): Boolean

    /**
     * @return Connected server of this executor, or null if this executor is not connected to any server (console will
     * always return null)
     */
    abstract fun getConnectedServer(): PlatformServer<S>?

}
