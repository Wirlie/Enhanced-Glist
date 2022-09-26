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

package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformCommandManager
import dev.wirlie.glist.velocity.commands.EglExecutor
import dev.wirlie.glist.velocity.commands.GlistExecutor
import dev.wirlie.glist.velocity.commands.SlistExecutor

/**
 * Velocity implementation for command management.
 * @param platformInstance Velocity platform instance.
 * @param commandManager Velocity command manager instance.
 */
class VelocityPlatformCommandManager(
    private val platformInstance: Platform<RegisteredServer, Player, ConsoleCommandSource>,
    private val commandManager: CommandManager
): PlatformCommandManager<RegisteredServer>(
    platformInstance
) {

    private val registeredMeta = mutableListOf<CommandMeta>()

    override fun registerCommands() {
        commandManager.register(
            commandManager
                .metaBuilder(glistCommand.name)
                .aliases(*glistCommand.aliases.toTypedArray())
                .build().also {
                  registeredMeta.add(it)
                },
            GlistExecutor(platformInstance, glistCommand)
        )

        commandManager.register(
            commandManager
                .metaBuilder(slistCommand.name)
                .aliases(*slistCommand.aliases.toTypedArray())
                .build().also {
                    registeredMeta.add(it)
                },
            SlistExecutor(platformInstance, slistCommand)
        )

        commandManager.register(
            commandManager
                .metaBuilder(eglCommand.name)
                .aliases(*eglCommand.aliases.toTypedArray())
                .build().also {
                    registeredMeta.add(it)
                },
            EglExecutor(platformInstance, eglCommand)
        )
    }

    override fun unregisterCommands() {
        for(meta in registeredMeta) {
            commandManager.unregister(meta)
        }
    }

}
