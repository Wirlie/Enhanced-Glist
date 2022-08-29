package dev.wirlie.glist.common.platform

abstract class PlatformServer<S>(
    val server: S
) {

    abstract fun getName(): String

    abstract fun getPlayers(): List<PlatformExecutor<S>>

}
