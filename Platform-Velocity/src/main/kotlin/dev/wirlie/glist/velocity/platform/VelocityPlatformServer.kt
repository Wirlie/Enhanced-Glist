package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

class VelocityPlatformServer(
    server: RegisteredServer
): PlatformServer<RegisteredServer>(
    server
) {

    override fun getName(): String {
        return server.serverInfo.name
    }

    override fun getPlayers(): List<PlatformExecutor<RegisteredServer>> {
        return server.playersConnected.map { VelocityPlayerPlatformExecutor(it) }
    }

}
