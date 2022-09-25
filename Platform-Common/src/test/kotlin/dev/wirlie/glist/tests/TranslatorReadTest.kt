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

package dev.wirlie.glist.tests

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformLogger
import dev.wirlie.glist.common.configurate.IntRangeSerializer
import dev.wirlie.glist.common.configurate.RegexSerializer
import dev.wirlie.glist.common.configurate.ServersFormatSerializer
import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.translation.TranslationMessages
import dev.wirlie.glist.common.translation.Translator
import dev.wirlie.glist.common.translation.TranslatorManager
import net.kyori.adventure.audience.Audience
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class TranslatorReadTest {

    @Mock lateinit var platform: Platform<*, *, *>
    private lateinit var manager: TranslatorManager
    private val translators = mutableMapOf<String, Translator>()

    @BeforeEach
    fun loadTranslator() {
        Mockito.`when`(platform.logger).thenReturn(PlatformLogger(Audience.empty()))
        Mockito.`when`(platform.pluginFolder).thenReturn(File("test-data-folder"))

        val configuration = PlatformConfiguration(platform)
        configuration.setup()
        Mockito.`when`(platform.configuration).thenReturn(configuration)

        manager = TranslatorManager(platform)
        setTranslatorCode(manager, "en")
        translators["en"] = manager.getTranslator()
        setTranslatorCode(manager, "es")
        translators["es"] = manager.getTranslator()
    }

    @AfterEach
    fun cleanup() {
        // Remove test folder
        platform.pluginFolder.deleteRecursively()
    }

    @Test
    fun readMessagesEs() {
        doReadTest("es")
    }

    @Test
    fun readMessagesEn() {
        doReadTest("en")
    }

    private fun doReadTest(code: String) {
        val translator = translators[code]
        assertNotNull(translator, "Failed to get translator for language '$code'")

        val configurationFile = File(platform.pluginFolder, "test-$code.conf")
        val customFactory: ObjectMapper.Factory = ObjectMapper.factoryBuilder().build()
        val loader = HoconConfigurationLoader.builder()
            .emitComments(true)
            .prettyPrinting(true)
            .path(configurationFile.toPath())
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers { build: TypeSerializerCollection.Builder ->
                    build.registerAnnotatedObjects(customFactory)
                    build.register(IntRange::class.java, IntRangeSerializer())
                    build.register(Regex::class.java, RegexSerializer())
                    build.register(TranslationMessages.GlistMessages.ServersFormat::class.java, ServersFormatSerializer())
                }
            }
            .build()

        val loadedNode = loader.createNode()
        loadedNode.set(translator.getMessages())

        // Save default file
        Files.copy(this::class.java.getResourceAsStream("/messages/$code.conf")!!, configurationFile.toPath())
        val originalNode = loader.load()

        travelChildrenMap(originalNode, loadedNode)
        assertEquals(originalNode, loadedNode, "Translator mismatch data for '$code' language")
    }

    private fun travelChildrenMap(originalNode: ConfigurationNode, loadedNode: ConfigurationNode) {
        for(nodeTravel in originalNode.childrenMap()) {
            val key = nodeTravel.key
            if(key is String) {
                val childNode = originalNode.node(key)

                if(childNode.childrenMap().isNotEmpty()) {
                    travelChildrenMap(childNode, loadedNode)
                } else {
                    val testNode = loadedNode.node(*childNode.path().array())
                    if(testNode is CommentedConfigurationNode) {
                        // Set comment to prevent equals mismatch
                        testNode.comment((childNode as CommentedConfigurationNode).comment())
                    }
                    assertEquals(childNode, testNode, "Mismatch for [${testNode.path().array().joinToString(" ")}]")
                }
            }
        }
    }

    private fun setTranslatorCode(manager: TranslatorManager, code: String) {
        val clazz = manager::class.java

        val codeField = clazz.getDeclaredField("code")
        codeField.isAccessible = true
        codeField.set(manager, code)

        val translatorField = clazz.getDeclaredField("translator")
        translatorField.isAccessible = true
        translatorField.set(manager, null)
    }

}
