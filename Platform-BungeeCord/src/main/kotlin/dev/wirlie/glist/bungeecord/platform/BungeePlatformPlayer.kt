package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class BungeePlatformPlayer(
    player: ProxiedPlayer
): PlatformPlayer<ServerInfo, ProxiedPlayer>(
    player
) {

    override fun getName(): String {
        return player.name
    }

    override fun getUUID(): UUID {
        return player.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<ServerInfo, ProxiedPlayer>? {
        val server = player.server ?: return null
        return BungeePlatformServer(server.info)
    }

}
