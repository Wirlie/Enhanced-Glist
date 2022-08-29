package dev.wirlie.glist.bungeecord

import dev.wirlie.glist.bungeecord.platform.BungeePlatform
import dev.wirlie.glist.bungeecord.platform.BungeePlatformCommandManager
import dev.wirlie.glist.common.Platform
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

class EnhancedGlistBungeeCord: Plugin() {

    lateinit var platform: BungeePlatform

    override fun onEnable() {
        adventure = BungeeAudiences.create(this)
        platform = BungeePlatform()
        platform.pluginFolder = dataFolder
        platform.console = adventure.console()
        platform.setup(
            BungeePlatformCommandManager(platform, ProxyServer.getInstance().pluginManager, this)
        )
    }

    override fun onDisable() {
        platform.disable()
    }

    companion object {
        private lateinit var adventure: BungeeAudiences

        fun getAdventure(): BungeeAudiences {
            if (!this::adventure.isInitialized) {
                throw IllegalStateException("Cannot retrieve audience provider when plugin is not enabled")
            }
            return adventure
        }
    }

}
