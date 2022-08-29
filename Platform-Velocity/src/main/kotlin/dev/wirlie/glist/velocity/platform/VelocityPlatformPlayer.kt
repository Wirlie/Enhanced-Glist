package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import java.util.*

class VelocityPlatformPlayer(
    player: Player
): PlatformPlayer<RegisteredServer, Player>(
    player
) {

    override fun getName(): String {
        return player.username
    }

    override fun getUUID(): UUID {
        return player.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return player.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<RegisteredServer, Player>? {
        val server = player.currentServer.orElse(null) ?: return null
        return VelocityPlatformServer(server.server)
    }

}
