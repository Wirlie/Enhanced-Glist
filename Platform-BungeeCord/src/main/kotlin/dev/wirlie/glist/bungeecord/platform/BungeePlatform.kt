package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class BungeePlatform: Platform<ServerInfo, ProxiedPlayer>() {

    override fun toPlatform(server: ServerInfo): PlatformServer<ServerInfo, ProxiedPlayer> {
        return BungeePlatformServer(server)
    }

    override fun toPlatform(player: ProxiedPlayer): PlatformPlayer<ServerInfo, ProxiedPlayer> {
        return BungeePlatformPlayer(player)
    }

}
