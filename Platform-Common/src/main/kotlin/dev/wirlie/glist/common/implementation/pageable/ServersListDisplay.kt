package dev.wirlie.glist.common.implementation.pageable

import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience

class ServersListDisplay<P, S>(
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformServer<P, S>> = mutableListOf()
): PageDisplay<PlatformServer<P, S>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun showPage(page: Page<PlatformServer<P, S>>) {

    }

}
