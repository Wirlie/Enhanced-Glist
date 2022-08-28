package dev.wirlie.glist.common.platform

import java.util.UUID

abstract class PlatformPlayer<S, P>(
    val player: P
) {

    abstract fun getName(): String

    abstract fun getUUID(): UUID

    abstract fun hasPermission(permission: String): Boolean

    abstract fun getConnectedServer(): PlatformServer<S, P>?

}
