package dev.wirlie.bungeecord.glist.servers

import net.md_5.bungee.api.connection.ProxiedPlayer

interface ServerInfoProvider {

    val id: String

    val displayName: String

    val playerCount: Int

    val players: List<ProxiedPlayer>

}
