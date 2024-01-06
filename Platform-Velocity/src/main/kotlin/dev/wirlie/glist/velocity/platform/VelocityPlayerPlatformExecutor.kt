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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import java.util.*

/**
 * Velocity implementation for command executor.
 */
class VelocityPlayerPlatformExecutor(
    val platform: VelocityPlatform,
    val executor: Player
): PlatformExecutor<RegisteredServer>() {

    override fun isConsole(): Boolean {
        return false
    }

    override fun isPlayer(): Boolean {
        return true
    }

    override fun asAudience(): Audience {
        return executor
    }

    override fun getName(): String {
        return executor.username
    }

    override fun getUUID(): UUID {
        return executor.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return executor.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<RegisteredServer>? {
        val server = executor.currentServer.orElse(null) ?: return null
        return VelocityPlatformServer(platform, server.server)
    }

}
