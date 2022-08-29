package dev.wirlie.glist.common.platform

import net.kyori.adventure.audience.Audience
import java.util.UUID

abstract class PlatformExecutor<S> {

    abstract fun isConsole(): Boolean

    abstract fun isPlayer(): Boolean

    abstract fun asAudience(): Audience

    abstract fun getName(): String

    abstract fun getUUID(): UUID

    abstract fun hasPermission(permission: String): Boolean

    abstract fun getConnectedServer(): PlatformServer<S>?

}
