package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.bungeecord.EnhancedGlistBungeeCord
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class BungeePlayerPlatformExecutor(
    val player: ProxiedPlayer
): PlatformExecutor<ServerInfo>() {

    override fun isPlayer(): Boolean {
        return true
    }

    override fun isConsole(): Boolean {
        return false
    }

    override fun asAudience(): Audience {
        return EnhancedGlistBungeeCord.getAdventure().player(player)
    }

    override fun getName(): String {
        return player.name
    }

    override fun getUUID(): UUID {
        return player.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<ServerInfo>? {
        val server = player.server ?: return null
        return BungeePlatformServer(server.info)
    }

}
