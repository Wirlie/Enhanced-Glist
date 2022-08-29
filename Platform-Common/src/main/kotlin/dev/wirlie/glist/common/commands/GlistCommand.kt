package dev.wirlie.glist.common.commands

import com.github.benmanes.caffeine.cache.Caffeine
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.GeneralSection
import dev.wirlie.glist.common.display.ServersListDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.util.AdventureUtil
import java.util.concurrent.TimeUnit

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

    private val cache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<String, ServersListDisplay<S>>()

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {
        val display = getDisplayFor(executor)
        val audience = executor.asAudience()

        if(display.data.isEmpty()) {
            audience.sendMessage(
                AdventureUtil.parseMiniMessage(
                    platform.translatorManager.getTranslator().getMessages().glist.noServersToDisplay
                )
            )
            return
        }

        var page = if(args.isEmpty()) {
            0
        } else {
            (args[0].toIntOrNull() ?: 1) - 1
        }

        if(page >= display.totalPages) {
            page = display.totalPages - 1
        }

        if(page < 0) {
            page = 0
        }

        display.showPage(page)
    }

    private fun getDisplayFor(executor: PlatformExecutor<S>): ServersListDisplay<S> {
        val key = if (executor.isConsole()) "console" else "player-${executor.getUUID()}"
        val current = cache.getIfPresent(key)

        if(current != null) {
            return current
        }

        val newDisplay = ServersListDisplay(
            platform,
            executor.asAudience(),
            platform.configuration.getSection(GeneralSection::class.java)?.serversPerPage ?: 8,
            platform.getAllServers().toMutableList()
        )

        cache.put(key, newDisplay)

        return newDisplay
    }

}
