package dev.wirlie.glist.common.commands

import dev.wirlie.glist.common.platform.PlatformExecutor

abstract class PlatformCommand<S>(
    val name: String,
    val aliases: MutableList<String>,
    val permission: String
) {

    abstract fun tryExecution(executor: PlatformExecutor<S>)

}
