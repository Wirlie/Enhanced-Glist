package dev.wirlie.bungeecord.glist.hooks

import net.md_5.bungee.api.connection.ProxiedPlayer

abstract class GroupHook(val priority: Int) {

    abstract fun getPrefix(player: ProxiedPlayer): String?

    abstract fun reload()

}
