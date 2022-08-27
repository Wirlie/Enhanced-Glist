package dev.wirlie.bungeecord.glist.servers

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer

class BungeecordInfoProvider(private val info: ServerInfo) : ServerInfoProvider {

    override val id: String
        get() = info.name

    override val playerCount: Int
        get() = info.players.size

    override val players: List<ProxiedPlayer>
        get() = ArrayList(info.players)

    override val displayName: String
        get () = info.name

}
