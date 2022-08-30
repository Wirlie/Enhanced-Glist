/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022  Josue Acevedo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: wirlie.dev@gmail.com
 */

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

    fun groupListToString(list: List<String>): String {
        return list.joinToString("<newline>")
    }

}
