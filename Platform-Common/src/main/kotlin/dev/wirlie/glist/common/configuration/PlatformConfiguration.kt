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
    private lateinit var hocon: HoconConfigurationLoader
    private lateinit var hoconConfiguration: ConfigurationNode

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

    fun applyUpdates() {
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

        if(newConfig.node("do-not-edit-this", "config-version").getInt(1) != hoconConfiguration.node("do-not-edit-this", "config-version").getInt(0)) {
            platform.logger.info(Component.text("Updating configuration...", NamedTextColor.YELLOW))

            // Remove dynamic nodes
            newConfig.node("group-servers", "lobby").set(null)
            newConfig.node("group-servers", "bedwars").set(null)

            hoconConfiguration.mergeFrom(newConfig)

            hocon.save(hoconConfiguration)

            Files.delete(temporalFile.toPath())
            platform.logger.info(Component.text("Configuration updated.", NamedTextColor.GREEN))
        }
    }

    fun load() {
        hocon = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(configurationFile.toPath())
            .build()

        hoconConfiguration = hocon.load()
        platform.logger.info(Component.text("Configuration loaded."))
    }

    fun save() {
        hoconConfiguration
        hocon.save(hoconConfiguration)
    }

    fun <T> getSection(clazz: Class<T>): T {
        val rootAnnotation = clazz.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        if(!hoconConfiguration.hasChild(rootPath)) {
            throw IllegalStateException("Cannot resolve root node '${rootPath}', configuration modified or corrupted?")
        }

        return if(ConfigHandler::class.java.isAssignableFrom(clazz)) {
            val instance = clazz.getDeclaredConstructor().newInstance()
            (instance as ConfigHandler).handle(hoconConfiguration.node(rootPath))
            instance
        } else {
            // If configurate cannot get ConfigurationNode, use a new instance instead to work with default values
            hoconConfiguration.node(rootPath).get(clazz) ?: clazz.getDeclaredConstructor().newInstance()
        }
    }

    fun setSection(section: Any) {
        val rootAnnotation = section::class.java.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        hoconConfiguration.node(rootPath).set(section)
    }

}
