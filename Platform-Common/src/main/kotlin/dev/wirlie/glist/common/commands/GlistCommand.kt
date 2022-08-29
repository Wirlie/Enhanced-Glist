package dev.wirlie.glist.common.commands

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import net.kyori.adventure.text.Component

class GlistCommand<S>(
    val platform: Platform<S, *, *>,
    name: String,
    aliases: MutableList<String>,
    permission: String
): PlatformCommand<S>(
    name,
    aliases,
    permission
) {

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        executor.asAudience().sendMessage(Component.text("EXECUTED!"))
    }

}
