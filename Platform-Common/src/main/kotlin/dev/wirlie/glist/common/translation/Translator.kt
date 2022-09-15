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
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import dev.wirlie.glist.common.configurate.RegexSerializer
import dev.wirlie.glist.common.util.ConfigurateUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.io.File
import java.nio.file.Files

class Translator(
    val platform: Platform<*, *, *>,
    val code: String
) {

    private val fileName = "$code.conf"
    private val configurationFile = File(platform.pluginFolder, fileName)
    private val configuration: ConfigurationNode
    private val configurationLoader: HoconConfigurationLoader
    var realCode = code

    private var translationMessages: TranslationMessages

    init {
        if(!configurationFile.exists()) {
            saveDefaultFile()
        }

        val customFactory: ObjectMapper.Factory = ObjectMapper.factoryBuilder().build()

        configurationLoader = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(configurationFile.toPath())
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers { build: TypeSerializerCollection.Builder ->
                    build.registerAnnotatedObjects(customFactory)
                    build.register(IntRange::class.java, IntRangeSerializer())
                    build.register(Regex::class.java, RegexSerializer())
                }
            }
            .build()

        configuration = configurationLoader.load()
        translationMessages = configuration.get(TranslationMessages::class.java)!!

        applyUpdates()
    }

    private fun saveDefaultFile() {
        if(!configurationFile.parentFile.exists()) {
            Files.createDirectories(configurationFile.parentFile.toPath())
        }

        var input = this::class.java.getResourceAsStream("/messages/$fileName")

        if(input == null) {
            // Fallback to english...
            platform.logger.warning(Component.text("Unknown language code '$code' (/messages/$fileName).", NamedTextColor.YELLOW))
            platform.logger.warning(Component.text("New file generated '$code.conf', you can edit this file and make your own translation.", NamedTextColor.YELLOW))
            platform.logger.warning(Component.text("If this is not intentional, please read the documentation to view the list of supported languages.", NamedTextColor.YELLOW))
            input = this::class.java.getResourceAsStream("/messages/en.conf")!!
            realCode = "en"
        }

        Files.copy(input, configurationFile.toPath())
    }

    private fun applyUpdates() {
        val temporalFile = File(platform.pluginFolder, "$code-temp.conf")

        if(temporalFile.exists()) {
            Files.delete(temporalFile.toPath())
        }

        var input = this::class.java.getResourceAsStream("/messages/$fileName")

        if(input == null) {
            // Fallback to english
            input = this::class.java.getResourceAsStream("/messages/en.conf")!!
        }

        Files.copy(input, temporalFile.toPath())

        val newConfig = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(temporalFile.toPath())
            .build()
            .load()

        if(newConfig.node("do-not-edit-this", "config-version").getInt(1) != configuration.node("do-not-edit-this", "config-version").getInt(0)) {
            platform.logger.info(Component.text("Updating translation file ($code)...", NamedTextColor.YELLOW))

            // Remove dynamic nodes, only if exists
            ConfigurateUtil.setIfMissingConfigMap(newConfig, configuration, "glist", "servers-format", "bars")

            configuration.mergeFrom(newConfig)

            // Set version
            configuration.node("do-not-edit-this", "config-version").set(newConfig.node("do-not-edit-this", "config-version"))

            configurationLoader.save(configuration)

            Files.delete(temporalFile.toPath())
            platform.logger.info(Component.text("Translation updated.", NamedTextColor.GREEN))

            // Load messages again
            translationMessages = configuration.get(TranslationMessages::class.java)!!
        } else {
            // Delete temporal file, no longer needed.
            Files.delete(temporalFile.toPath())
        }
    }

    fun getMessages(): TranslationMessages {
        return translationMessages
    }

}
