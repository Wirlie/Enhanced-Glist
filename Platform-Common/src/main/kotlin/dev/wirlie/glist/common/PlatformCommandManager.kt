package dev.wirlie.glist.common

import dev.wirlie.glist.common.commands.GlistCommand
import dev.wirlie.glist.common.configuration.sections.CommandsSection

abstract class PlatformCommandManager<S>(
    val platform: Platform<S, *, *>
) {

    lateinit var glistCommand: GlistCommand<S>

    fun setup() {
        glistCommand = platform.configuration.getSection(CommandsSection::class.java).run {
            if(this == null) {
                throw IllegalStateException("Corrupted configuration? Cannot find 'commands' section.")
            }

            GlistCommand(
                platform,
                this.glist.label,
                this.glist.aliases.toMutableList(),
                this.glist.permission
            )
        }
    }

    abstract fun registerCommands()

}
