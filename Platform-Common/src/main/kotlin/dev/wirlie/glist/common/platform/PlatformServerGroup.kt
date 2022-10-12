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
import dev.wirlie.glist.common.display.PlayersDataProvider

class PlatformServerGroup<S>(
    private val platform: Platform<S, *, *>,
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

    /**
     * Get total number of servers of this group.
     * @return Server count of this group.
     */
    fun getServersCount() = servers.size

    /**
     * @return Servers of this group.
     */
    fun getServers() = servers.toList()

    /**
     * Get players connected to this server group.
     */
    fun getPlayers(): List<PlatformExecutor<S>> {
        return servers.flatMap { it.getPlayers() }
    }

    fun getFilteredData(executor: PlatformExecutor<S>): PlayersDataProvider<S> {
        return PlayersDataProvider(executor, platform, getPlayers())
    }

    fun fakePlayerCountForTest() = 0

}
