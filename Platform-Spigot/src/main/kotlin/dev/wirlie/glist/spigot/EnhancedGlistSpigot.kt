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

package dev.wirlie.glist.spigot

import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.messenger.api.MessengerLogger
import dev.wirlie.glist.messenger.impl.DummyPlatformMessenger
import dev.wirlie.glist.messenger.impl.RabbitMQMessenger
import dev.wirlie.glist.messenger.impl.RedisMessenger
import dev.wirlie.glist.spigot.configuration.ConfigurationManager
import dev.wirlie.glist.spigot.hooks.HookManager
import dev.wirlie.glist.spigot.listeners.PlayerJoinListener
import dev.wirlie.glist.spigot.messenger.SpigotPluginMessageMessenger
import dev.wirlie.glist.spigot.messenger.listeners.RequestAllDataListener
import dev.wirlie.glist.spigot.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.spigot.messenger.messages.RequestAllDataMessage
import dev.wirlie.glist.spigot.messenger.messages.VanishStateUpdateMessage
import dev.wirlie.glist.spigot.util.AdventureUtil
import dev.wirlie.glist.updater.PluginUpdater
import dev.wirlie.glist.updater.SimpleLogger
import dev.wirlie.glist.updater.UpdaterScheduler
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class EnhancedGlistSpigot: JavaPlugin(), SimpleLogger, UpdaterScheduler, MessengerLogger {

    lateinit var messenger: PlatformMessenger
    lateinit var hookManager: HookManager
    lateinit var configurationManager: ConfigurationManager
    private lateinit var pluginUpdater: PluginUpdater
    private var updaterCheckTask: BukkitTask? = null
    private var consoleNotificationTask: BukkitTask? = null

    override fun onEnable() {
        AdventureUtil.adventure = BukkitAudiences.create(this)

        configurationManager = ConfigurationManager(this)

        setupMessenger()

        hookManager = HookManager(this)

        hookManager.registerHooks()
        hookManager.sendAllPlayersToProxy()

        val updaterConfiguration = configurationManager.getConfiguration().updates
        pluginUpdater = PluginUpdater(
            this,
            updaterConfiguration.checkInterval,
            updaterConfiguration.notify.console.notificationInterval,
            this,
            dataFolder,
            updaterConfiguration.notify.console.enable
        )
        if(updaterConfiguration.checkForUpdates) {
            pluginUpdater.setup()
        }

        getCommand("egls")!!.executor = GlistExecutor(this)

        server.pluginManager.also { pluginManager ->
            pluginManager.registerEvents(PlayerJoinListener(this), this)
        }
    }

    override fun onDisable() {
        pluginUpdater.stop()
        messenger.unregister()
    }

    private fun setupMessenger() {
        val communicationConfig = configurationManager.getConfiguration().communication

        when (communicationConfig.type.lowercase()) {
            "plugin-messages" -> {
                logger.info("Enabling communication using plugin messages.")
                messenger = SpigotPluginMessageMessenger(this)
            }
            "rabbitmq" -> {
                logger.info("Enabling communication using RabbitMQ.")
                messenger = RabbitMQMessenger(
                    this,
                    communicationConfig.rabbitmqServer.host,
                    communicationConfig.rabbitmqServer.port,
                    communicationConfig.rabbitmqServer.user,
                    communicationConfig.rabbitmqServer.password,
                    false
                )
            }
            "redis" -> {
                logger.info("Enabling communication using Redis.")
                messenger = RedisMessenger(
                    this,
                    communicationConfig.redisServer.host,
                    communicationConfig.redisServer.port,
                    communicationConfig.redisServer.user,
                    communicationConfig.redisServer.password,
                    false
                )
            }
            else -> {
                messenger = DummyPlatformMessenger()
                logger.severe("Unknown communication type: '${communicationConfig.type}'.")
                logger.severe("Fix this to enable communication between Proxy and Server.")
            }
        }

        try {
            messenger.register()
        } catch (ex: Throwable) {
            messenger = DummyPlatformMessenger()
            logger.severe("An exception has occurred while enabling communication system.")
            logger.severe("Fix this to enable communication between Proxy and Server.")
            ex.printStackTrace()
        }

        // Messenger message registration
        messenger.registerMessage("request-all-data", RequestAllDataMessage::class.java)
        messenger.registerMessage("afk-state-update", AFKStateUpdateMessage::class.java)
        messenger.registerMessage("vanish-state-update", VanishStateUpdateMessage::class.java)
        // Messenger listener registration
        messenger.addListener(RequestAllDataListener(this))
    }

    fun performReload() {
        messenger.unregister()
        configurationManager.reload()
        setupMessenger()
        hookManager.reload()
        pluginUpdater.stop()
        val updaterConfiguration = configurationManager.getConfiguration().updates
        pluginUpdater = PluginUpdater(
            this,
            updaterConfiguration.checkInterval,
            updaterConfiguration.notify.console.notificationInterval,
            this,
            dataFolder,
            updaterConfiguration.notify.console.enable
        )
        if(updaterConfiguration.checkForUpdates) {
            pluginUpdater.setup()
        }
    }

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warning(message: String) {
        logger.warning(message)
    }

    override fun severe(message: String) {
        logger.severe(message)
    }

    override fun scheduleUpdaterCheckTask(task: Runnable, periodSeconds: Int) {
        updaterCheckTask = object: BukkitRunnable() {
            override fun run() {
                task.run()
            }
        }.runTaskTimer(this, 1, periodSeconds * 20L)
    }

    override fun stopUpdaterCheckTask() {
        updaterCheckTask?.cancel()
    }

    override fun scheduleConsoleNotificationTask(task: Runnable, periodSeconds: Int) {
        consoleNotificationTask = object: BukkitRunnable() {
            override fun run() {
                task.run()
            }
        }.runTaskTimer(this, periodSeconds * 20L, periodSeconds * 20L)
    }

    override fun stopConsoleNotificationTask() {
        consoleNotificationTask?.cancel()
    }

}
