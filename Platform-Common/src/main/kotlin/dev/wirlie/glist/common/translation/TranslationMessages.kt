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

package dev.wirlie.glist.common.translation

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class TranslationMessages {

    var doNotEditThis: DoNotEditShitSection = DoNotEditShitSection()

    var glist: GlistMessages = GlistMessages()

    var slist: SlistMessages = SlistMessages()

    var egl: EglMessages = EglMessages()

    var gui: GuiMessages = GuiMessages()

    @ConfigSerializable
    class DoNotEditShitSection {

        var configVersion: Int = -1

    }

    @ConfigSerializable
    class GlistMessages {

        var mainMessage: MutableList<String> = mutableListOf()

        var noServersToDisplay: String = ""

        var pageController: PageController = PageController()

        var serversFormat = ServersFormat()

        @ConfigSerializable
        class ServersFormat {

            var clickToShowPlayersHoverMessage = ""

            var template: String = ""

            var bars: Map<IntRange, ConfigurationNode> = mutableMapOf()

            fun buildServersComponent(platform: Platform<*, *, *>, servers: List<PlatformServerGroup<*>>): Component {

                var component = Component.empty()

                for((index, server) in servers.withIndex()) {
                    val playerAmount = server.getPlayers().size
                    val totalPlayers = platform.getConnectedPlayersAmount()
                    val percent = if(totalPlayers == 0) 0.0 else playerAmount * 100.0 / platform.getConnectedPlayersAmount()
                    var barsToUse = "<dark_gray>???????????????</dark_gray>"

                    for(entry in bars.entries) {
                        if(playerAmount == 0 || percent.toInt() == 0) {
                            if (entry.key.contains(0)) {
                                barsToUse = entry.value.string!!
                                break
                            }
                        } else {
                            if(entry.key.contains(percent.toInt())) {
                                barsToUse = entry.value.string!!
                                break
                            }
                        }
                    }

                    val slistLabel = platform.configuration.getSection(CommandsSection::class.java).slist.label

                    component = component
                        .append(
                            AdventureUtil.parseMiniMessage(
                                template,
                                TagResolver.resolver(
                                    "server-name",
                                    Tag.selfClosingInserting(Component.text(server.getName()))
                                ),
                                TagResolver.resolver(
                                    "player-amount",
                                    Tag.selfClosingInserting(Component.text(playerAmount))
                                ),
                                TagResolver.resolver(
                                    Formatter.number("percent", percent)
                                ),
                                TagResolver.resolver(
                                    "bars",
                                    Tag.selfClosingInserting(
                                        AdventureUtil.parseMiniMessage(
                                            barsToUse
                                        )
                                    )
                                ),
                            ).hoverEvent(
                                HoverEvent.showText(
                                    AdventureUtil.parseMiniMessage(
                                        clickToShowPlayersHoverMessage,
                                        TagResolver.resolver(
                                            "server-name",
                                            Tag.selfClosingInserting(Component.text(server.getName()))
                                        )
                                    )
                                )
                            ).clickEvent(
                                ClickEvent.runCommand("/$slistLabel ${server.getName()}")
                            )
                        )
                        .run {
                            if(index != (servers.size - 1)) {
                                this.append(Component.newline())
                            } else {
                                this
                            }
                        }
                }

                return component
            }

        }

    }

    @ConfigSerializable
    class SlistMessages {

        var mainMessage = MainMessage()

        var serverNoPlayers = ""

        var cannotFindServer = ""

        var usage = ""

        var pageController = PageController()

        @ConfigSerializable
        class MainMessage {

            val server = mutableListOf<String>()

            val group = mutableListOf<String>()

            val format = FormatSection()

            @ConfigSerializable
            class FormatSection {

                val playersRows = ""

                val players = ""

                val afkStatus = ""

                val vanishStatus = ""

            }

        }

    }

    @ConfigSerializable
    class PageController {

        var previousPageHoverMessage = ""

        var previousPageHoverMessageNoPreviousPage = ""

        var nextPageHoverMessage = ""

        var nextPageHoverMessageNoNextPage = ""

        var previousPagePhrase = ""

        var previousPageController = ""

        var previousPageControllerDisabled = ""

        var nextPagePhrase = ""

        var nextPageController = ""

        var nextPageControllerDisabled = ""

        var format = Format()

        var console = ConsoleMessages()

        @ConfigSerializable
        class Format {

            var previousAndNext = ""

            var previousOnly = ""

            var nextOnly = ""

            var disabled = ""

        }

        @ConfigSerializable
        class ConsoleMessages {

            var previousPage = ""

            var nextPage = ""

        }

        fun buildController(
            hasPrevious: Boolean,
            hasNext: Boolean,
            previousCommand: String,
            nextCommand: String,
            pageNumber: Int,
            fromConsole: Boolean
        ): Component {
            if(fromConsole) {
                return if (hasPrevious && hasNext) {
                    Component.empty()
                        .append(buildPreviousControllerForConsole(previousCommand))
                        .append(Component.newline())
                        .append(buildNextControllerForConsole(nextCommand))
                } else if (hasPrevious) {
                    buildPreviousControllerForConsole(previousCommand)
                } else if (hasNext) {
                    buildNextControllerForConsole(nextCommand)
                } else {
                    Component.empty()
                }
            } else {
                return if (hasPrevious && hasNext) {
                    buildPreviousAndNextController(previousCommand, nextCommand, pageNumber)
                } else if (hasPrevious) {
                    buildPreviousOnlyController(previousCommand, pageNumber)
                } else if (hasNext) {
                    buildNextOnlyController(nextCommand, pageNumber)
                } else {
                    buildDisabledController()
                }
            }
        }

        private fun buildPreviousAndNextController(previousCommand: String, nextCommand: String, pageNumber: Int): Component {
            return AdventureUtil.parseMiniMessage(
                format.previousAndNext,
                TagResolver.resolver("previous-page-controller", Tag.selfClosingInserting(buildPreviousControllerEnabled(previousCommand, pageNumber))),
                TagResolver.resolver("next-page-controller", Tag.selfClosingInserting(buildNextControllerEnabled(nextCommand, pageNumber))),
            )
        }

        private fun buildPreviousOnlyController(previousCommand: String, pageNumber: Int): Component {
            return AdventureUtil.parseMiniMessage(
                format.previousOnly,
                TagResolver.resolver("previous-page-controller", Tag.selfClosingInserting(buildPreviousControllerEnabled(previousCommand, pageNumber))),
                TagResolver.resolver("next-page-controller-disabled", Tag.selfClosingInserting(buildNextControllerDisabled())),
            )
        }

        private fun buildNextOnlyController(nextCommand: String, pageNumber: Int): Component {
            return AdventureUtil.parseMiniMessage(
                format.nextOnly,
                TagResolver.resolver("previous-page-controller-disabled", Tag.selfClosingInserting(buildPreviousControllerDisabled())),
                TagResolver.resolver("next-page-controller", Tag.selfClosingInserting(buildNextControllerEnabled(nextCommand, pageNumber))),
            )
        }

        private fun buildDisabledController(): Component {
            return AdventureUtil.parseMiniMessage(
                format.disabled,
                TagResolver.resolver("previous-page-controller-disabled", Tag.selfClosingInserting(buildPreviousControllerDisabled())),
                TagResolver.resolver("next-page-controller-disabled", Tag.selfClosingInserting(buildNextControllerDisabled())),
            )
        }

        private fun buildPreviousControllerEnabled(command: String, pageNumber: Int): Component {
            return AdventureUtil.parseMiniMessage(
                previousPageController,
                TagResolver.resolver("previous-page-phrase", Tag.selfClosingInserting(Component.text(previousPagePhrase)))
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(
                        previousPageHoverMessage,
                        TagResolver.resolver("page-number", Tag.selfClosingInserting(Component.text("${(pageNumber + 1) - 1}")))
                    )
                )
            ).clickEvent(
                ClickEvent.runCommand(command)
            )
        }

        private fun buildPreviousControllerDisabled(): Component {
            return AdventureUtil.parseMiniMessage(
                previousPageControllerDisabled,
                TagResolver.resolver("previous-page-phrase", Tag.selfClosingInserting(Component.text(previousPagePhrase)))
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(previousPageHoverMessageNoPreviousPage)
                )
            )
        }

        private fun buildNextControllerEnabled(command: String, pageNumber: Int): Component {
            return AdventureUtil.parseMiniMessage(
                nextPageController,
                TagResolver.resolver("next-page-phrase", Tag.selfClosingInserting(Component.text(nextPagePhrase)))
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(
                        nextPageHoverMessage,
                        TagResolver.resolver("page-number", Tag.selfClosingInserting(Component.text("${(pageNumber + 1) + 1}")))
                    )
                )
            ).clickEvent(
                ClickEvent.runCommand(command)
            )
        }

        private fun buildNextControllerDisabled(): Component {
            return AdventureUtil.parseMiniMessage(
                nextPageControllerDisabled,
                TagResolver.resolver("next-page-phrase", Tag.selfClosingInserting(Component.text(nextPagePhrase)))
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(nextPageHoverMessageNoNextPage)
                )
            )
        }

        private fun buildPreviousControllerForConsole(command: String): Component {
            return AdventureUtil.parseMiniMessage(
                console.previousPage,
                TagResolver.resolver(
                    "command",
                    Tag.selfClosingInserting(Component.text(command))
                )
            )
        }

        private fun buildNextControllerForConsole(command: String): Component {
            return AdventureUtil.parseMiniMessage(
                console.nextPage,
                TagResolver.resolver(
                    "command",
                    Tag.selfClosingInserting(Component.text(command))
                )
            )
        }

    }

    @ConfigSerializable
    class EglMessages {

        var usage = mutableListOf<String>()

        var pluginReloaded = ""

    }

    @ConfigSerializable
    class GuiMessages {

        var closedByReload = ""

    }

}
