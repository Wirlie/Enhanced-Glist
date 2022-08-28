package dev.wirlie.glist.common.implementation.pageable

import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformPlayer
import dev.wirlie.glist.common.platform.PlatformServer
import net.kyori.adventure.audience.Audience

class ServersListDisplay(
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformServer<*, *>> = mutableListOf()
): PageDisplay<PlatformServer<*, *>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun showPage(page: Page<PlatformServer<*, *>>) {

    }

}
