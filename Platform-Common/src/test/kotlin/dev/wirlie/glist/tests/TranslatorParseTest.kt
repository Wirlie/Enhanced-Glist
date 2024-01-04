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

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TranslatorParseTest {

    /*
    @Mock lateinit var platform: Platform<*, *, *>
    @Mock lateinit var executor: PlatformExecutor<*>
    private var servers: MutableList<PlatformServerGroup<*>> = mutableListOf()
    private lateinit var manager: TranslatorManager
    private val translators = mutableMapOf<String, Translator>()

    @BeforeEach
    fun loadTranslator() {
        // Mock Platform
        Mockito.`when`(platform.logger).thenReturn(PlatformLogger(Audience.empty()))
        Mockito.`when`(platform.pluginFolder).thenReturn(File("test-data-folder"))
        //Mockito.`when`(platform.fakePlayerCountForTest()).thenReturn(283)

        // Mock PlatformServerGroup
        val fakeServer1 = Mockito.mock(PlatformServerGroup::class.java)
        //Mockito.`when`(fakeServer1.fakePlayerCountForTest()).thenReturn(21)
        Mockito.`when`(fakeServer1.getName()).thenReturn("fakeserver1")
        servers.add(fakeServer1)

        val fakeServer2 = Mockito.mock(PlatformServerGroup::class.java)
        //Mockito.`when`(fakeServer2.fakePlayerCountForTest()).thenReturn(83)
        Mockito.`when`(fakeServer2.getName()).thenReturn("fakeserver2")
        servers.add(fakeServer2)

        val fakeServer3 = Mockito.mock(PlatformServerGroup::class.java)
        //Mockito.`when`(fakeServer3.fakePlayerCountForTest()).thenReturn(0)
        Mockito.`when`(fakeServer3.getName()).thenReturn("fakeserver3")
        servers.add(fakeServer3)

        val fakeServer4 = Mockito.mock(PlatformServerGroup::class.java)
        //Mockito.`when`(fakeServer4.fakePlayerCountForTest()).thenReturn(283)
        Mockito.`when`(fakeServer4.getName()).thenReturn("fakeserver4")
        servers.add(fakeServer4)

        val configuration = PlatformConfiguration(platform)
        configuration.setup()
        Mockito.`when`(platform.configuration).thenReturn(configuration)

        manager = TranslatorManager(platform)
        setTranslatorCode(manager, "en")
        translators["en"] = manager.getTranslator()
        setTranslatorCode(manager, "es")
        translators["es"] = manager.getTranslator()
    }

    @Test
    fun buildServersComponentES() {
        doBuildServersComponentTest("es")
    }

    @Test
    fun buildServersComponentEN() {
        doBuildServersComponentTest("en")
    }

    private fun doBuildServersComponentTest(code: String) {
        val translator = translators[code]
        assertNotNull(translator, "Failed to get translator for language '$code'")

        //TODO: Disabled for now
        /*assertEquals(
            Component.empty()
                .run { serversComponentAppend(translator, this, platform, servers[0], true) }
                .run { serversComponentAppend(translator, this, platform, servers[1], true) }
                .run { serversComponentAppend(translator, this, platform, servers[2], true) }
                .run { serversComponentAppend(translator, this, platform, servers[3], false) }
            ,
            translator.getMessages().glist.serversFormat.buildServersComponent(platform, servers, executor)
        )*/
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

    private fun serversComponentAppend(translator: Translator, component: Component, platform: Platform<*,*,*>, server: PlatformServerGroup<*>, newline: Boolean): Component {
        val percent = server.fakePlayerCountForTest() * 100.0 / platform.fakePlayerCountForTest()
        return component.append(
            AdventureUtil.parseMiniMessage(
                translator.getMessages().glist.serversFormat.template,
                TagResolver.resolver(
                    "server-name",
                    Tag.selfClosingInserting(Component.text(server.getName()))
                ),
                TagResolver.resolver(
                    "player-amount",
                    Tag.selfClosingInserting(Component.text(server.fakePlayerCountForTest()))
                ),
                TagResolver.resolver(
                    Formatter.number("percent", percent)
                ),
                TagResolver.resolver(
                    "bars",
                    Tag.selfClosingInserting(
                        AdventureUtil.parseMiniMessage(
                            translator.getMessages().glist.serversFormat.bars.entries.first { it.key.contains(percent.toInt()) }.value.string!!
                        )
                    )
                ),
            ).hoverEvent(
                HoverEvent.showText(
                    AdventureUtil.parseMiniMessage(
                        translator.getMessages().glist.serversFormat.clickToShowPlayersHoverMessage,
                        TagResolver.resolver(
                            "server-name",
                            Tag.selfClosingInserting(Component.text(server.getName()))
                        )
                    )
                )
            ).clickEvent(
                ClickEvent.runCommand("/slisttest ${server.getName()}")
            )
        ).run {
            if(newline) {
                this.append(Component.newline())
            } else {
                this
            }
        }
    }*/

}
