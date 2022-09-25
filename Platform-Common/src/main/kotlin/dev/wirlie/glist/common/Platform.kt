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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.configuration.sections.GroupServersSection
import dev.wirlie.glist.common.configuration.sections.IgnoreServersSection
import dev.wirlie.glist.common.configuration.sections.UpdatesSection
import dev.wirlie.glist.common.extensions.miniMessage
import dev.wirlie.glist.common.gui.GUIManager
import dev.wirlie.glist.common.hooks.HookManager
import dev.wirlie.glist.common.messenger.listeners.AfkStateChangeListener
import dev.wirlie.glist.common.messenger.listeners.VanishStateChangeListener
import dev.wirlie.glist.common.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.common.messenger.messages.RequestAllDataMessage
import dev.wirlie.glist.common.messenger.messages.VanishStateUpdateMessage
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.player.PlayerManager
import dev.wirlie.glist.common.translation.TranslatorManager
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.messenger.impl.DummyPlatformMessenger
import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.updater.PluginUpdater
import dev.wirlie.glist.updater.UpdaterScheduler
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.io.File
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max

abstract class Platform<S, P, C>: UpdaterScheduler {

    lateinit var pluginFolder: File
    lateinit var logger: PlatformLogger

    lateinit var translatorManager: TranslatorManager
    private lateinit var platformCommandManager: PlatformCommandManager<S>
    lateinit var hookManager: HookManager
    lateinit var playerManager: PlayerManager
    lateinit var messenger: PlatformMessenger
    private lateinit var pluginUpdater: PluginUpdater
    var guiManager: GUIManager? = null
    var guiSystemEnabled = false

    val gsonInstance: Gson = GsonBuilder().create()

    var console: Audience = Audience.empty()
        set(value) {
            field = value
            logger = PlatformLogger(value)
        }

    val configuration = PlatformConfiguration(this)

    fun setupConfig() {
        configuration.setup()
    }

    fun setup(
        commandManager: PlatformCommandManager<S>,
        networkMessenger: PlatformMessenger
    ) {
        unsafeInstance = this
        messenger = networkMessenger
        setupConfig()
        pluginPrefix = configuration.getSection(GeneralSection::class.java).prefix.miniMessage()
        translatorManager = TranslatorManager(this)
        translatorManager.setup()
        platformCommandManager = commandManager
        platformCommandManager.setup()
        platformCommandManager.registerCommands()
        hookManager = HookManager(this)
        registerHooks()
        playerManager = PlayerManager(this)

        try {
            networkMessenger.register()
            logger.info(
                Component.text("Communication system established.", NamedTextColor.LIGHT_PURPLE)
            )
        } catch (ex: Throwable) {
            messenger = DummyPlatformMessenger()
            logger.error(
                Component.text("An exception has occurred while enabling communication system.", NamedTextColor.RED)
            )
            logger.error(
                Component.text("Fix this to enable communication between Proxy and Server.", NamedTextColor.RED)
            )
            ex.printStackTrace()
        }

        val updaterConfig = configuration.getSection(UpdatesSection::class.java)
        pluginUpdater = PluginUpdater(
            this,
            updaterConfig.checkInterval,
            updaterConfig.notify.console.notificationInterval,
            logger,
            pluginFolder,
            updaterConfig.notify.console.enable
        )
        if(updaterConfig.checkForUpdates) {
            // Only check for updates if enabled
            pluginUpdater.setup()
        }

        // register messenger messages
        setupMessenger()

        // Request all players data
        scheduleLater({
            messenger.sendMessage(
                RequestAllDataMessage(),
                null
            )
        }, 1, TimeUnit.SECONDS)
    }

    fun setupMessenger() {
        messenger.registerMessage("afk-state-update", AFKStateUpdateMessage::class.java)
        messenger.registerMessage("vanish-state-update", VanishStateUpdateMessage::class.java)
        messenger.registerMessage("request-all-data", RequestAllDataMessage::class.java)

        messenger.addListener(AfkStateChangeListener(this))
        messenger.addListener(VanishStateChangeListener(this))
    }

    fun disable() {
        // Close inventories from Protocolize
        guiManager?.disable()
        messenger.unregister()
    }

    fun reload() {
        // Reload Configuration
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Configuration", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        configuration.reload()
        // Reload Hooks
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Hooks", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        hookManager.reload()
        logger.info(
            Component.text("Registering ", NamedTextColor.GREEN)
                .append(Component.text("Hooks", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        registerHooks()
        // Reload Translator
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Translator", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        translatorManager.reload()
        // Reload Commands
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Commands", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        platformCommandManager.reload()
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Protocolize (GUI System)", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        // Reload Messenger
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Communication System", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        reloadMessenger()
        // Reload Updater
        logger.info(
            Component.text("Reloading ", NamedTextColor.GREEN)
                .append(Component.text("Updater", NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
        )
        pluginUpdater.stop()
        val updaterConfig = configuration.getSection(UpdatesSection::class.java)
        pluginUpdater = PluginUpdater(
            this,
            updaterConfig.checkInterval,
            updaterConfig.notify.console.notificationInterval,
            logger,
            pluginFolder,
            updaterConfig.notify.console.enable
        )
        if(updaterConfig.checkForUpdates) {
            // Only check for updates if enabled
            pluginUpdater.setup()
        }
        // Reload Protocolize
        guiManager?.reload()
        logger.info(
            Component.text("Plugin reloaded!", NamedTextColor.GREEN)
        )
        // Reload prefix
        pluginPrefix = configuration.getSection(GeneralSection::class.java).prefix.miniMessage()
    }

    abstract fun toPlatformServer(server: S): PlatformServer<S>

    abstract fun toPlatformExecutorPlayer(executor: P): PlatformExecutor<S>

    abstract fun toPlatformExecutorConsole(executor: C): PlatformExecutor<S>

    abstract fun getAllServers(): List<PlatformServer<S>>

    abstract fun getServerByName(name: String): PlatformServer<S>?

    abstract fun getConnectedPlayersAmount(): Int

    abstract fun registerHooks()

    abstract fun performCommandForPlayer(player: PlatformExecutor<S>, command: String)

    abstract fun getAllPlayers(): List<PlatformExecutor<S>>

    abstract fun scheduleLater(task: Runnable, time: Long, unit: TimeUnit)

    fun onPlayerJoin(executor: PlatformExecutor<S>) {
        val onJoinConfig = configuration.getSection(UpdatesSection::class.java).notify.onJoin
        if(pluginUpdater.updateAvailable && onJoinConfig.enable && executor.hasPermission(onJoinConfig.permission)) {
            val delay = max(onJoinConfig.delay, 1)
            scheduleLater({
                executor.asAudience().sendMessage(
                    AdventureUtil.parseMiniMessage(
                        translatorManager.getTranslator().getMessages().updater.notifyMessage.joinToString("<newline>"),
                        TagResolver.resolver("download-url",
                            Tag.selfClosingInserting(
                                Component.text(pluginUpdater.updateDownloadURL)
                                    .clickEvent(ClickEvent.openUrl(pluginUpdater.updateDownloadURL))
                            )
                        )
                    )
                )
            }, delay.toLong(), TimeUnit.MILLISECONDS)
        }
    }

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

        // In case that a server matches the group name...
        val server = allServers.firstOrNull { it.getName().equals(serverConfig.serverName, true) }

        if(server != null && !matchedServers.contains(server)) {
            // Ok, add it to the group
            matchedServers.add(server)
        }

        return matchedServers
    }

    abstract fun callAFKStateChangeEvent(fromPlayer: PlatformExecutor<S>, state: Boolean): CompletableFuture<Boolean>

    abstract fun callVanishStateChangeEvent(fromPlayer: PlatformExecutor<S>, state: Boolean): CompletableFuture<Boolean>

    abstract fun toPlatformComponent(component: Component): Any

    abstract fun getPlayerByName(name: String): PlatformExecutor<S>?

    abstract fun getPlayerByUUID(uuid: UUID): PlatformExecutor<S>?

    abstract fun reloadMessenger()

    companion object {

        var pluginPrefix: Component = Component.empty()

        lateinit var unsafeInstance: Platform<*,*,*>

    }

}
