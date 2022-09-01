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
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class ServerPlayersDisplay<S>(
    val platform: Platform<S, *, *>,
    val serverGroup: PlatformServerGroup<S>,
    audience: Audience,
    initialPageSize: Int
): PageDisplay<PlatformExecutor<S>>(
    audience,
    initialPageSize,
    serverGroup.getPlayers().toMutableList()
) {

    override fun buildPageDisplay(page: Page<PlatformExecutor<S>>) {
        val slistMessages = platform.translatorManager.getTranslator().getMessages().slist
        val slistLabel = platform.configuration.getSection(CommandsSection::class.java).slist.label

        val messageToUse = if(serverGroup.byConfiguration) {
            slistMessages.mainMessage.group
        } else {
            slistMessages.mainMessage.server
        }

        val pageControllerMessages = slistMessages.pageController

        audience.sendMessage(
            AdventureUtil.parseMiniMessage(
                AdventureUtil.groupListToString(messageToUse),
                TagResolver.resolver(
                    "group-name", Tag.selfClosingInserting(Component.text(serverGroup.name))
                ),
                TagResolver.resolver(
                    "server-name", Tag.selfClosingInserting(Component.text(serverGroup.name))
                ),
                TagResolver.resolver(
                    "server-count", Tag.selfClosingInserting(Component.text("${serverGroup.getServers().size}"))
                ),
                TagResolver.resolver(
                    "players-count", Tag.selfClosingInserting(Component.text("${serverGroup.getPlayers().size}"))
                ),
                TagResolver.resolver(
                    "page-number", Tag.selfClosingInserting(Component.text("${page.pageNumber + 1}"))
                ),
                TagResolver.resolver(
                    "total-pages", Tag.selfClosingInserting(Component.text("${page.totalPages}"))
                ),
                TagResolver.resolver(
                    "page-controller",
                    Tag.selfClosingInserting(
                        pageControllerMessages.buildController(
                            page.hasPrevious,
                            page.hasNext,
                            "/$slistLabel ${(page.pageNumber + 1) - 1}",
                            "/$slistLabel ${(page.pageNumber + 1) + 1}",
                            page.pageNumber
                        )
                    )
                )
            )
        )
    }

}
