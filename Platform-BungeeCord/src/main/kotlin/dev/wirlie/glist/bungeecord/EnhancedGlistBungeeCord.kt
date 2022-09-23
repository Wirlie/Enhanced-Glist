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

package dev.wirlie.glist.bungeecord

import dev.wirlie.glist.bungeecord.api.impl.EnhancedGlistAPIImpl
import dev.wirlie.glist.bungeecord.listener.PlayerDisconnectListener
import dev.wirlie.glist.bungeecord.listener.PlayerJoinListener
import dev.wirlie.glist.bungeecord.listener.PlayerServerChangeListener
import dev.wirlie.glist.bungeecord.platform.BungeePlatform
import dev.wirlie.glist.bungeecord.platform.BungeePlatformCommandManager
import dev.wirlie.glist.bungeecord.platform.messenger.BungeePluginMessageMessenger
import dev.wirlie.glist.messenger.impl.RabbitMQMessenger
import dev.wirlie.glist.common.configuration.sections.CommunicationSection
import dev.wirlie.glist.messenger.impl.DummyPlatformMessenger
import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.messenger.impl.RedisMessenger
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

class EnhancedGlistBungeeCord: Plugin() {

    lateinit var platform: BungeePlatform
    private lateinit var messenger: PlatformMessenger

    override fun onEnable() {
        adventure = BungeeAudiences.create(this)
        platform = BungeePlatform(this) {
            // Reload Messenger
            reloadMessenger()
        }
        platform.pluginFolder = dataFolder
        platform.console = adventure.console()
        platform.setupConfig()

        setupMessenger()

        platform.setup(
            BungeePlatformCommandManager(platform, ProxyServer.getInstance().pluginManager, this),
            messenger
        )

        val proxy = ProxyServer.getInstance()
        val pluginManager = proxy.pluginManager

        pluginManager.registerListener(this, PlayerDisconnectListener(platform))
        pluginManager.registerListener(this, PlayerJoinListener(platform))
        pluginManager.registerListener(this, PlayerServerChangeListener(platform))

        if(messenger is BungeePluginMessageMessenger) {
            pluginManager.registerListener(this, messenger as BungeePluginMessageMessenger)
        }

        // Init API
        EnhancedGlistAPIImpl(platform)
    }

    override fun onDisable() {
        platform.disable()
    }

    private fun setupMessenger() {
        val communicationConfig = platform.configuration.getSection(CommunicationSection::class.java)

        when (communicationConfig.type.lowercase()) {
            "plugin-messages" -> {
                platform.logger.info(
                    Component.text("Enabling communication using plugin messages.", NamedTextColor.LIGHT_PURPLE)
                )
                messenger = BungeePluginMessageMessenger()
            }
            "rabbitmq" -> {
                platform.logger.info(
                    Component.text("Enabling communication using RabbitMQ.", NamedTextColor.LIGHT_PURPLE)
                )
                messenger = RabbitMQMessenger(
                    platform.logger,
                    communicationConfig.rabbitmqServer.host,
                    communicationConfig.rabbitmqServer.port,
                    communicationConfig.rabbitmqServer.user,
                    communicationConfig.rabbitmqServer.password,
                    true
                )
            }
            "redis" -> {
                platform.logger.info(
                    Component.text("Enabling communication using Redis.", NamedTextColor.LIGHT_PURPLE)
                )
                messenger = RedisMessenger(
                    platform.logger,
                    communicationConfig.redisServer.host,
                    communicationConfig.redisServer.port,
                    communicationConfig.redisServer.user,
                    communicationConfig.redisServer.password,
                    true
                )
            }
            else -> {
                messenger = DummyPlatformMessenger()
                platform.logger.error(
                    Component.text("Unknown communication type: '${communicationConfig.type}'.", NamedTextColor.RED)
                )
                platform.logger.error(
                    Component.text("Fix this to enable communication between Proxy and Server.", NamedTextColor.RED)
                )
            }
        }
    }

    private fun reloadMessenger() {
        if (this::messenger.isInitialized) {
            messenger.unregister()
        }
        setupMessenger()
        try {
            messenger.register()
        } catch (ex: Throwable) {
            messenger = DummyPlatformMessenger()
            platform.logger.error(
                Component.text("An exception has occurred while enabling communication system.", NamedTextColor.RED)
            )
            platform.logger.error(
                Component.text("Fix this to enable communication between Proxy and Server.", NamedTextColor.RED)
            )
            ex.printStackTrace()
        }
        platform.messenger = messenger
        platform.setupMessenger()
    }

    companion object {
        private lateinit var adventure: BungeeAudiences

        fun getAdventure(): BungeeAudiences {
            if (!this::adventure.isInitialized) {
                throw IllegalStateException("Cannot retrieve audience provider when plugin is not enabled")
            }
            return adventure
        }
    }

}
