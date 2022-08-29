package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.ConsoleCommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import java.util.*

class VelocityConsolePlatformExecutor(
    val console: ConsoleCommandSource
): PlatformExecutor<RegisteredServer>() {

    override fun isConsole(): Boolean {
        return true
    }

    override fun isPlayer(): Boolean {
        return false
    }

    override fun asAudience(): Audience {
        return console
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

    override fun getConnectedServer(): PlatformServer<RegisteredServer>? {
        return null
    }

}
