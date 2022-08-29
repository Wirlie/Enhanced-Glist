package dev.wirlie.glist.bungeecord

import dev.wirlie.glist.bungeecord.platform.BungeePlatform
import dev.wirlie.glist.common.Platform
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.plugin.Plugin

class EnhancedGlistBungeeCord: Plugin() {

    private lateinit var adventure: BungeeAudiences
    lateinit var platform: BungeePlatform

    override fun onEnable() {
        adventure = BungeeAudiences.create(this)
        platform = BungeePlatform()
        platform.pluginFolder = dataFolder
        platform.console = adventure.console()
        platform.setup()
    }

    override fun onDisable() {
        platform.disable()
    }

    fun getAdventure(): BungeeAudiences {
        if(!this::adventure.isInitialized) {
            throw IllegalStateException("Cannot retrieve audience provider when plugin is not enabled")
        }
        return adventure
    }

}
