package dev.wirlie.bungeecord.glist.util

import dev.wirlie.bungeecord.glist.activity.ActivityType
import net.md_5.bungee.api.connection.ProxiedPlayer

class PlayerGlistEntry(val player: ProxiedPlayer, val prefix: String, var activities: MutableSet<ActivityType>)
