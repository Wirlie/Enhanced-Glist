package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class BungeePlatformServer(
    server: ServerInfo
): PlatformServer<ServerInfo, ProxiedPlayer>(
    server
) {

    override fun getName(): String {
        return server.name
    }

    override fun getPlayers(): List<PlatformPlayer<ServerInfo, ProxiedPlayer>> {
        return server.players.map { BungeePlatformPlayer(it) }
    }

}
