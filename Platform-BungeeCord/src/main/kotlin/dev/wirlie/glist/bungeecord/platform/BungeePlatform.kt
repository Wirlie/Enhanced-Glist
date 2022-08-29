package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.command.ConsoleCommandSender

class BungeePlatform: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>() {

    override fun toPlatformServer(server: ServerInfo): PlatformServer<ServerInfo> {
        return BungeePlatformServer(server)
    }

    override fun toPlatformExecutorPlayer(executor: ProxiedPlayer): PlatformExecutor<ServerInfo> {
        return BungeePlayerPlatformExecutor(executor)
    }

    override fun toPlatformExecutorConsole(executor: ConsoleCommandSender): PlatformExecutor<ServerInfo> {
        return BungeeConsolePlatformExecutor()
    }

    override fun getAllServers(): List<PlatformServer<ServerInfo>> {
        return ProxyServer.getInstance().servers.values.map { BungeePlatformServer(it) }
    }

    override fun getConnectedPlayersAmount(): Int {
        return ProxyServer.getInstance().players.size
    }

}
