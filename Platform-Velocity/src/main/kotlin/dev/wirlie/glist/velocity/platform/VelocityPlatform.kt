package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer

class VelocityPlatform: Platform<RegisteredServer, Player>() {

    override fun toPlatform(server: RegisteredServer): PlatformServer<RegisteredServer, Player> {
        return VelocityPlatformServer(server)
    }

    override fun toPlatform(player: Player): PlatformPlayer<RegisteredServer, Player> {
        return VelocityPlatformPlayer(player)
    }

}
