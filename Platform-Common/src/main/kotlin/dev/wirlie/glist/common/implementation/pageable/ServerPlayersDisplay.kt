package dev.wirlie.glist.common.implementation.pageable

import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformPlayer
import net.kyori.adventure.audience.Audience

class ServerPlayersDisplay<P, S>(
    audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<PlatformPlayer<P, S>> = mutableListOf()
): PageDisplay<PlatformPlayer<P, S>>(
    audience,
    initialPageSize,
    initialData
) {

    override fun showPage(page: Page<PlatformPlayer<P, S>>) {

    }

}
