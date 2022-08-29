package dev.wirlie.glist.common.extensions

import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

fun String.miniMessage(vararg tagResolver: TagResolver): Component {
    return AdventureUtil.parseMiniMessage(this, *tagResolver)
}
