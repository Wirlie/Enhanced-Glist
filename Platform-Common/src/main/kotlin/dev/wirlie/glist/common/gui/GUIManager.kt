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

package dev.wirlie.glist.common.gui

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import dev.wirlie.glist.common.configurate.RegexSerializer
import dev.wirlie.glist.common.gui.config.GuiGlistMenuConfig
import dev.wirlie.glist.common.gui.config.GuiSlistMenuConfig
import dev.wirlie.glist.common.gui.config.toolbar.DefinitionsConfigSerializer
import dev.wirlie.glist.common.gui.config.toolbar.DefinitionsCustomConfig
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

class GUIManager(
    val platform: Platform<*,*,*>
) {

    val glistConfigFile = File(platform.pluginFolder, "gui-glist-menu.conf")
    val slistConfigFile = File(platform.pluginFolder, "gui-slist-menu.conf")

    val glistConfigLoader: HoconConfigurationLoader
    val slistConfigLoader: HoconConfigurationLoader

    lateinit var glistConfig: GuiGlistMenuConfig
    lateinit var slistConfig: GuiSlistMenuConfig

    init {
        val customFactory: ObjectMapper.Factory = ObjectMapper.factoryBuilder().build()

        glistConfigLoader = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(glistConfigFile.toPath())
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers { build: TypeSerializerCollection.Builder ->
                    build.registerAnnotatedObjects(customFactory)
                    build.register(IntRange::class.java, IntRangeSerializer())
                    build.register(Regex::class.java, RegexSerializer())
                    build.register(DefinitionsCustomConfig::class.java, DefinitionsConfigSerializer())
                }
            }
            .build()

        slistConfigLoader = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(slistConfigFile.toPath())
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers { build: TypeSerializerCollection.Builder ->
                    build.registerAnnotatedObjects(customFactory)
                    build.register(IntRange::class.java, IntRangeSerializer())
                    build.register(Regex::class.java, RegexSerializer())
                    build.register(DefinitionsCustomConfig::class.java, DefinitionsConfigSerializer())
                }
            }
            .build()

        saveDefaults()
        load()
    }

    private fun saveDefaults() {
        fun trySave(file: File) {
            if (!file.exists()) {
                val resource = this::class.java.getResourceAsStream("/${file.name}")!!
                val parent = file.parentFile

                if (!parent.exists()) {
                    Files.createDirectories(parent.toPath())
                }

                Files.copy(resource, file.toPath())
            }
        }

        trySave(glistConfigFile)
        trySave(slistConfigFile)
    }

    private fun load() {
        glistConfig = glistConfigLoader.load().also {
            applyUpdates(glistConfigFile, it, glistConfigLoader)
        }.get(GuiGlistMenuConfig::class.java)!!

        slistConfig = glistConfigLoader.load().also {
            applyUpdates(slistConfigFile, it, slistConfigLoader)
        }.get(GuiSlistMenuConfig::class.java)!!
    }

    private fun applyUpdates(file: File, currentConfiguration: ConfigurationNode, loader: HoconConfigurationLoader) {
        val temporalFile = File(platform.pluginFolder, "tempo-${file.name}")

        if(temporalFile.exists()) {
            Files.delete(temporalFile.toPath())
        }

        val input = this::class.java.getResourceAsStream("/${file.name}")!!

        Files.copy(input, temporalFile.toPath())

        val newConfig = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(temporalFile.toPath())
            .build()
            .load()

        if(newConfig.node("do-not-edit-this", "config-version").getInt(1) != currentConfiguration.node("do-not-edit-this", "config-version").getInt(0)) {
            platform.logger.info(Component.text("Updating GUI configuration file (${file.name})...", NamedTextColor.YELLOW))

            // Remove dynamic nodes, only if exists
            ConfigurateUtil.setIfMissingConfigMap(newConfig, currentConfiguration, "toolbar", "background", "definitions")

            currentConfiguration.mergeFrom(newConfig)

            // Set version
            currentConfiguration.node("do-not-edit-this", "config-version").set(newConfig.node("do-not-edit-this", "config-version"))

            loader.save(currentConfiguration)

            Files.delete(temporalFile.toPath())
            platform.logger.info(Component.text("GUI Configuration (${file.name}) updated.", NamedTextColor.GREEN))
        } else {
            // Delete temporal file, no longer needed.
            Files.delete(temporalFile.toPath())
        }
    }

}
