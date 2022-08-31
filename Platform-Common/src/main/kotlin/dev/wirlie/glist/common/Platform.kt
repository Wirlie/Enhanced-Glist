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

package dev.wirlie.glist.common

import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.configuration.sections.GroupServersSection
import dev.wirlie.glist.common.extensions.miniMessage
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.translation.TranslatorManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.io.File

abstract class Platform<S, P, C> {

    lateinit var pluginFolder: File
    lateinit var logger: PlatformLogger

    lateinit var translatorManager: TranslatorManager
    lateinit var platformCommandManager: PlatformCommandManager<S>

    var console: Audience = Audience.empty()
        set(value) {
            field = value
            logger = PlatformLogger(value)
        }

    val configuration = PlatformConfiguration(this)

    fun setup(
        commandManager: PlatformCommandManager<S>
    ) {
        configuration.setup()
        pluginPrefix = configuration.getSection(GeneralSection::class.java).prefix.miniMessage()
        translatorManager = TranslatorManager(this)
        translatorManager.setup()
        platformCommandManager = commandManager
        platformCommandManager.setup()
        platformCommandManager.registerCommands()
    }

    fun disable() {

    }

    fun reload() {

    }

    abstract fun toPlatformServer(server: S): PlatformServer<S>

    abstract fun toPlatformExecutorPlayer(executor: P): PlatformExecutor<S>

    abstract fun toPlatformExecutorConsole(executor: C): PlatformExecutor<S>

    abstract fun getAllServers(): List<PlatformServer<S>>

    abstract fun getServerByName(name: String): PlatformServer<S>?

    abstract fun getConnectedPlayersAmount(): Int

    fun getServerGrouped(name: String): PlatformServerGroup<S>? {
        val configuration = configuration.getSection(GroupServersSection::class.java)

        val groupConfiguration = configuration.servers.firstOrNull { it.serverName.equals(name, true) }

        @Suppress("FoldInitializerAndIfToElvis")
        if(groupConfiguration == null) {
            // No group, try to return a server directly
            return getServerByName(name)?.run { PlatformServerGroup(this.getName(), listOf(this), false) }
        }

        val allServers = getAllServers()
        val servers = resolveServerGroupByConfiguration(groupConfiguration, allServers)

        if(servers.isEmpty()) {
            return null
        }

        return PlatformServerGroup(groupConfiguration.serverName, servers)
    }

    fun getAllServersGrouped(): List<PlatformServerGroup<S>> {
        val configuration = configuration.getSection(GroupServersSection::class.java)
        val allServers = getAllServers()
        val groups = mutableListOf<PlatformServerGroup<S>>()

        for(serverConfig in configuration.servers) {
            val matchedServers = resolveServerGroupByConfiguration(serverConfig, allServers)

            if(matchedServers.isNotEmpty()) {
                groups.add(PlatformServerGroup(serverConfig.serverName, matchedServers))
            }
        }

        // Make groups for servers without group
        allServers.filter { s -> groups.none { g -> g.getServers().contains(s) } }.forEach {
            groups.add(PlatformServerGroup(it.getName(), listOf(it), byConfiguration = false))
        }

        return groups
    }

    private fun resolveServerGroupByConfiguration(
        serverConfig: GroupServersSection.ServerSection,
        allServers: List<PlatformServer<S>>
    ): MutableList<PlatformServer<S>> {
        val matchedServers = mutableListOf<PlatformServer<S>>()

        for(name in serverConfig.byName) {
            getServerByName(name)?.also {
                matchedServers.add(it)
            }
        }

        for(patternString in serverConfig.byPattern) {
            val regex = Regex(patternString)

            allServers.filter { regex.matches(it.getName()) }.forEach {
                matchedServers.add(it)
            }
        }

        return matchedServers
    }

    companion object {

        var pluginPrefix: Component = Component.empty()

    }

}
