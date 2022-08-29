package dev.wirlie.glist.common.display

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformServer
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

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

    override fun buildPageDisplay(page: Page<PlatformServer<S>>) {
        val glistMessages = platform.translatorManager.getTranslator().getGlistMessages()
        val pageControllerMessages = glistMessages.pageController
        val glistLabel = platform.configuration.getSection(CommandsSection::class.java)?.glist?.label ?: "glist"
        val slistLabel = platform.configuration.getSection(CommandsSection::class.java)?.slist?.label ?: "slist"

        val mainMessage = AdventureUtil.parseMiniMessage(
            AdventureUtil.groupListToString(
                glistMessages.mainMessage
            ),
            TagResolver.resolver(
                "page-number", Tag.selfClosingInserting(Component.text("${page.pageNumber + 1}"))
            ),
            TagResolver.resolver(
                "total-pages", Tag.selfClosingInserting(Component.text("${page.totalPages}"))
            ),
            TagResolver.resolver(
                "players-amount", Tag.selfClosingInserting(Component.text("${platform.getConnectedPlayersAmount()}"))
            ),
            TagResolver.resolver(
                "slist-label", Tag.selfClosingInserting(Component.text(slistLabel))
            ),
            TagResolver.resolver(
                "page-controller",
                Tag.selfClosingInserting(
                    pageControllerMessages.buildController(
                        page.hasPrevious,
                        page.hasNext,
                        "/$glistLabel ${(page.pageNumber + 1) - 1}",
                        "/$glistLabel ${(page.pageNumber + 1) + 1}",
                        page.pageNumber
                    )
                )
            )
        )

        audience.sendMessage(mainMessage)
    }

}
