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

package dev.wirlie.glist.common.configuration

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.util.ConfigurateUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.io.File
import java.nio.file.Files

class PlatformConfiguration(
    val platform: Platform<*, *, *>
) {

    private lateinit var configurationFile: File
    private lateinit var configuration: ConfigurationNode
    private lateinit var configurationLoader: HoconConfigurationLoader

    fun setup() {
        platform.logger.info(Component.text("Loading configuration..."))
        configurationFile = File(platform.pluginFolder, "config.conf")

        if(!configurationFile.exists()) {
            saveDefault()
            load()
        } else {
            load()
            applyUpdates()
        }
    }

    fun saveDefault() {
        platform.logger.info(Component.text("Configuration not found, saving default configuration..."))
        if(!configurationFile.parentFile.exists()) {
            Files.createDirectories(configurationFile.parentFile.toPath())
        }
        Files.copy(this::class.java.getResourceAsStream("/config.conf")!!, configurationFile.toPath())
    }

    private fun applyUpdates() {
        val temporalFile = File(platform.pluginFolder, "config-temp.conf")
        if(temporalFile.exists()) {
            Files.delete(temporalFile.toPath())
        }

        Files.copy(this::class.java.getResourceAsStream("/config.conf")!!, temporalFile.toPath())

        val newConfig = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(temporalFile.toPath())
            .build()
            .load()

        if(newConfig.node("do-not-edit-this", "config-version").getInt(1) != configuration.node("do-not-edit-this", "config-version").getInt(0)) {
            platform.logger.info(Component.text("Updating configuration...", NamedTextColor.YELLOW))

            // Remove dynamic nodes, only if exists
            ConfigurateUtil.setIfMissingConfigMap(newConfig, configuration, "group-servers")

            configuration.mergeFrom(newConfig)

            // Set version
            configuration.node("do-not-edit-this", "config-version").set(newConfig.node("do-not-edit-this", "config-version"))

            configurationLoader.save(configuration)

            Files.delete(temporalFile.toPath())
            platform.logger.info(Component.text("Configuration updated.", NamedTextColor.GREEN))
        } else {
            // Delete temporal file, no longer needed.
            Files.delete(temporalFile.toPath())
        }
    }

    fun load() {
        configurationLoader = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(configurationFile.toPath())
            .build()

        configuration = configurationLoader.load()
        platform.logger.info(Component.text("Configuration loaded."))
    }

    fun save() {
        configuration
        configurationLoader.save(configuration)
    }

    fun <T> getSection(clazz: Class<T>): T {
        val rootAnnotation = clazz.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        if(!configuration.hasChild(rootPath)) {
            throw IllegalStateException("Cannot resolve root node '${rootPath}', configuration modified or corrupted?")
        }

        return if(ConfigHandler::class.java.isAssignableFrom(clazz)) {
            val instance = clazz.getDeclaredConstructor().newInstance()
            (instance as ConfigHandler).handle(configuration.node(rootPath))
            instance
        } else {
            // If configurate cannot get ConfigurationNode, use a new instance instead to work with default values
            configuration.node(rootPath).get(clazz) ?: clazz.getDeclaredConstructor().newInstance()
        }
    }

    fun setSection(section: Any) {
        val rootAnnotation = section::class.java.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        configuration.node(rootPath).set(section)
    }

}
