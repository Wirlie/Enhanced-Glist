package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.bungeecord.EnhancedGlistBungeeCord
import dev.wirlie.glist.bungeecord.commands.GlistExecutor
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.PlatformCommandManager
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.PluginManager
import net.md_5.bungee.command.ConsoleCommandSender

class BungeePlatformCommandManager(
    private val platformInstance: Platform<ServerInfo, ProxiedPlayer, ConsoleCommandSender>,
    private val pluginManager: PluginManager,
    private val plugin: EnhancedGlistBungeeCord
): PlatformCommandManager<ServerInfo>(
    platformInstance
) {

    override fun registerCommands() {
        pluginManager.registerCommand(plugin, GlistExecutor(platformInstance, glistCommand))
    }

}
