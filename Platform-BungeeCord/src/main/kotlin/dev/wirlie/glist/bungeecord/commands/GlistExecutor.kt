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

package dev.wirlie.glist.bungeecord.commands

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.commands.PlatformCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.command.ConsoleCommandSender

/**
 * Executor for /glist
 * @param platform BungeeCord platform instance
 * @param platformCommand BungeeCord platform command instance
 */
class GlistExecutor(
    private val platform: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>,
    private val platformCommand: PlatformCommand<ServerInfo>
): Command(
    platformCommand.name,
    platformCommand.permission,
    *platformCommand.aliases.toTypedArray()
) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(sender is ProxiedPlayer) {
            platformCommand.tryExecution(platform.toPlatformExecutorPlayer(sender), args)
        } else {
            platformCommand.tryExecution(platform.toPlatformExecutorConsole(sender as ConsoleCommandSender), args)
        }
    }

}
