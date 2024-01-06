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

package dev.wirlie.glist.velocity.commands

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.commands.PlatformCommand

class EglExecutor(
    val platform: Platform<RegisteredServer, Player, ConsoleCommandSource>,
    private val platformCommand: PlatformCommand<RegisteredServer>
): SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        if(invocation.source() is Player) {
            platformCommand.tryExecution(platform.toPlatformExecutorPlayer(invocation.source() as Player), invocation.arguments())
        } else {
            platformCommand.tryExecution(platform.toPlatformExecutorConsole(invocation.source() as ConsoleCommandSource), invocation.arguments())
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission(platformCommand.permission)
    }

    @Suppress("DuplicatedCode")
    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val sender = invocation.source()
        val args = invocation.arguments()

        return when (sender) {
            is Player -> {
                platformCommand.handleTabCompletion(platform.toPlatformExecutorPlayer(sender), args)
            }
            is ConsoleCommandSource -> {
                platformCommand.handleTabCompletion(platform.toPlatformExecutorConsole(sender), args)
            }
            else -> {
                listOf()
            }
        }
    }

}
