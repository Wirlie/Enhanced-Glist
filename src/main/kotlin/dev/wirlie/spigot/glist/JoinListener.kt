package dev.wirlie.spigot.glist

import dev.wirlie.spigot.glist.hooks.AbstractHook
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Consumer

class JoinListener(private val bridge: EnhancedBCLBridge) : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun event(e: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                bridge.getHooks().forEach(Consumer { h: AbstractHook -> h.sendPlayerToBridge(e.player) })
            }
        }.runTask(bridge)
    }
}
