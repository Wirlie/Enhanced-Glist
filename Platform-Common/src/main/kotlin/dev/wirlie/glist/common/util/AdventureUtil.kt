/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

object AdventureUtil {

    private val miniMessage: MiniMessage = MiniMessage.miniMessage()
    private val legacySectionSerializer = LegacyComponentSerializer.legacySection()
    private val legacyAmpersandSerializer = LegacyComponentSerializer.legacyAmpersand()
    private val gsonSerializer = GsonComponentSerializer.colorDownsamplingGson()

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

    fun parseMiniMessageAndGsonSerialize(text: String, removeItalicDecoration: Boolean, vararg tagResolver: TagResolver): String {
        return gsonSerializer.serialize(
            parseMiniMessage(text, *tagResolver).decoration(TextDecoration.ITALIC, !removeItalicDecoration)
        )
    }

    fun legacySectionSerialize(component: Component): String {
        return legacySectionSerializer.serialize(component)
    }

    fun legacySectionDeserialize(legacy: String): Component {
        return legacySectionSerializer.deserialize(legacy)
    }

    fun legacyAmpersandSerialize(component: Component): String {
        return legacyAmpersandSerializer.serialize(component)
    }

    fun legacyAmpersandDeserialize(legacy: String): Component {
        return legacyAmpersandSerializer.deserialize(legacy)
    }

    fun groupListToString(list: List<String>): String {
        return list.joinToString("<newline>")
    }

    private fun removeChatColor(text: String): String {
        return text.replace("§a", "")
            .replace("§b", "")
            .replace("§c", "")
            .replace("§d", "")
            .replace("§e", "")
            .replace("§f", "")
            .replace("§0", "")
            .replace("§1", "")
            .replace("§2", "")
            .replace("§3", "")
            .replace("§4", "")
            .replace("§5", "")
            .replace("§6", "")
            .replace("§7", "")
            .replace("§8", "")
            .replace("§9", "")
            .replace("§k", "")
            .replace("§l", "")
            .replace("§m", "")
            .replace("§n", "")
            .replace("§o", "")
            .replace("§r", "")
            .replace(
                Regex("§x§[abcdef0123456789klmnor]§[abcdef0123456789klmnor]§[abcdef0123456789klmnor]§[abcdef0123456789klmnor]§[abcdef0123456789klmnor]§[abcdef0123456789klmnor]"),
            ""
            )
    }

}
