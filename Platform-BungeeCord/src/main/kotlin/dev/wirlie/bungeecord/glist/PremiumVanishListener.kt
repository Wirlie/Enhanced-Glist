package dev.wirlie.bungeecord.glist

import de.myzelyam.api.vanish.BungeePlayerHideEvent
import de.myzelyam.api.vanish.BungeePlayerShowEvent
import de.myzelyam.api.vanish.BungeeVanishAPI
import dev.wirlie.bungeecord.glist.activity.ActivityType
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class PremiumVanishListener(private val plugin: EnhancedBCL) : Listener {

    @EventHandler
    fun event(e: BungeePlayerShowEvent) {
        plugin.activityManager.removeActivity(e.player.uniqueId, ActivityType.VANISH)
    }

    @EventHandler
    fun event(e: BungeePlayerHideEvent) {
        plugin.activityManager.addActivity(e.player.uniqueId, ActivityType.VANISH)
    }

    @EventHandler
    fun event(e: PostLoginEvent) {
        if (BungeeVanishAPI.isInvisible(e.player)) {
            plugin.activityManager.addActivity(e.player.uniqueId, ActivityType.VANISH)
        }
    }

    fun initialHandle() {
        for (p in BungeeCord.getInstance().players) {
            if (BungeeVanishAPI.isInvisible(p)) {
                plugin.activityManager.addActivity(p.uniqueId, ActivityType.VANISH)
            }
        }
    }
}
