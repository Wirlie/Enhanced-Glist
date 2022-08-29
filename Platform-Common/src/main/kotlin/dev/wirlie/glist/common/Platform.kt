package dev.wirlie.glist.common

import dev.wirlie.glist.common.configuration.PlatformConfiguration
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.extensions.miniMessage
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.translation.TranslatorManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.io.File

abstract class Platform<S, P, C> {

    lateinit var pluginFolder: File
    lateinit var logger: PlatformLogger

    lateinit var translatorManager: TranslatorManager
    lateinit var platformCommandManager: PlatformCommandManager<S>

    var console: Audience = Audience.empty()
        set(value) {
            field = value
            logger = PlatformLogger(value)
        }

    val configuration = PlatformConfiguration(this)

    fun setup(
        commandManager: PlatformCommandManager<S>
    ) {
        configuration.setup()
        pluginPrefix = configuration.getSection(GeneralSection::class.java)?.prefix?.miniMessage()?: Component.empty()
        translatorManager = TranslatorManager(this)
        translatorManager.setup()
        platformCommandManager = commandManager
        platformCommandManager.setup()
        platformCommandManager.registerCommands()
    }

    fun disable() {

    }

    fun reload() {

    }

    abstract fun toPlatformServer(server: S): PlatformServer<S>

    abstract fun toPlatformExecutorPlayer(executor: P): PlatformExecutor<S>

    abstract fun toPlatformExecutorConsole(executor: C): PlatformExecutor<S>

    abstract fun getAllServers(): List<PlatformServer<S>>

    abstract fun getConnectedPlayersAmount(): Int

    companion object {

        var pluginPrefix: Component = Component.empty()

    }

}
