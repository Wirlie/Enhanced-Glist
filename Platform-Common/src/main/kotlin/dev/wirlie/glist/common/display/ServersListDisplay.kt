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

package dev.wirlie.glist.common.display

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class ServersListDisplay<S>(
    val platform: Platform<S, *, *>,
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformServer<S>> = mutableListOf()
): PageDisplay<PlatformServer<S>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun buildPageDisplay(page: Page<PlatformServer<S>>) {
        val glistMessages = platform.translatorManager.getTranslator().getMessages().glist
        val pageControllerMessages = glistMessages.pageController
        val serversFormat = glistMessages.serversFormat
        val glistLabel = platform.configuration.getSection(CommandsSection::class.java)?.glist?.label ?: "glist"
        val slistLabel = platform.configuration.getSection(CommandsSection::class.java)?.slist?.label ?: "slist"

        val mainMessage = AdventureUtil.parseMiniMessage(
            AdventureUtil.groupListToString(
                glistMessages.mainMessage
            ),
            TagResolver.resolver(
                "page-number", Tag.selfClosingInserting(Component.text("${page.pageNumber + 1}"))
            ),
            TagResolver.resolver(
                "total-pages", Tag.selfClosingInserting(Component.text("${page.totalPages}"))
            ),
            TagResolver.resolver(
                "players-amount", Tag.selfClosingInserting(Component.text("${platform.getConnectedPlayersAmount()}"))
            ),
            TagResolver.resolver(
                "slist-label", Tag.selfClosingInserting(Component.text(slistLabel))
            ),
            TagResolver.resolver(
                "servers",
                Tag.selfClosingInserting(
                    serversFormat.buildServersComponent(platform, page.items)
                )
            ),
            TagResolver.resolver(
                "page-controller",
                Tag.selfClosingInserting(
                    pageControllerMessages.buildController(
                        page.hasPrevious,
                        page.hasNext,
                        "/$glistLabel ${(page.pageNumber + 1) - 1}",
                        "/$glistLabel ${(page.pageNumber + 1) + 1}",
                        page.pageNumber
                    )
                )
            )
        )

        audience.sendMessage(mainMessage)
    }

}
