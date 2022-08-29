package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.config.ServerInfo

class BungeePlatformServer(
    server: ServerInfo
): PlatformServer<ServerInfo>(
    server
) {

    override fun getName(): String {
        return server.name
    }

    override fun getPlayers(): List<PlatformExecutor<ServerInfo>> {
        return server.players.map { BungeePlayerPlatformExecutor(it) }
    }

}
