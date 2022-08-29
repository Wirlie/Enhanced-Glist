package dev.wirlie.glist.common.translation

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Files

class Translator(
    val platform: Platform<*, *, *>,
    private val code: String
) {

    private val fileName = "$code.yml"
    private val file = File(platform.pluginFolder, fileName)
    private val yamlConfiguration: ConfigurationNode
    private val yamlLoader: YamlConfigurationLoader

    init {
        if(!file.exists()) {
            saveDefaultFile()
        }

        yamlLoader = YamlConfigurationLoader.builder()
            .path(file.toPath())
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build()
        yamlConfiguration = yamlLoader.load()
    }

    private fun saveDefaultFile() {
        if(!file.parentFile.exists()) {
            Files.createDirectories(file.parentFile.toPath())
        }

        var input = this::class.java.getResourceAsStream("/messages/$fileName")

        if(input == null) {
            // Fallback to english...
            platform.logger.warning(Component.text("Unknown language code '$code'.", NamedTextColor.YELLOW))
            platform.logger.warning(Component.text("New file generated '$code.yml', you can edit this file and make your own translation.", NamedTextColor.YELLOW))
            platform.logger.warning(Component.text("If this is not intentional, please read the documentation to view the list of supported languages.", NamedTextColor.YELLOW))
            input = this::class.java.getResourceAsStream("/messages/en.yml")!!
        }

        Files.copy(input, file.toPath())
    }

    fun getGlistMessages(): GlistMessages {
        return yamlConfiguration.node("glist").get(GlistMessages::class.java)!!
    }

    @ConfigSerializable
    class GlistMessages {

        var mainMessage: List<String> = mutableListOf()

        var noServersToDisplay: String = ""

        var pageController: PageController = PageController()

        var serversFormat = ServersFormat()

        @ConfigSerializable
        class ServersFormat {

            var template: String = ""

            var bars: Map<String, ConfigurationNode> = mutableMapOf()

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

        @ConfigSerializable
        class Format {

            var previousAndNext = ""

            var previousOnly = ""

            var nextOnly = ""

        }

        fun buildController(hasPrevious: Boolean, hasNext: Boolean, previousCommand: String, nextCommand: String, pageNumber: Int): Component {
            return if(hasPrevious && hasNext) {
                buildPreviousAndNextController(previousCommand, nextCommand, pageNumber)
            } else if(hasPrevious) {
                buildPreviousOnlyController(previousCommand, pageNumber)
            } else {
                buildNextOnlyController(nextCommand, pageNumber)
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
                TagResolver.resolver("next-page-phrase", Tag.selfClosingInserting(Component.text(previousPagePhrase)))
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(nextPageHoverMessageNoNextPage)
                )
            )
        }

    }

}
