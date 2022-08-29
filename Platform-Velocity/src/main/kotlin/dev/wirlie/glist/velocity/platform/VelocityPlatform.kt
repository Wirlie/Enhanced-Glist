package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

class VelocityPlatform(
    val server: ProxyServer
): Platform<RegisteredServer, Player, ConsoleCommandSource>() {

    override fun toPlatformServer(server: RegisteredServer): PlatformServer<RegisteredServer> {
        return VelocityPlatformServer(server)
    }

    override fun toPlatformExecutorPlayer(executor: Player): PlatformExecutor<RegisteredServer> {
        return VelocityPlayerPlatformExecutor(executor)
    }

    override fun toPlatformExecutorConsole(executor: ConsoleCommandSource): PlatformExecutor<RegisteredServer> {
        return VelocityConsolePlatformExecutor(server.consoleCommandSource)
    }

    override fun getAllServers(): List<PlatformServer<RegisteredServer>> {
        return server.allServers.map { VelocityPlatformServer(it) }
    }

}
