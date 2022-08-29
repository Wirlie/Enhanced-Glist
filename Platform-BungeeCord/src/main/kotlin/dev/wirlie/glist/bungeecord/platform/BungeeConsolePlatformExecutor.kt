package dev.wirlie.glist.bungeecord.platform

import dev.wirlie.glist.bungeecord.EnhancedGlistBungeeCord
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import net.md_5.bungee.api.config.ServerInfo
import java.util.*

class BungeeConsolePlatformExecutor: PlatformExecutor<ServerInfo>() {

    override fun isPlayer(): Boolean {
        return false
    }

    override fun isConsole(): Boolean {
        return true
    }

    override fun asAudience(): Audience {
        return EnhancedGlistBungeeCord.getAdventure().console()
    }

    override fun getName(): String {
        return "Console"
    }

    override fun getUUID(): UUID {
        return UUID.randomUUID()
    }

    override fun hasPermission(permission: String): Boolean {
        return true
    }

    override fun getConnectedServer(): PlatformServer<ServerInfo>? {
        return null
    }

}
