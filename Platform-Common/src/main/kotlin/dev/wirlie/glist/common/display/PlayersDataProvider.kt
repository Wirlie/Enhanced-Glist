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

package dev.wirlie.glist.common.display

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.BehaviorSection
import dev.wirlie.glist.common.pageable.FilteredDataProvider
import dev.wirlie.glist.common.platform.PlatformExecutor

class PlayersDataProvider<S>(
    val executor: PlatformExecutor<S>,
    val platform: Platform<S,*,*>,
    data: List<PlatformExecutor<S>>
): FilteredDataProvider<PlatformExecutor<S>>(
    data
) {

    override fun applyFilter(element: PlatformExecutor<S>): Boolean {
        val configuration = platform.configuration.getSection(BehaviorSection::class.java)

        return if (
            !configuration.vanish.enable /* Vanish is not enabled */ ||
            executor.isConsole() /* Console always have permission */
        ) {
            true
        } else {
            // Hide vanished players if executor does not have permission
            val vanishBypassPermission = configuration.vanish.hideBypassPermission
            val canSeeVanished = !configuration.vanish.hideVanishedUsers || executor.hasPermission(vanishBypassPermission)

            canSeeVanished || !platform.playerManager.hasVanishState(element.getUUID())
        }
    }

}
