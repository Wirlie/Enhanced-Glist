package dev.wirlie.glist.bungeecord.commands

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.commands.PlatformCommand
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.command.ConsoleCommandSender

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
            platformCommand.tryExecution(platform.toPlatformExecutorPlayer(sender))
        } else {
            platformCommand.tryExecution(platform.toPlatformExecutorConsole(sender as ConsoleCommandSender))
        }
    }

}
