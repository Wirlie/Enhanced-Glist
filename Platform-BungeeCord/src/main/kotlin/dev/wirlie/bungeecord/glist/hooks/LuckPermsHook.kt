package dev.wirlie.bungeecord.glist.hooks

import dev.wirlie.bungeecord.glist.config.Config
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.query.QueryOptions
import net.md_5.bungee.api.connection.ProxiedPlayer

class LuckPermsHook : GroupHook(Config.BEHAVIOUR__GROUPS_PREFIX__PRIORITY__LUCKPERMS.get()) {

    override fun getPrefix(player: ProxiedPlayer): String {
        var prefix: String? = null
        val luckPerms = LuckPermsProvider.get()
        val user = luckPerms.userManager.getUser(player.uniqueId)

        if (user != null) {
            prefix = user
                .cachedData
                .getMetaData(QueryOptions.defaultContextualOptions())
                .prefix
        }

        if (prefix == null || prefix.equals("null", ignoreCase = true)) {
            prefix = ""
        }

        return prefix
    }

    override fun reload() {}
}
