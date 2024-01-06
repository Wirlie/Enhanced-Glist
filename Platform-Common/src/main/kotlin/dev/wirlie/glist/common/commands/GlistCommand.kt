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

package dev.wirlie.glist.common.commands

import com.github.benmanes.caffeine.cache.Caffeine
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.display.ServersListDisplay
import dev.wirlie.glist.common.display.ServersListGUIDisplay
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Implementation for /glist command.
 * @param platform Platform instance.
 * @param name Label of command.
 * @param aliases Aliases of command.
 * @param permission Permission required to execute command.
 */
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

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        val display = getDisplayFor(executor)
        val audience = executor.asAudience()

        if(display.dataProvider.provideData().isEmpty()) {
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

        val totalPages = display.calculateTotalPages()
        if(page >= totalPages) {
            page = totalPages - 1
        }

        if(page < 0) {
            page = 0
        }

        display.showPage(page)
    }

    /**
     * Create a Display instance if not cached.
     * @param executor Command Executor.
     * @return Display instance.
     */
    private fun getDisplayFor(executor: PlatformExecutor<S>): PageDisplay<PlatformServerGroup<S>> {
        val servers = platform.getAllServersGrouped(executor)
            .sortedWith(compareByDescending<PlatformServerGroup<S>> { it.getPlayersFiltered(executor).provideData().size }.thenBy { it.getName() })
            .toMutableList()

        val newDisplay = if(
            !platform.guiSystemEnabled || // GUI System not enabled
            !platform.configuration.getSection(CommandsSection::class.java).glist.useGuiMenu ||
            executor.isConsole() // Console is not compatible with GUI
        ) {
            ServersListDisplay(
                platform,
                executor,
                executor.asAudience(),
                platform.configuration.getSection(GeneralSection::class.java).serversPerPage,
                servers
            )
        } else {
            var rows = platform.guiManager!!.glistConfig.rows

            if(rows == -1) {
                // Calculate manually
                rows = ceil(servers.size / 9.0).toInt()
            }

            rows = min(max(rows, 2), 6)

            ServersListGUIDisplay(
                platform,
                executor,
                executor.asAudience(),
                servers,
                rows
            )
        }

        return newDisplay
    }

    override fun handleTabCompletion(executor: PlatformExecutor<S>, args: Array<String>): List<String> {
        return listOf()
    }

}
