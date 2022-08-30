/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022  Josue Acevedo
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
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.configuration.sections.DoNotEditSection
import dev.wirlie.glist.common.configuration.sections.GroupServersSection
import dev.wirlie.glist.velocity.platform.VelocityPlatform
import dev.wirlie.glist.velocity.platform.VelocityPlatformCommandManager
import net.kyori.adventure.text.Component
import java.nio.file.Path

@Plugin(
    id = "enhanced-glist-velocity",
    name = "EnhancedGlist",
    version = "2.0.0",
    url = "https://www.spigotmc.org/resources/enhancedbungeelist.53295/",
    description = "Enhanced Glist/ is a high-configurable plugin that enhances /glist command.",
    authors = ["Wirlie"],
    dependencies = [Dependency(id = "luckperms", optional = true)]
)
class EnhancedGlistVelocity {

    @Inject
    @DataDirectory
    lateinit var pluginDirectory: Path

    @Inject
    lateinit var server: ProxyServer

    @Inject
    lateinit var commandManager: CommandManager

    lateinit var platform: VelocityPlatform

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        platform = VelocityPlatform(server)
        platform.pluginFolder = pluginDirectory.toFile()
        platform.console = server.consoleCommandSource
        platform.setup(VelocityPlatformCommandManager(platform, commandManager))

        // Test
        val section = platform.configuration.getSection(GroupServersSection::class.java)
        platform.logger.error(Component.text("TEST: ${section?.servers?.joinToString(", ") { sv -> "${sv.serverName}|${sv.byName.size}|${sv.byPattern.size}" }}"))
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        platform.disable()
    }

}
