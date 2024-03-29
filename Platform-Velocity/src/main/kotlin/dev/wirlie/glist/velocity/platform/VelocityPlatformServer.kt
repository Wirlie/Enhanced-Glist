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

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

/**
 * Velocity implementation for server.
 * @param platform Velocity platform instance.
 * @param server Velocity server reference.
 */
class VelocityPlatformServer(
    val platform: VelocityPlatform,
    server: RegisteredServer
): PlatformServer<RegisteredServer>(
    server
) {

    override fun getName(): String {
        val upperCase = platform.configuration.getSection(GeneralSection::class.java).displayServerNameUppercase
        return if (upperCase) {
            server.serverInfo.name.uppercase()
        } else {
            server.serverInfo.name
        }
    }

    override fun getPlayers(): List<PlatformExecutor<RegisteredServer>> {
        return server.playersConnected.map { VelocityPlayerPlatformExecutor(platform, it) }
    }

}
