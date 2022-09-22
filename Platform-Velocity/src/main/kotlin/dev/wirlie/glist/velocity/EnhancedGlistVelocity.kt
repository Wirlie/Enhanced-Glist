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

package dev.wirlie.glist.velocity

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.wirlie.glist.common.configuration.sections.CommunicationSection
import dev.wirlie.glist.messenger.impl.DummyPlatformMessenger
import dev.wirlie.glist.messenger.PlatformMessenger
import dev.wirlie.glist.messenger.impl.RabbitMQMessenger
import dev.wirlie.glist.messenger.impl.RedisMessenger
import dev.wirlie.glist.velocity.api.impl.EnhancedGlistAPIImpl
import dev.wirlie.glist.velocity.listener.PlayerDisconnectListener
import dev.wirlie.glist.velocity.listener.PlayerJoinListener
import dev.wirlie.glist.velocity.listener.PlayerServerChangeListener
import dev.wirlie.glist.velocity.platform.VelocityPlatform
import dev.wirlie.glist.velocity.platform.VelocityPlatformCommandManager
import dev.wirlie.glist.velocity.platform.messenger.VelocityPluginMessageMessenger
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.nio.file.Path

@Plugin(
    id = "enhanced-glist-velocity",
    name = "EnhancedGlist",
    version = BuildConstants.VERSION,
    url = "https://www.spigotmc.org/resources/enhancedbungeelist.53295/",
    description = "Enhanced Glist is a high-configurable plugin that enhances /glist command.",
    authors = ["Wirlie"],
    dependencies = [Dependency(id = "luckperms", optional = true)]
)
class EnhancedGlistVelocity {

    @Inject
    @DataDirectory
    lateinit var pluginDirectory: Path

    @Inject
    lateinit var proxyServer: ProxyServer

    @Inject
    lateinit var commandManager: CommandManager

    lateinit var platform: VelocityPlatform

    private lateinit var messenger: PlatformMessenger

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        platform = VelocityPlatform(this, proxyServer)
        platform.pluginFolder = pluginDirectory.toFile()
        platform.console = proxyServer.consoleCommandSource
        platform.setupConfig()

        val communicationConfig = platform.configuration.getSection(CommunicationSection::class.java)

        when (communicationConfig.type.lowercase()) {
            "plugin-messages" -> {
                platform.logger.info(
                    Component.text("Enabling communication using plugin messages.", NamedTextColor.LIGHT_PURPLE)
                )
                messenger = VelocityPluginMessageMessenger(proxyServer)
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

        platform.setup(
            VelocityPlatformCommandManager(platform, commandManager),
            messenger
        )

        proxyServer.eventManager.register(this, PlayerDisconnectListener(platform))
        proxyServer.eventManager.register(this, PlayerJoinListener(platform))
        proxyServer.eventManager.register(this, PlayerServerChangeListener(platform))
        proxyServer.eventManager.register(this, messenger)

        // Init API
        EnhancedGlistAPIImpl(platform)
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        platform.disable()
    }

}
