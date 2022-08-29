package dev.wirlie.glist.velocity.platform

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import java.util.*

class VelocityPlayerPlatformExecutor(
    private val executor: Player
): PlatformExecutor<RegisteredServer>() {

    override fun isConsole(): Boolean {
        return false
    }

    override fun isPlayer(): Boolean {
        return true
    }

    override fun asAudience(): Audience {
        return executor
    }

    override fun getName(): String {
        return executor.username
    }

    override fun getUUID(): UUID {
        return executor.uniqueId
    }

    override fun hasPermission(permission: String): Boolean {
        return executor.hasPermission(permission)
    }

    override fun getConnectedServer(): PlatformServer<RegisteredServer>? {
        val server = executor.currentServer.orElse(null) ?: return null
        return VelocityPlatformServer(server.server)
    }

}
