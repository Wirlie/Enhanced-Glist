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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformCommandManager
import dev.wirlie.glist.velocity.commands.GlistExecutor

class VelocityPlatformCommandManager(
    private val platformInstance: Platform<RegisteredServer, Player, ConsoleCommandSource>,
    private val commandManager: CommandManager
): PlatformCommandManager<RegisteredServer>(
    platformInstance
) {

    override fun registerCommands() {
        val meta = commandManager.metaBuilder(glistCommand.name)
            .aliases(*glistCommand.aliases.toTypedArray())
            .build()

        commandManager.register(meta, GlistExecutor(platformInstance, glistCommand))
    }

}
