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

package dev.wirlie.glist.common.commands

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.display.ServerPlayersDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.concurrent.TimeUnit
import kotlin.math.max

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

    private var cache: Cache<String, ServerPlayersDisplay<S>>? = null

    init {
        platform.configuration.getSection(GeneralSection::class.java).also { config ->
            if(config.cache.serverPlayers.enable) {
                var time = config.cache.serverPlayers.time.toLong()

                if(time < 1) {
                    time = 1
                }

                cache = Caffeine.newBuilder()
                    .expireAfterAccess(time, TimeUnit.SECONDS)
                    .build()
            }
        }
    }

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        val audience = executor.asAudience()
        val translation = platform.translatorManager.getTranslator().getMessages()
        val configuration = platform.configuration.getSection(CommandsSection::class.java)

        if(args.isEmpty()) {
            // Wrong usage
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

        val server = platform.getServerGrouped(args[0])

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
        var page = ((if(args.size > 1) args[1].toIntOrNull() else null) ?: 1) - 1

        if(page < 0) {
            page = 0
        } else if(page >= display.totalPages) {
            page = max(display.totalPages - 1, 0)
        }

        display.showPage(page)
    }

    private fun getDisplayFor(executor: PlatformExecutor<S>, server: PlatformServerGroup<S>): ServerPlayersDisplay<S> {

        fun makeDisplay(): ServerPlayersDisplay<S> {
            return ServerPlayersDisplay(
                platform,
                server,
                executor.asAudience(),
                platform.configuration.getSection(GeneralSection::class.java).playersPerPage
            )
        }

        if(cache == null) {
            // Cache disabled, always make a new instance.
            return makeDisplay()
        }

        val key = if (executor.isConsole()) "console" else "player-${executor.getUUID()}"
        val current = cache!!.getIfPresent(key)

        if(current != null && current.serverGroup == server) {
            // Cache exists
            return current
        }

        // Cache not exists, or exists for another server...
        val newDisplay = makeDisplay()
        cache!!.put(key, newDisplay)

        return newDisplay
    }

}
