package dev.wirlie.bungeecord.glist.hooks

import dev.wirlie.bungeecord.glist.EnhancedBCL
import dev.wirlie.bungeecord.glist.config.Config
import dev.wirlie.bungeecord.glist.groups.Group
import dev.wirlie.bungeecord.glist.groups.GroupManager
import net.md_5.bungee.api.connection.ProxiedPlayer

class InternalGroupSystemHook(plugin: EnhancedBCL) : GroupHook(
    Config.BEHAVIOUR__GROUPS_PREFIX__PRIORITY__INTERNAL_GROUP_SYSTEM.get()
) {

    private val manager: GroupManager

    init {
        manager = GroupManager(plugin)
        manager.loadGroups()
    }

    override fun getPrefix(player: ProxiedPlayer): String? {
        return manager.getGroup(player).map { g: Group -> g.prefix + g.nameColor }
            .orElse(null)
    }

    override fun reload() {
        manager.loadGroups()
    }

}
