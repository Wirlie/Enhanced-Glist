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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.configuration.sections.IgnoreServersSection
import dev.wirlie.glist.common.display.PlayersDataProvider
import dev.wirlie.glist.common.display.ServerPlayersAbstractDisplay
import dev.wirlie.glist.common.display.ServerPlayersChatDisplay
import dev.wirlie.glist.common.display.ServerPlayersGUIDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Implementation for /slist command.
 * @param platform Platform instance.
 * @param name Label of command.
 * @param aliases Aliases of command.
 * @param permission Permission required to execute command.
 */
class SlistCommand<S>(
    val platform: Platform<S, *, *>,
    name: String,
    aliases: MutableList<String>,
    permission: String
): PlatformCommand<S>(
    name,
    aliases,
    permission
) {

    private var cache: Cache<String, ServerPlayersAbstractDisplay<S>>? = null

    init {
        platform.configuration.getSection(GeneralSection::class.java).also { config ->
            if(config.cache.serverPlayers.enable) {
                var time = config.cache.serverPlayers.time.toLong()

                if(time < 1) {
                    time = 1
                }

                cache = Caffeine.newBuilder()
                    .expireAfterWrite(time, TimeUnit.SECONDS)
                    .build()
            }
        }
    }

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        val audience = executor.asAudience()
        val translation = platform.translatorManager.getTranslator().getMessages()
        val configuration = platform.configuration.getSection(CommandsSection::class.java)

        var server: PlatformServerGroup<S>? = null

        if(args.isEmpty()) {
            // Determine if current server is listable by EnhancedGlist
            val currentServer = executor.getConnectedServer()

            if(currentServer == null) {
                // Weird, this should never happen
                audience.sendMessage(
                    AdventureUtil.parseMiniMessage(
                        translation.slist.usage,
                        TagResolver.resolver(
                            "slist-label",
                            Tag.selfClosingInserting(Component.text(configuration.slist.label))
                        )
                    )
                )
                return
            }

            // Should list this server? Or ignore it?
            val ignoreServersConfiguration = platform.configuration.getSection(IgnoreServersSection::class.java)
            if(ignoreServersConfiguration.shouldIgnore(currentServer.getName())) {
                // Ignore server
                audience.sendMessage(
                    AdventureUtil.parseMiniMessage(
                        translation.slist.cannotFindServer,
                        TagResolver.resolver(
                            "server-name",
                            Tag.selfClosingInserting(Component.text(args[0]))
                        )
                    )
                )
                return
            }

            // Ok, current this server to list players
            server = PlatformServerGroup(platform, currentServer.getName(), listOf(currentServer), false)
        }

        if(server == null) {
            // If null, then player has used [/slist <server>] usage.
            server = platform.getServerGrouped(args[0])
        }

        if(server == null) {
            // Server not found
            audience.sendMessage(
                AdventureUtil.parseMiniMessage(
                    translation.slist.cannotFindServer,
                    TagResolver.resolver(
                        "server-name",
                        Tag.selfClosingInserting(Component.text(args[0]))
                    )
                )
            )
            return
        }

        if(server.getPlayers().isEmpty()) {
            // No players in server
            audience.sendMessage(
                AdventureUtil.parseMiniMessage(
                    translation.slist.serverNoPlayers
                )
            )
            return
        }

        val display = getDisplayFor(executor, server)

        if(display.dataProvider.provideData().isEmpty()) {
            // No players in server (vanished)
            audience.sendMessage(
                AdventureUtil.parseMiniMessage(
                    translation.slist.serverNoPlayers
                )
            )
            return
        }

        var page = ((if(args.size > 1) args[1].toIntOrNull() else null) ?: 1) - 1
        val totalPages = display.calculateTotalPages()

        if(page < 0) {
            page = 0
        } else if(page >= totalPages) {
            page = max(totalPages - 1, 0)
        }

        display.showPage(page)
    }

    private fun getDisplayFor(executor: PlatformExecutor<S>, server: PlatformServerGroup<S>): ServerPlayersAbstractDisplay<S> {

        fun makeDisplay(executor: PlatformExecutor<S>): ServerPlayersAbstractDisplay<S> {
            if(
                !platform.guiSystemEnabled || // GUI System not enabled
                !platform.configuration.getSection(CommandsSection::class.java).slist.useGuiMenu ||
                executor.isConsole() // Console is not compatible with GUI
            ) {
                return ServerPlayersChatDisplay(
                    platform,
                    server,
                    executor,
                    executor.asAudience(),
                    platform.configuration.getSection(GeneralSection::class.java).playersPerPage
                )
            } else {
                var rows = platform.guiManager!!.slistConfig.rows

                if(rows == -1) {
                    // Calculate manually
                    val temporalProvider = PlayersDataProvider(executor, platform, server.getPlayers())
                    val data = temporalProvider.provideData()
                    rows = ceil(data.size / 9.0).toInt()
                }

                rows = min(max(rows, 2), 6)

                return ServerPlayersGUIDisplay(
                    platform,
                    server,
                    executor,
                    executor.asAudience(),
                    rows
                )
            }
        }

        if(cache == null) {
            // Cache disabled, always make a new instance.
            return makeDisplay(executor)
        }

        val key = if (executor.isConsole()) "console" else "player-${executor.getUUID()}"
        val current = cache!!.getIfPresent(key)

        if(current != null && current.serverGroup == server) {
            // Cache exists
            return current
        }

        // Cache not exists, or exists for another server...
        val newDisplay = makeDisplay(executor)
        cache!!.put(key, newDisplay)

        return newDisplay
    }

    override fun handleTabCompletion(executor: PlatformExecutor<S>, args: Array<String>): List<String> {
        if(!executor.hasPermission(permission)) {
            // Do not make suggestions if player doest not have permission to use this command
            return listOf()
        }

        return platform.getAllServersGrouped(executor).map { it.getName().lowercase() }.filter { args.isEmpty() || it.contains(args[0], true) }
    }

}
