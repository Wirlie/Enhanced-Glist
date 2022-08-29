package dev.wirlie.glist.velocity.commands

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.commands.PlatformCommand

class GlistExecutor(
    private val platform: Platform<RegisteredServer, Player, ConsoleCommandSource>,
    private val platformCommand: PlatformCommand<RegisteredServer>
): SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        if(invocation.source() is Player) {
            platformCommand.tryExecution(platform.toPlatformExecutorPlayer(invocation.source() as Player))
        } else {
            platformCommand.tryExecution(platform.toPlatformExecutorConsole(invocation.source() as ConsoleCommandSource))
        }
    }

    override fun hasPermission(invocation: SimpleCommand.Invocation): Boolean {
        return invocation.source().hasPermission(platformCommand.permission)
    }

}
