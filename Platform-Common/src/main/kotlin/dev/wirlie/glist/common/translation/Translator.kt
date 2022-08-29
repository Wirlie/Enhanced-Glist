package dev.wirlie.glist.common.translation

import dev.wirlie.glist.common.Platform
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
        class PageController {

            var previousPageHoverMessage = ""

            var previousPageHoverNoPreviousPage = ""

            var nextPageHoverMessage = ""

            var nextPageHoverNoNextPage = ""

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

        }

        @ConfigSerializable
        class ServersFormat {

            var template: String = ""

            var bars: Map<String, ConfigurationNode> = mutableMapOf()

        }

    }

}
