package dev.wirlie.glist.common.implementation.pageable

import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformPlayer
import net.kyori.adventure.audience.Audience

class ServerPlayersDisplay(
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformPlayer<*, *>> = mutableListOf()
): PageDisplay<PlatformPlayer<*, *>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun showPage(page: Page<PlatformPlayer<*, *>>) {

    }

}
