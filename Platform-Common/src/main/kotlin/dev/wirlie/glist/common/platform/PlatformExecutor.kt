/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022  Josue Acevedo
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

abstract class PlatformExecutor<S> {

    abstract fun isConsole(): Boolean

    abstract fun isPlayer(): Boolean

    abstract fun asAudience(): Audience

    abstract fun getName(): String

    abstract fun getUUID(): UUID

    abstract fun hasPermission(permission: String): Boolean

    abstract fun getConnectedServer(): PlatformServer<S>?

}
