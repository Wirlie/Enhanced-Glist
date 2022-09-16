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
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import dev.wirlie.glist.common.configurate.RegexSerializer
import dev.wirlie.glist.common.configuration.sections.ConfigurationSection
import dev.wirlie.glist.common.util.ConfigurateUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import java.io.File
import java.nio.file.Files
import dev.wirlie.glist.common.configuration.sections.*
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection

/**
 * Plugin configuration.
 * @param platform Platform instance.
 */
class PlatformConfiguration(
    val platform: Platform<*, *, *>
) {

    private lateinit var configurationFile: File
    private lateinit var configuration: ConfigurationNode
    private lateinit var configurationLoader: HoconConfigurationLoader

    /**
     * Setup configuration.
     *
     * * This will create configuration file if not exists.
     * * If configuration file exists, existing configuration will be updated to the latest version.
     * * Configuration will be loaded after setup.
     */
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

    /**
     * Save default configuration.
     */
    fun saveDefault() {
        if (!configurationFile.exists()) {
            platform.logger.info(Component.text("Configuration not found, saving default configuration..."))
            if (!configurationFile.parentFile.exists()) {
                Files.createDirectories(configurationFile.parentFile.toPath())
            }
            Files.copy(this::class.java.getResourceAsStream("/config.conf")!!, configurationFile.toPath())
        }
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

        if(newConfig.node("do-not-edit-this", "config-version").getString("unknown") != configuration.node("do-not-edit-this", "config-version").getString("unknown2")) {
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

    /**
     * Load configuration.
     */
    fun load() {
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
        platform.logger.info(Component.text("Configuration loaded."))
    }

    /**
     * Save configuration.
     */
    fun save() {
        configuration
        configurationLoader.save(configuration)
    }

    /**
     * Get configuration section. Currently available sections:
     * * [BehaviorSection]
     * * [CommandsSection]
     * * [DoNotEditSection]
     * * [GeneralSection]
     * * [GroupServersSection]
     * * [IgnoreServersSection]
     * * [UpdatesSection]
     *
     * @param clazz Section [Class].
     * @return Section instance using Configurate.
     */
    fun <T: ConfigurationSection> getSection(clazz: Class<T>): T {
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

    /**
     * Set section values from instance. Currently available sections:
     * * [BehaviorSection]
     * * [CommandsSection]
     * * [DoNotEditSection]
     * * [GeneralSection]
     * * [GroupServersSection]
     * * [IgnoreServersSection]
     * * [UpdatesSection]
     *
     * @param section Section instance to set.
     */
    fun setSection(section: ConfigurationSection) {
        val rootAnnotation = section::class.java.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        configuration.node(rootPath).set(section)
    }

    fun reload() {
        saveDefault()
        load()
        applyUpdates()
    }

}
