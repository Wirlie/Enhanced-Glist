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

package dev.wirlie.glist.common.commands

import com.github.benmanes.caffeine.cache.Caffeine
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.display.ServersListDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.util.AdventureUtil
import java.util.concurrent.TimeUnit

class GlistCommand<S>(
    val platform: Platform<S, *, *>,
    name: String,
    aliases: MutableList<String>,
    permission: String
): PlatformCommand<S>(
    name,
    aliases,
    permission
) {

    private val cache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<String, ServersListDisplay<S>>()

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        val display = getDisplayFor(executor)
        val audience = executor.asAudience()

        if(display.data.isEmpty()) {
            audience.sendMessage(
                AdventureUtil.parseMiniMessage(
                    platform.translatorManager.getTranslator().getMessages().glist.noServersToDisplay
                )
            )
            return
        }

        var page = if(args.isEmpty()) {
            0
        } else {
            (args[0].toIntOrNull() ?: 1) - 1
        }

        if(page >= display.totalPages) {
            page = display.totalPages - 1
        }

        if(page < 0) {
            page = 0
        }

        display.showPage(page)
    }

    private fun getDisplayFor(executor: PlatformExecutor<S>): ServersListDisplay<S> {
        val key = if (executor.isConsole()) "console" else "player-${executor.getUUID()}"
        val current = cache.getIfPresent(key)

        if(current != null) {
            return current
        }

        val newDisplay = ServersListDisplay(
            platform,
            executor.asAudience(),
            platform.configuration.getSection(GeneralSection::class.java)?.serversPerPage ?: 5,
            platform.getAllServers().sortedWith(compareByDescending<PlatformServer<S>> { it.getPlayers().size }.thenBy { it.getName() }).toMutableList()
        )

        cache.put(key, newDisplay)

        return newDisplay
    }

}
