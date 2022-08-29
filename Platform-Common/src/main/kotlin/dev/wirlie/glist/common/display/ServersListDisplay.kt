package dev.wirlie.glist.common.display

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience

class ServersListDisplay<S>(
    val platform: Platform<S, *, *>,
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformServer<S>> = mutableListOf()
): PageDisplay<PlatformServer<S>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun showPage(page: Page<PlatformServer<S>>) {

    }

}
