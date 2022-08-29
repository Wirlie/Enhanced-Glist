package dev.wirlie.glist.common

import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience
import java.io.File

abstract class Platform<S, P> {

    lateinit var pluginFolder: File
    lateinit var logger: PlatformLogger

    var console: Audience = Audience.empty()
        set(value) {
            field = value
            logger = PlatformLogger(value)
        }

    val configuration = PlatformConfiguration(this)

    fun setup() {
        configuration.setup()
    }

    fun disable() {

    }

    fun reload() {

    }

    abstract fun toPlatform(server: S): PlatformServer<S, P>

    abstract fun toPlatform(player: P): PlatformPlayer<S, P>

}
