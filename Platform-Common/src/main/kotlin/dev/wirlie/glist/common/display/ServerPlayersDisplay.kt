package dev.wirlie.glist.common.display

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import net.kyori.adventure.audience.Audience

class ServerPlayersDisplay<S>(
    val platform: Platform<S, *, *>,
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformExecutor<S>> = mutableListOf()
): PageDisplay<PlatformExecutor<S>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun buildPageDisplay(page: Page<PlatformExecutor<S>>) {

    }

}
