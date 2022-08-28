package dev.wirlie.glist.common.platform

abstract class PlatformServer<S, P>(
    val server: S
) {

    abstract fun getName(): String

    abstract fun getPlayers(): List<PlatformPlayer<S, P>>

}
