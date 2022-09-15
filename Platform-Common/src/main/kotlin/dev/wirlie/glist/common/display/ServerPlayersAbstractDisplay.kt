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
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import net.kyori.adventure.audience.Audience

abstract class ServerPlayersAbstractDisplay<S>(
    val platform: Platform<S, *, *>,
    val serverGroup: PlatformServerGroup<S>,
    val executor: PlatformExecutor<S>,
    audience: Audience,
    playersPerPage: Int
) : PageDisplay<PlatformExecutor<S>>(
    audience,
    playersPerPage,
    PlayersDataProvider(executor, platform, serverGroup.getPlayers().toMutableList().also {
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
        it.addAll(it)
    })
)
