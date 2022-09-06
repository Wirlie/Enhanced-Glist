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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.configuration.sections.BehaviorSection
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

class VelocityPlatformServer(
    val platform: VelocityPlatform,
    server: RegisteredServer
): PlatformServer<RegisteredServer>(
    server
) {

    override fun getName(): String {
        return server.serverInfo.name
    }

    override fun getPlayers(onlyReachableBy: PlatformExecutor<RegisteredServer>?): List<PlatformExecutor<RegisteredServer>> {
        return if (onlyReachableBy == null || onlyReachableBy.isConsole() /* Console always have permission */) {
            server.playersConnected.map { VelocityPlayerPlatformExecutor(platform, it) }
        } else {
            // Hide vanished players if executor does not have permission
            val vanishByPassPermission = platform.configuration.getSection(BehaviorSection::class.java).vanish.hideBypassPermission
            val canSeeVanished = onlyReachableBy.hasPermission(vanishByPassPermission)

            server.playersConnected
                .filter { canSeeVanished || !platform.playerManager.hasVanishState(it.uniqueId) }
                .map { VelocityPlayerPlatformExecutor(platform, it) }
        }
    }

}
