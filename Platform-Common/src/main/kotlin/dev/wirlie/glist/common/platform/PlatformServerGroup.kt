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

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.GeneralSection

class PlatformServerGroup<S>(
    private val originalName: String,
    private val servers: List<PlatformServer<S>>,
    val byConfiguration: Boolean = true
) {

    fun getName(): String {
        val upperCase = Platform.unsafeInstance.configuration.getSection(GeneralSection::class.java).displayServerNameUppercase

        return if(upperCase) {
            originalName.uppercase()
        } else {
            originalName
        }
    }

    fun getServersCount() = servers.size

    fun getServers() = servers.toList()

    fun getPlayers(
        onlyReachableBy: PlatformExecutor<S>? = null
    ): List<PlatformExecutor<S>> {
        return servers.flatMap { it.getPlayers(onlyReachableBy) }
    }

    fun getPlayersCount(
        onlyReachableBy: PlatformExecutor<S>? = null
    ): Int {
        return servers.sumOf { it.getPlayers(onlyReachableBy).size }
    }

}
