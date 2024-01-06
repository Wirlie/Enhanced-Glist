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

package dev.wirlie.glist.tests

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformLogger
import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.configuration.sections.*
import net.kyori.adventure.audience.Audience
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File
import kotlin.test.*

@ExtendWith(MockitoExtension::class)
class MainConfigurationTest {

    @Mock private lateinit var platform: Platform<*, *, *>
    private lateinit var configuration: PlatformConfiguration

    @BeforeEach
    fun loadConfiguration() {
        Mockito.`when`(platform.logger).thenReturn(PlatformLogger(Audience.empty()))
        Mockito.`when`(platform.pluginFolder).thenReturn(File("test-data-folder"))

        // Configure platform configuration
        configuration = PlatformConfiguration(platform)
        configuration.setup()
    }

    @AfterEach
    fun cleanup() {
        // Remove test folder
        platform.pluginFolder.deleteRecursively()
    }

    @Test
    fun commandGlistRead() {
        val section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        assertEquals("globallistest", section.glist.label, "Failed read test for configuration [commands, glist, label]")
        assertEquals("test.glist", section.glist.permission, "Failed read test for configuration [commands, glist, permission]")
        assertContentEquals(arrayOf("test", "test2", "test3"), section.glist.aliases, "Failed read test for configuration [commands, glist, aliases]")
        assertFalse(section.glist.useGuiMenu, "Failed read test for configuration [commands, glist, use-gui-menu]")
    }

    @Test
    fun commandGlistWrite() {
        var section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        section.glist.label = "writetest-glist"
        section.glist.permission = "write.test.glist"
        section.glist.aliases = arrayOf("write1", "write2", "write3")
        section.glist.useGuiMenu = true

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(CommandsSection::class.java)
        assertEquals("writetest-glist", section.glist.label, "Failed write test for configuration [commands, glist, label]")
        assertEquals("write.test.glist", section.glist.permission, "Failed write test for configuration [commands, glist, permission]")
        assertContentEquals(arrayOf("write1", "write2", "write3"), section.glist.aliases, "Failed write test for configuration [commands, glist, aliases]")
        assertTrue(section.glist.useGuiMenu, "Failed write test for configuration [commands, glist, use-gui-menu]")
    }

    @Test
    fun commandSlistRead() {
        val section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        assertEquals("slisttest", section.slist.label, "Failed read test for configuration [commands, slist, label]")
        assertEquals("test.slist", section.slist.permission, "Failed read test for configuration [commands, slist, permission]")
        assertContentEquals(arrayOf("test4", "test5", "test6", "test7"), section.slist.aliases, "Failed read test for configuration [commands, slist, aliases]")
        assertFalse(section.slist.useGuiMenu, "Failed read test for configuration [commands, slist, use-gui-menu]")
    }

    @Test
    fun commandSlistWrite() {
        var section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        section.slist.label = "writetest-slist"
        section.slist.permission = "write.test.slist"
        section.slist.aliases = arrayOf("write4", "write5")
        section.slist.useGuiMenu = true

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        assertEquals("writetest-slist", section.slist.label, "Failed write test for configuration [commands, slist, label]")
        assertEquals("write.test.slist", section.slist.permission, "Failed write test for configuration [commands, slist, permission]")
        assertContentEquals(arrayOf("write4", "write5"), section.slist.aliases, "Failed write test for configuration [commands, slist, aliases]")
        assertTrue(section.slist.useGuiMenu, "Failed write test for configuration [commands, slist, use-gui-menu]")
    }

    @Test
    fun commandEglRead() {
        val section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        assertEquals("egltest", section.egl.label, "Failed read test for configuration [commands, egl, label]")
        assertEquals("test.egl", section.egl.permission, "Failed read test for configuration [commands, egl, permission]")
        assertContentEquals(arrayOf("test8", "test9"), section.egl.aliases, "Failed read test for configuration [commands, egl, aliases]")
    }

    @Test
    fun commandEglWrite() {
        var section = configuration.getSection(CommandsSection::class.java)
        assertNotNull(section, "Failed to read: CommandsSection")
        section.egl.label = "writetest-egl"
        section.egl.permission = "write.test.egl"
        section.egl.aliases = arrayOf("write6", "write7", "write8", "write9")

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(CommandsSection::class.java)
        assertEquals("writetest-egl", section.egl.label, "Failed write test for configuration [commands, egl, label]")
        assertEquals("write.test.egl", section.egl.permission, "Failed write test for configuration [commands, egl, permission]")
        assertContentEquals(arrayOf("write6", "write7", "write8", "write9"), section.egl.aliases, "Failed write test for configuration [commands, egl, aliases]")
    }

    @Test
    fun generalRead() {
        val section = configuration.getSection(GeneralSection::class.java)
        assertNotNull(section, "Failed to read: GeneralSection")
        assertEquals("<bold><aqua>EGlist TEST 123 ></aqua></bold><reset>", section.prefix, "Failed read test for configuration [general, prefix]")
        assertEquals("testlang", section.language, "Failed read test for configuration [general, language]")
        assertEquals(826, section.playersPerRow, "Failed read test for configuration [general, players-per-row]")
        assertEquals(124, section.playersPerPage, "Failed read test for configuration [general, players-per-page]")
        assertFalse(section.cache.serverPlayers.enable, "Failed read test for configuration [general, cache, server-players, enable]")
        assertEquals(2938, section.cache.serverPlayers.time, "Failed read test for configuration [general, cache, server-players, time]")
        assertEquals(false, section.hideEmptyServers, "Failed read test for configuration [general, hide-empty-servers]")
        assertEquals(255, section.serversPerPage, "Failed read test for configuration [general, servers-per-page]")
        assertEquals(2193, section.minPlayersRequiredToDisplayServer, "Failed read test for configuration [general, min-players-required-to-display-server]")
        assertTrue(section.displayServerNameUppercase, "Failed read test for configuration [general, display-server-name-uppercase]")
    }

    @Test
    fun generalWrite() {
        var section = configuration.getSection(GeneralSection::class.java)
        assertNotNull(section, "Failed to read: GeneralSection")
        section.prefix = "<white>EGList <bold>test</bold></white>"
        section.language = "test-language-code-write"
        section.playersPerRow = 9834
        section.playersPerPage = 8273
        section.cache.serverPlayers.enable = true
        section.cache.serverPlayers.time = 1239
        section.hideEmptyServers = true
        section.serversPerPage = 12873
        section.minPlayersRequiredToDisplayServer = 32983
        section.displayServerNameUppercase = false

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(GeneralSection::class.java)
        assertNotNull(section, "Failed to read: GeneralSection")
        assertEquals("<white>EGList <bold>test</bold></white>", section.prefix, "Failed write test for configuration [general, prefix]")
        assertEquals("test-language-code-write", section.language, "Failed write test for configuration [general, language]")
        assertEquals(9834, section.playersPerRow, "Failed write test for configuration [general, players-per-row]")
        assertEquals(8273, section.playersPerPage, "Failed write test for configuration [general, players-per-page]")
        assertTrue(section.cache.serverPlayers.enable, "Failed write test for configuration [general, cache, server-players, enable]")
        assertEquals(1239, section.cache.serverPlayers.time, "Failed write test for configuration [general, cache, server-players, time]")
        assertEquals(true, section.hideEmptyServers, "Failed write test for configuration [general, hide-empty-servers]")
        assertEquals(12873, section.serversPerPage, "Failed write test for configuration [general, servers-per-page]")
        assertEquals(32983, section.minPlayersRequiredToDisplayServer, "Failed write test for configuration [general, min-players-required-to-display-server]")
        assertFalse(section.displayServerNameUppercase, "Failed write test for configuration [general, display-server-name-uppercase]")
    }

    @Test
    fun behaviorRead() {
        val section = configuration.getSection(BehaviorSection::class.java)
        assertNotNull(section, "Failed to read: BehaviorSection")
        assertFalse(section.vanish.enable, "Failed read test for configuration [behavior, vanish, enable]")
        assertFalse(section.vanish.hideVanishedUsers, "Failed read test for configuration [behavior, vanish, hide-vanished-users]")
        assertEquals("test.bypass.test.hide", section.vanish.hideBypassPermission, "Failed read test for configuration [behavior, vanish, hide-bypass-permission]")
        assertFalse(section.afk.enable, "Failed read test for configuration [behavior, afk, enable]")
    }

    @Test
    fun behaviorWrite() {
        var section = configuration.getSection(BehaviorSection::class.java)
        assertNotNull(section, "Failed to read: BehaviorSection")
        section.vanish.enable = true
        section.vanish.hideVanishedUsers = true
        section.vanish.hideBypassPermission = "write.test.bypass.vanish"
        section.afk.enable = true

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(BehaviorSection::class.java)
        assertTrue(section.vanish.enable, "Failed write test for configuration [behavior, vanish, enable]")
        assertTrue(section.vanish.hideVanishedUsers, "Failed write test for configuration [behavior, vanish, hide-vanished-users]")
        assertEquals("write.test.bypass.vanish", section.vanish.hideBypassPermission, "Failed write test for configuration [behavior, vanish, hide-bypass-permission]")
        assertTrue(section.afk.enable, "Failed write test for configuration [behavior, afk, enable]")
    }

    @Test
    fun updatesRead() {
        val section = configuration.getSection(UpdatesSection::class.java)
        assertNotNull(section, "Failed to read: UpdatesSection")
        assertFalse(section.checkForUpdates, "Failed read test for configuration [updates, check-for-updates]")
        assertEquals(1239, section.checkInterval, "Failed read test for configuration [updates, check-interval]")
        assertFalse(section.notify.onJoin.enable, "Failed read test for configuration [updates, notify, on-join, enable]")
        assertEquals(5823, section.notify.onJoin.delay, "Failed read test for configuration [updates, notify, on-join, delay]")
        assertEquals("test.ebcl.update.test", section.notify.onJoin.permission, "Failed read test for configuration [updates, notify, on-join, permission]")
        assertFalse(section.notify.console.enable, "Failed read test for configuration [updates, notify, console, enable]")
        assertEquals(93248, section.notify.console.notificationInterval, "Failed read test for configuration [updates, notify, console, notification-interval]")
    }

    @Test
    fun updatesWrite() {
        var section = configuration.getSection(UpdatesSection::class.java)
        assertNotNull(section, "Failed to read: UpdatesSection")
        section.checkForUpdates = true
        section.checkInterval = 8944
        section.notify.onJoin.enable = true
        section.notify.onJoin.delay = 28733
        section.notify.onJoin.permission = "test.write.update.test"
        section.notify.console.enable = true
        section.notify.console.notificationInterval = 19283

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(UpdatesSection::class.java)
        assertTrue(section.checkForUpdates, "Failed write test for configuration [updates, check-for-updates]")
        assertEquals(8944, section.checkInterval, "Failed write test for configuration [updates, check-interval]")
        assertTrue(section.notify.onJoin.enable, "Failed write test for configuration [updates, notify, on-join, enable]")
        assertEquals(28733, section.notify.onJoin.delay, "Failed write test for configuration [updates, notify, on-join, delay]")
        assertEquals("test.write.update.test", section.notify.onJoin.permission, "Failed write test for configuration [updates, notify, on-join, permission]")
        assertTrue(section.notify.console.enable, "Failed write test for configuration [updates, notify, console, enable]")
        assertEquals(19283, section.notify.console.notificationInterval, "Failed write test for configuration [updates, notify, console, notification-interval]")
    }

    @Test
    fun ignoreServersRead() {
        val section = configuration.getSection(IgnoreServersSection::class.java)
        assertNotNull(section, "Failed to read: IgnoreServersSection")
        assertContentEquals(listOf("test1", "test2", "test3"), section.byName, "Failed read test for configuration [ignore-servers, by-name]")
        assertContentEquals(
            listOf("test4.*", "test5-1234.*"),
            section.byPattern.map { it.pattern },
            "Failed read test for configuration [ignore-servers, by-pattern]"
        )
    }

    @Test
    fun ignoreServersWrite() {
        var section = configuration.getSection(IgnoreServersSection::class.java)
        assertNotNull(section, "Failed to read: IgnoreServersSection")
        section.byName = mutableListOf("write10", "write11", "write12")
        section.byPattern = mutableListOf(Regex("writepattern.test1.*"), Regex("writepattern.test2.*"), Regex("writepattern.test3.*"))

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(IgnoreServersSection::class.java)
        assertContentEquals(listOf("write10", "write11", "write12"), section.byName, "Failed write test for configuration [ignore-servers, by-name]")
        assertContentEquals(
            listOf("writepattern.test1.*", "writepattern.test2.*", "writepattern.test3.*"),
            section.byPattern.map { it.pattern },
            "Failed write test for configuration [ignore-servers, by-pattern]"
        )
    }

    @Test
    fun groupServersRead() {
        val section = configuration.getSection(GroupServersSection::class.java)
        assertNotNull(section, "Failed to read: GroupServersSection")
        assertTrue(section.servers.any{ it.serverName == "testlobby" }, "Failed read test for configuration [group-servers, testlobby]")
        assertFalse(section.servers.any{ it.serverName == "lobby" }, "Failed read test for configuration [group-servers, testlobby]")
        assertContentEquals(
            listOf("testlobby1", "testlobby2", "testlobby3", "testlobby4"),
            section.servers.first { it.serverName == "testlobby" }.byName,
            "Failed read test for configuration [group-servers, testlobby]"
        )
        assertContentEquals(
            listOf("testbedwars-game.*", "testbedwars-lobby.*"),
            section.servers.first { it.serverName == "testbedwars" }.byPattern,
            "Failed read test for configuration [group-servers, testbedwars]"
        )
    }

    @Test
    fun groupServersWrite() {
        var section = configuration.getSection(GroupServersSection::class.java)
        assertNotNull(section, "Failed to read: GroupServersSection")
        section.servers = mutableListOf(
            GroupServersSection.ServerSection().also {
                it.serverName = "writelobby1"
                it.byName = mutableListOf("writelobby1", "writelobby2")
            },
            GroupServersSection.ServerSection().also {
                it.serverName = "writelobby2"
                it.byPattern = mutableListOf("writelobby5.*")
            }
        )

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(GroupServersSection::class.java)

        assertTrue(section.servers.any{ it.serverName == "writelobby1" }, "Failed write test for configuration [group-servers, testlobby]")
        assertTrue(section.servers.any{ it.serverName == "writelobby2" }, "Failed write test for configuration [group-servers, testlobby]")
        assertFalse(section.servers.any{ it.serverName == "writelobby3" }, "Failed write test for configuration [group-servers, testlobby]")
        assertContentEquals(
            listOf("writelobby1", "writelobby2"),
            section.servers.first { it.serverName == "writelobby1" }.byName,
            "Failed write test for configuration [group-servers, testlobby]"
        )
        assertContentEquals(
            listOf("writelobby5.*"),
            section.servers.first { it.serverName == "writelobby2" }.byPattern,
            "Failed write test for configuration [group-servers, testbedwars]"
        )
    }

    @Test
    fun communicationRead() {
        val section = configuration.getSection(CommunicationSection::class.java)
        assertNotNull(section, "Failed to read: CommunicationSection")
        assertEquals("test-plugin-messages", section.type, "Failed read test for configuration [communication, type]")
        assertEquals("test-test", section.rabbitmqServer.host, "Failed read test for configuration [communication, rabbitmq-server, host]")
        assertEquals(4932, section.rabbitmqServer.port, "Failed read test for configuration [communication, rabbitmq-server, port]")
        assertEquals("testuser", section.rabbitmqServer.user, "Failed read test for configuration [communication, rabbitmq-server, user]")
        assertEquals("testpass", section.rabbitmqServer.password, "Failed read test for configuration [communication, rabbitmq-server, password]")
        assertEquals("test-test2", section.redisServer.host, "Failed read test for configuration [communication, redis-server, host]")
        assertEquals(1245, section.redisServer.port, "Failed read test for configuration [communication, redis-server, port]")
        assertEquals("testuserredis", section.redisServer.user, "Failed read test for configuration [communication, redis-server, user]")
        assertEquals("testpassredis", section.redisServer.password, "Failed read test for configuration [communication, redis-server, password]")
    }

    @Test
    fun communicationWrite() {
        var section = configuration.getSection(CommunicationSection::class.java)
        assertNotNull(section, "Failed to read: CommunicationSection")
        section.type = "test-write-type"
        section.rabbitmqServer.host = "write-rabbitmq-host"
        section.rabbitmqServer.port = 19283
        section.rabbitmqServer.user = "write-rabbitmq-user"
        section.rabbitmqServer.password = "write-rabbitmq-pass"
        section.redisServer.host = "write-redis-host"
        section.redisServer.port = 53643
        section.redisServer.user = "write-redis-user"
        section.redisServer.password = "write-redis-pass"

        //save section
        saveSectionAndReload(section)

        // read again and check if set
        section = configuration.getSection(CommunicationSection::class.java)
        assertNotNull(section, "Failed to read: CommunicationSection")
        assertEquals("test-write-type", section.type, "Failed write test for configuration [communication, type]")
        assertEquals("write-rabbitmq-host", section.rabbitmqServer.host, "Failed write test for configuration [communication, rabbitmq-server, host]")
        assertEquals(19283, section.rabbitmqServer.port, "Failed write test for configuration [communication, rabbitmq-server, port]")
        assertEquals("write-rabbitmq-user", section.rabbitmqServer.user, "Failed write test for configuration [communication, rabbitmq-server, user]")
        assertEquals("write-rabbitmq-pass", section.rabbitmqServer.password, "Failed write test for configuration [communication, rabbitmq-server, password]")
        assertEquals("write-redis-host", section.redisServer.host, "Failed write test for configuration [communication, redis-server, host]")
        assertEquals(53643, section.redisServer.port, "Failed write test for configuration [communication, redis-server, port]")
        assertEquals("write-redis-user", section.redisServer.user, "Failed write test for configuration [communication, redis-server, user]")
        assertEquals("write-redis-pass", section.redisServer.password, "Failed write test for configuration [communication, redis-server, password]")
    }

    private fun saveSectionAndReload(section: ConfigurationSection) {
        configuration.setSection(section)
        configuration.save()
        configuration.reload()
    }

}
