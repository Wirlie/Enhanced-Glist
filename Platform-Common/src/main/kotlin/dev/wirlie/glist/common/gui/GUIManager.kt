/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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

import dev.simplix.protocolize.api.Protocolize
import dev.simplix.protocolize.data.packets.CloseWindow
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import dev.wirlie.glist.common.configurate.RegexSerializer
import dev.wirlie.glist.common.gui.config.GuiGlistMenuConfig
import dev.wirlie.glist.common.gui.config.GuiSlistMenuConfig
import dev.wirlie.glist.common.gui.config.toolbar.DefinitionsConfigSerializer
import dev.wirlie.glist.common.gui.config.toolbar.DefinitionsCustomConfig
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.common.util.ConfigurateUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
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

    private val glistConfigFile = File(platform.pluginFolder, "gui-glist-menu.conf")
    private val slistConfigFile = File(platform.pluginFolder, "gui-slist-menu.conf")

    private val glistConfigLoader: HoconConfigurationLoader
    private val slistConfigLoader: HoconConfigurationLoader

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
                val code = platform.translatorManager.getTranslator().realCode
                val resource = this::class.java.getResourceAsStream("/$code/${file.name}")!!
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
        glistConfig = glistConfigLoader.load().run {
            applyUpdates(glistConfigFile, this, glistConfigLoader)
            glistConfigLoader.load() // Load again, to ensure application of updates
        }.get(GuiGlistMenuConfig::class.java)!!

        slistConfig = slistConfigLoader.load().run {
            applyUpdates(slistConfigFile, this, slistConfigLoader)
            slistConfigLoader.load() // Load again, to ensure application of updates
        }.get(GuiSlistMenuConfig::class.java)!!
    }

    private fun applyUpdates(file: File, currentConfiguration: ConfigurationNode, loader: HoconConfigurationLoader) {
        val currentTranslatorCode = platform.translatorManager.getTranslator().realCode
        val currentConfigurationCode = currentConfiguration.node("do-not-edit-this", "code").string

        // Check if we can replace current configuration with original translated configuration
        if(currentConfigurationCode != null && currentConfigurationCode != currentTranslatorCode) {
            // Check if current configuration matches original
            val input = this::class.java.getResourceAsStream("/$currentConfigurationCode/${file.name}")

            if(input != null) {
                // Check...
                val temporalCodeFile = File(platform.pluginFolder, "temp-$currentConfigurationCode-${file.name}")
                Files.copy(input, temporalCodeFile.toPath())

                val originalConfig = HoconConfigurationLoader.builder()
                    .emitComments(true)
                    .prettyPrinting(true)
                    .path(temporalCodeFile.toPath())
                    .build()
                    .load()

                if(originalConfig.equals(currentConfiguration)) {
                    // Matches! So we can safely remove current file and replace with original.
                    // Remove current file
                    file.delete()
                    // Remove temporal to file
                    temporalCodeFile.delete()
                    // Regenerate
                    saveDefaults()
                    // Done
                    platform.logger.info(Component.text("Changed language of '${file.name}' from '$currentConfigurationCode' to '$currentTranslatorCode'", NamedTextColor.GREEN))
                    // We do not need to apply updates, at this point file is up-to-date
                    return
                } else {
                    // Done
                    platform.logger.info(Component.text("Cannot change the language of '${file.name}' from '$currentConfigurationCode' to '$currentTranslatorCode' because you have modified this file manually, file skipped.", NamedTextColor.YELLOW))
                    // Remove this file
                    temporalCodeFile.delete()
                }
            }
        }

        // Okay, check if configurations are up-to-date
        val temporalFile = File(platform.pluginFolder, "temp-${file.name}")

        if(temporalFile.exists()) {
            Files.delete(temporalFile.toPath())
        }

        val input = this::class.java.getResourceAsStream("/$currentTranslatorCode/${file.name}")!!

        Files.copy(input, temporalFile.toPath())

        val newConfig = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(temporalFile.toPath())
            .build()
            .load()

        if(newConfig.node("do-not-edit-this", "config-version").getString("unknown") != currentConfiguration.node("do-not-edit-this", "config-version").getString("unknown2")) {
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

    fun reload() {
        load()
        closeProtocolizeInventories()
    }

    fun disable() {
        closeProtocolizeInventories()
    }

    private fun closeProtocolizeInventories() {
        // Reload Protocolize, close inventories
        for(player in platform.getAllPlayers()) {
            val protocolizePlayer = Protocolize.playerProvider().player(player.getUUID())
            // Get inventories opened by EnhancedGlist
            var sendCloseMessage = false
            for(inventory in protocolizePlayer.registeredInventories()) {
                if(inventory.value is GUIInventory) {
                    // EnhancedGlist Inventory, remove and send close packet
                    protocolizePlayer.registerInventory(inventory.key, null)
                    protocolizePlayer.sendPacket(CloseWindow(inventory.key))
                    sendCloseMessage = true
                }
            }

            if(sendCloseMessage) {
                player.asAudience().sendMessage(
                    AdventureUtil.parseMiniMessage(
                        platform.translatorManager.getTranslator().getMessages().gui.closedByReload,
                        TagResolver.resolver("prefix",
                            Tag.selfClosingInserting(Platform.pluginPrefix)
                        )
                    )
                )
            }
        }
    }

}
