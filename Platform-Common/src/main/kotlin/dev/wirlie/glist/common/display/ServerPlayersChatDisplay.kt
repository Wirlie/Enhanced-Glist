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
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.translation.TranslationMessages
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.common.util.TextWidthUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import kotlin.math.ceil

/**
 * Display for connected players (server).
 * @param platform Platform instance.
 * @param serverGroup Server group to use for players list.
 * @param executor Command executor to filter vanished players if executor does not have permission to see vanished players.
 * @param audience Audience to send the result of this display.
 * @param playersPerPage Players to display per page.
 */
class ServerPlayersChatDisplay<S>(
    platform: Platform<S, *, *>,
    serverGroup: PlatformServerGroup<S>,
    executor: PlatformExecutor<S>,
    audience: Audience,
    playersPerPage: Int
): ServerPlayersAbstractDisplay<S>(
    platform, serverGroup, executor, audience, playersPerPage
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
        val totalPages = calculateTotalPages()

        val mainMessage = AdventureUtil.parseMiniMessage(
            AdventureUtil.groupListToString(messageToUse),
            TagResolver.resolver(
                "group-name", Tag.selfClosingInserting(Component.text(serverGroup.getName()))
            ),
            TagResolver.resolver(
                "server-name", Tag.selfClosingInserting(Component.text(serverGroup.getName()))
            ),
            TagResolver.resolver(
                "server-count", Tag.selfClosingInserting(Component.text("${serverGroup.getServers().size}"))
            ),
            TagResolver.resolver(
                "players-count", Tag.selfClosingInserting(Component.text("${serverGroup.getFilteredData(executor).provideData().size}"))
            ),
            TagResolver.resolver(
                "page-number", Tag.selfClosingInserting(Component.text("${page.pageNumber + 1}"))
            ),
            TagResolver.resolver(
                "total-pages", Tag.selfClosingInserting(Component.text("$totalPages"))
            ),
            TagResolver.resolver(
                "page-controller",
                Tag.selfClosingInserting(
                    pageControllerMessages.buildController(
                        page.hasPrevious,
                        page.hasNext,
                        "/$slistLabel ${(page.pageNumber + 1) - 1}",
                        "/$slistLabel ${(page.pageNumber + 1) + 1}",
                        page.pageNumber,
                        executor.isConsole()
                    )
                )
            ),
            TagResolver.resolver(
                "players-rows",
                Tag.selfClosingInserting(
                    makePlayersComponent(slistMessages.mainMessage.format, page.items)
                )
            )
        )

        if(executor.isConsole()) {
            // Insert a line break due console log prefix.
            audience.sendMessage(
                Component.newline()
                    .append(mainMessage)
            )
        } else {
            audience.sendMessage(mainMessage)
        }
    }

    /**
     * Utility function to make <players> component.
     * @param format Format configuration.
     * @param items Players to use to build component.
     * @return Adventure Component.
     */
    private fun makePlayersComponent(
        format: TranslationMessages.SlistMessages.MainMessage.FormatSection,
        items: List<PlatformExecutor<S>>
    ): Component {

        val columnsPerRow = platform.configuration.getSection(GeneralSection::class.java).playersPerRow
        val behaviorConfiguration = platform.configuration.getSection(BehaviorSection::class.java)
        val columnWidth = TextWidthUtil.lineMaxWidth / columnsPerRow
        var component = Component.empty()
        var rowComponent = Component.empty()
        var columnIndex = 0

        for(i in items.indices) {
            val player = items[i]
            val playerComponent = AdventureUtil.parseMiniMessage(
                format.players,
                TagResolver.resolver(
                    "player-prefix",
                    Tag.selfClosingInserting(platform.playerManager.getPrefix(player))
                ),
                TagResolver.resolver(
                    "player-name",
                    Tag.selfClosingInserting(AdventureUtil.legacySectionDeserialize(player.getName()))
                ),
                TagResolver.resolver(
                    "afk-status",
                    Tag.selfClosingInserting(
                        if(behaviorConfiguration.afk.enable && platform.playerManager.getAFKState(player) == true) {
                            AdventureUtil.parseMiniMessage(format.afkStatus)
                        } else {
                            Component.empty()
                        }
                    )
                ),
                TagResolver.resolver(
                    "vanish-status",
                    Tag.selfClosingInserting(
                        if(behaviorConfiguration.vanish.enable && platform.playerManager.getVanishState(player) == true) {
                            AdventureUtil.parseMiniMessage(format.vanishStatus)
                        } else {
                            Component.empty()
                        }
                    )
                )
            )

            val onlyString = AdventureUtil.legacySectionSerialize(playerComponent)
            val width = TextWidthUtil.estimateWidth(onlyString, TextWidthUtil.VERSION_113)

            if(columnIndex == 0 && width > TextWidthUtil.lineMaxWidth) {
                // String is longer than width of row
                component = component.append(
                    AdventureUtil.parseMiniMessage(
                        format.playersRows,
                        TagResolver.resolver(
                            "players",
                            Tag.selfClosingInserting(playerComponent)
                        )
                    )
                ).run {
                    if(i != items.size - 1) {
                        this.append(Component.newline())
                    } else {
                        this
                    }
                }
                continue
            }

            val columns = ceil(width / columnWidth.toDouble()).toInt()

            if(columnIndex + columns > columnsPerRow) {
                // Put current row in component
                component = component.append(
                    AdventureUtil.parseMiniMessage(
                        format.playersRows,
                        TagResolver.resolver(
                            "players",
                            Tag.selfClosingInserting(rowComponent)
                        )
                    )
                ).append(Component.newline())
                rowComponent = Component.empty()

                // Add next component
                columnIndex = if (columns >= columnsPerRow) {
                    component = component.append(
                        AdventureUtil.parseMiniMessage(
                            format.playersRows,
                            TagResolver.resolver(
                                "players",
                                Tag.selfClosingInserting(playerComponent)
                            )
                        )
                    ).run {
                        if(i != items.size - 1) {
                            this.append(Component.newline())
                        } else {
                            this
                        }
                    }
                    0
                } else {
                    rowComponent = rowComponent.append(fillSpaces(playerComponent, columnWidth, width))
                    // Adjust column index
                    columns
                }
            } else {
                // Add component to row
                rowComponent = rowComponent.append(fillSpaces(playerComponent, columnWidth, width))
                columnIndex += columns
            }
        }

        if(columnIndex != 0) {
            // Add remaining row
            component = component.append(
                AdventureUtil.parseMiniMessage(
                    format.playersRows,
                    TagResolver.resolver(
                        "players",
                        Tag.selfClosingInserting(rowComponent)
                    )
                )
            )
        }

        return component
    }

    /**
     * Utility function to fill remaining column width with spaces.
     * @param component Column Component.
     * @param columnWidth Max width of column.
     * @param currentWidth Current used width of column.
     * @return Component filled by spaces.
     */
    private fun fillSpaces(component: Component, columnWidth: Int, currentWidth: Int): Component {
        val spaceWidth = TextWidthUtil.estimateWidth(' ', TextWidthUtil.VERSION_113) + 1
        val spacesToAdd = (columnWidth - currentWidth) / spaceWidth
        val string = StringBuilder()

        for(i in 0 until spacesToAdd) {
            string.append(' ')
        }

        if(string.toString().isEmpty()) {
            return component
        }

        return component.append(Component.text(string.toString()))
    }

}
