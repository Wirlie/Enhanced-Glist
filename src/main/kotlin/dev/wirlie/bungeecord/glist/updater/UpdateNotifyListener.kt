package dev.wirlie.bungeecord.glist.updater

import dev.wirlie.bungeecord.glist.EnhancedBCL
import dev.wirlie.bungeecord.glist.config.Config
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.concurrent.TimeUnit

class UpdateNotifyListener(private val plugin: EnhancedBCL) : Listener {

    @EventHandler
    fun event(e: PostLoginEvent) {
        val player = e.player

        if (Config.UPDATES__NOTIFY__ENABLE.get()) {
            if (player.hasPermission(Config.UPDATES__NOTIFY__PERMISSION.get())) {
                val delay = Config.UPDATES__NOTIFY__DELAY_MS.get()

                if (delay < 1) {
                    sendNotification(player)
                } else {
                    BungeeCord.getInstance().scheduler.schedule(
                        plugin,
                        { sendNotification(player) },
                        delay.toLong(),
                        TimeUnit.MILLISECONDS
                    )
                }
            }
        }
    }

    private fun sendNotification(player: ProxiedPlayer) {
        val audience = plugin.adventure().player(player)
        val rawMessages = Config.UPDATES__NOTIFY__MESSAGE.get()
        for (line in rawMessages) {
            audience.sendMessage(EnhancedBCL.defaultLegacyDeserializer.deserialize(line))
        }
    }

}
