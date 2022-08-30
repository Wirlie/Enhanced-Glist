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

package dev.wirlie.glist.common.translation

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
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

    private var translationMessages: TranslationMessages

    init {
        if(!file.exists()) {
            saveDefaultFile()
        }

        val customFactory: ObjectMapper.Factory = ObjectMapper.factoryBuilder().build()

        yamlLoader = YamlConfigurationLoader.builder()
            .path(file.toPath())
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers { build: TypeSerializerCollection.Builder ->
                    build.registerAnnotatedObjects(customFactory)
                    build.register(IntRange::class.java, IntRangeSerializer())
                }
            }
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build()

        yamlConfiguration = yamlLoader.load()

        translationMessages = yamlConfiguration.get(TranslationMessages::class.java)!!
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

    fun getMessages(): TranslationMessages {
        return translationMessages
    }

}
