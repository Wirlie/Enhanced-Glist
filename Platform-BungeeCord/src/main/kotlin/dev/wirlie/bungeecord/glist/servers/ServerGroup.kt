package dev.wirlie.bungeecord.glist.servers

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.stream.Collectors

class ServerGroup(override val id: String) : ServerInfoProvider {

    var servers: List<ServerInfo> = ArrayList()

    override val playerCount: Int
        get() = servers.stream().mapToInt { s: ServerInfo -> s.players.size }.sum()

    override val players: List<ProxiedPlayer>
        get() = servers.stream().flatMap { s: ServerInfo -> s.players.stream() }.collect(Collectors.toList())

    override val displayName: String
        get() = id + " (" + servers.stream().map { obj: ServerInfo -> obj.name }.collect(Collectors.joining(", ")) + ")"

}
