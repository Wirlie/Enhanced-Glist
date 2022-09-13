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
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.translation.TranslationMessages
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.common.util.TextWidthUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import kotlin.math.ceil

/**
 * Display for connected players (server).
 * @param platform Platform instance.
 * @param serverGroup Server group to use for players list.
 * @param executor Command executor to filter vanished players if executor does not have permission to see vanished players.
 * @param audience Audience to send the result of this display.
 * @param playersPerPage Players to display per page.
 */
class ServerPlayersGUIDisplay<S>(
    platform: Platform<S, *, *>,
    serverGroup: PlatformServerGroup<S>,
    executor: PlatformExecutor<S>,
    audience: Audience,
    playersPerPage: Int
): ServerPlayersAbstractDisplay<S>(
    platform, serverGroup, executor, audience, playersPerPage
) {

    override fun buildPageDisplay(page: Page<PlatformExecutor<S>>) {
        for(i in page.items.indices) {
            val player = page.items[i]
        }
    }

}
