package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer

class VelocityPlatformServer(
    server: RegisteredServer
): PlatformServer<RegisteredServer, Player>(
    server
) {

    override fun getName(): String {
        return server.serverInfo.name
    }

    override fun getPlayers(): List<PlatformPlayer<RegisteredServer, Player>> {
        return server.playersConnected.map { VelocityPlatformPlayer(it) }
    }

}
