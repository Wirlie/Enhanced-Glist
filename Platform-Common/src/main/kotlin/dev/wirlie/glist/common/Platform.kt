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

import com.google.gson.GsonBuilder
import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.configuration.sections.GroupServersSection
import dev.wirlie.glist.common.configuration.sections.IgnoreServersSection
import dev.wirlie.glist.common.extensions.miniMessage
import dev.wirlie.glist.common.gui.GUIManager
import dev.wirlie.glist.common.hooks.HookManager
import dev.wirlie.glist.common.messenger.NetworkMessenger
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.player.PlayerManager
import dev.wirlie.glist.common.translation.TranslatorManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.io.File
import java.util.concurrent.CompletableFuture

abstract class Platform<S, P, C> {

    lateinit var pluginFolder: File
    lateinit var logger: PlatformLogger

    lateinit var translatorManager: TranslatorManager
    lateinit var platformCommandManager: PlatformCommandManager<S>
    lateinit var hookManager: HookManager
    lateinit var playerManager: PlayerManager
    lateinit var messenger: NetworkMessenger<S>
    var guiManager: GUIManager? = null
    var guiSystemEnabled = false

    val gsonInstance = GsonBuilder().create()

    var console: Audience = Audience.empty()
        set(value) {
            field = value
            logger = PlatformLogger(value)
        }

    val configuration = PlatformConfiguration(this)

    fun setup(
        commandManager: PlatformCommandManager<S>,
        networkMessenger: NetworkMessenger<S>
    ) {
        unsafeInstance = this
        messenger = networkMessenger
        configuration.setup()
        pluginPrefix = configuration.getSection(GeneralSection::class.java).prefix.miniMessage()
        translatorManager = TranslatorManager(this)
        translatorManager.setup()
        platformCommandManager = commandManager
        platformCommandManager.setup()
        platformCommandManager.registerCommands()
        hookManager = HookManager(this)
        registerHooks()
        playerManager = PlayerManager(this)
        networkMessenger.register()
        networkMessenger.registerListeners()
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

    abstract fun registerHooks()

    abstract fun performCommandForPlayer(player: PlatformExecutor<S>, command: String)

    fun enableGUISystem() {
        if(!guiSystemEnabled) {
            guiSystemEnabled = true
            guiManager = GUIManager(this)
        }
    }

    fun getServerGrouped(name: String): PlatformServerGroup<S>? {
        val ignoreServers = configuration.getSection(IgnoreServersSection::class.java)
        val configuration = configuration.getSection(GroupServersSection::class.java)

        val groupConfiguration = configuration.servers.firstOrNull { it.serverName.equals(name, true) }

        @Suppress("FoldInitializerAndIfToElvis")
        if(groupConfiguration == null) {
            // No group, try to return a server directly
            return getServerByName(name)?.run {
                if(!ignoreServers.shouldIgnore(this.getName())) {
                    PlatformServerGroup(this.getName(), listOf(this), false)
                } else {
                    null
                }
            }
        }

        val allServers = getAllServers()
        val servers = resolveServerGroupByConfiguration(groupConfiguration, allServers, ignoreServers)

        if(servers.isEmpty()) {
            return null
        }

        return PlatformServerGroup(groupConfiguration.serverName, servers)
    }

    fun getAllServersGrouped(): List<PlatformServerGroup<S>> {
        val ignoreServersConfiguration = configuration.getSection(IgnoreServersSection::class.java)
        val groupsConfiguration = configuration.getSection(GroupServersSection::class.java)
        val allServers = getAllServers()
        val groups = mutableListOf<PlatformServerGroup<S>>()

        for(serverConfig in groupsConfiguration.servers) {
            val matchedServers = resolveServerGroupByConfiguration(serverConfig, allServers, ignoreServersConfiguration)

            if(matchedServers.isNotEmpty()) {
                groups.add(PlatformServerGroup(serverConfig.serverName, matchedServers))
            }
        }

        // Make groups for servers without group
        allServers.filter { s -> groups.none { g -> g.getServers().contains(s) } }.forEach {
            if (!ignoreServersConfiguration.shouldIgnore(it.getName())) {
                groups.add(PlatformServerGroup(it.getName(), listOf(it), byConfiguration = false))
            }
        }

        return groups.run {
            // Hide empty servers, if enabled.
            val generalConfiguration = configuration.getSection(GeneralSection::class.java)

            if(generalConfiguration.hideEmptyServers) {
                var minPlayers = generalConfiguration.minPlayersRequiredToDisplayServer
                if(minPlayers < 0) {
                    minPlayers = 0
                }
                this.filter { it.getPlayersCount() >= minPlayers }
            } else {
                this
            }
        }
    }

    private fun resolveServerGroupByConfiguration(
        serverConfig: GroupServersSection.ServerSection,
        allServers: List<PlatformServer<S>>,
        ignoreServersConfig: IgnoreServersSection
    ): MutableList<PlatformServer<S>> {
        val matchedServers = mutableListOf<PlatformServer<S>>()

        for(name in serverConfig.byName) {
            getServerByName(name)?.also {
                if(!ignoreServersConfig.shouldIgnore(it.getName())) {
                    matchedServers.add(it)
                }
            }
        }

        for(patternString in serverConfig.byPattern) {
            val regex = Regex(patternString)

            allServers.filter { regex.matches(it.getName()) }.forEach {
                if(!ignoreServersConfig.shouldIgnore(it.getName())) {
                    matchedServers.add(it)
                }
            }
        }

        return matchedServers
    }

    abstract fun callAFKStateChangeEvent(fromPlayer: PlatformExecutor<S>, state: Boolean): CompletableFuture<Boolean>

    abstract fun callVanishStateChangeEvent(fromPlayer: PlatformExecutor<S>, state: Boolean): CompletableFuture<Boolean>

    companion object {

        var pluginPrefix: Component = Component.empty()

        lateinit var unsafeInstance: Platform<*,*,*>

    }

}
