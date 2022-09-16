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

package dev.wirlie.glist.spigot.configuration

import dev.wirlie.glist.spigot.EnhancedGlistSpigot
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.io.File
import java.nio.file.Files

class ConfigurationManager(
    val plugin: EnhancedGlistSpigot
) {

    private lateinit var configuration: ConfigurationNode
    private lateinit var pluginConfiguration: PluginConfiguration

    val logger = plugin.logger
    val configurationFile = File(plugin.dataFolder, "config.conf")

    val configurationLoader = HoconConfigurationLoader.builder()
        .path(configurationFile.toPath())
        .emitComments(true)
        .prettyPrinting(true)
        .build()

    init {
        reload()
    }

    fun saveDefault() {
        if (!configurationFile.exists()) {
            logger.info("Configuration not found, saving default configuration...")
            if (!configurationFile.parentFile.exists()) {
                Files.createDirectories(configurationFile.parentFile.toPath())
            }
            Files.copy(this::class.java.getResourceAsStream("/config.conf")!!, configurationFile.toPath())
        }
    }

    private fun load() {
        configuration = configurationLoader.load()
        pluginConfiguration = configuration.get(PluginConfiguration::class.java)!!
        logger.info("Configuration loaded.")
    }

    fun getConfiguration() = pluginConfiguration

    fun reload() {
        plugin.logger.info("Reloading configuration...")
        saveDefault()
        load()
        applyUpdates()
    }

    fun save() {
        configuration.set(pluginConfiguration)
        configurationLoader.save(configuration)
        logger.info("Configuration saved.")
    }

    private fun applyUpdates() {
        val temporalFile = File(plugin.dataFolder, "config-temp.conf")
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

        if(newConfig.node("do-not-edit-this", "config-version").getString("unknown") != configuration.node("do-not-edit-this", "config-version").getString("unknown2")) {
            logger.info("Updating configuration...")

            configuration.mergeFrom(newConfig)

            // Set version
            configuration.node("do-not-edit-this", "config-version").set(newConfig.node("do-not-edit-this", "config-version"))

            configurationLoader.save(configuration)

            Files.delete(temporalFile.toPath())
            logger.info("Configuration updated.")
        } else {
            // Delete temporal file, no longer needed.
            Files.delete(temporalFile.toPath())
        }
    }

}
