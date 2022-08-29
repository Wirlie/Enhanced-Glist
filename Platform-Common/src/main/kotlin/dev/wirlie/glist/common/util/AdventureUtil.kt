package dev.wirlie.glist.common.util

import dev.wirlie.glist.common.Platform
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object AdventureUtil {

    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun parseMiniMessage(text: String, vararg tagResolver: TagResolver): Component {

        val resolvers = tagResolver.toMutableList()

        resolvers.add(
            TagResolver.resolver(
                "prefix",
                Tag.selfClosingInserting(Platform.pluginPrefix)
            )
        )

        return miniMessage.deserialize(text, *resolvers.toTypedArray())
    }

}
