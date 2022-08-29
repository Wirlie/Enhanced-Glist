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
