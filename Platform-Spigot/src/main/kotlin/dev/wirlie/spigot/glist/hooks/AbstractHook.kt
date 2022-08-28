package dev.wirlie.spigot.glist.hooks

import dev.wirlie.spigot.glist.EnhancedGlistSpigot
import org.bukkit.entity.Player

interface AbstractHook {

    fun sendStateToBridge(player: Player, type: StateNotificationType, newValue: Boolean)

    fun sendAllPlayersStateToBridge()

    fun sendPlayerToBridge(player: Player)

    fun registerListeners(bridge: EnhancedGlistSpigot)

}