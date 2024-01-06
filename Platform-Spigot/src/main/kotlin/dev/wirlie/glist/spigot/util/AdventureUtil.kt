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

package dev.wirlie.glist.spigot.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AdventureUtil {

    lateinit var adventure: BukkitAudiences
    private val miniMessage: MiniMessage = MiniMessage.miniMessage()
    private val legacySectionSerializer = LegacyComponentSerializer.legacySection()
    private val legacyAmpersandSerializer = LegacyComponentSerializer.legacyAmpersand()

    fun player(player: Player): Audience {
        return adventure.player(player)
    }

    fun sender(sender: CommandSender): Audience {
        return adventure.sender(sender)
    }

    fun all(): Audience {
        return adventure.all()
    }

    fun console(): Audience {
        return adventure.console()
    }

    fun parseMiniMessage(text: String, vararg tagResolver: TagResolver): Component {

        val resolvers = tagResolver.toMutableList()

        return miniMessage.deserialize(text, *resolvers.toTypedArray())
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
