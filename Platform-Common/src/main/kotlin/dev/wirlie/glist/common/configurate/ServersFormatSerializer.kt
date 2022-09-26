/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022 Josue Acevedo and the Enhanced Glist contributors
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

package dev.wirlie.glist.common.configurate

import dev.wirlie.glist.common.translation.TranslationMessages
import org.spongepowered.configurate.BasicConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class ServersFormatSerializer : TypeSerializer<TranslationMessages.GlistMessages.ServersFormat> {

    private val intRangeSerializer = IntRangeSerializer()

    override fun deserialize(type: Type, node: ConfigurationNode): TranslationMessages.GlistMessages.ServersFormat {
        val format = TranslationMessages.GlistMessages.ServersFormat()

        if(node.hasChild("click-to-show-players-hover-message")) {
            format.clickToShowPlayersHoverMessage = node.node("click-to-show-players-hover-message").string!!
        }

        if(node.hasChild("template")) {
            format.template = node.node("template").string!!
        }

        if(node.hasChild("bars")) {
            val barsToSet = mutableMapOf<IntRange, ConfigurationNode>()
            node.node("bars").childrenMap().forEach {
                val intRangeNode = BasicConfigurationNode.root()
                intRangeNode.set(it.key)
                barsToSet[intRangeSerializer.deserialize(String::class.java, intRangeNode)] = it.value
            }
            format.bars = barsToSet
        }

        return format
    }

    override fun serialize(
        type: Type,
        obj: TranslationMessages.GlistMessages.ServersFormat?,
        node: ConfigurationNode
    ) {
        if(obj == null) {
            node.set(null)
        } else {
            node.node("click-to-show-players-hover-message").set(obj.clickToShowPlayersHoverMessage)
            node.node("template").set(obj.template)

            for(bar in obj.bars) {
                val keyNode = BasicConfigurationNode.root()
                intRangeSerializer.serialize(IntRange::class.java, bar.key, keyNode)

                node.node("bars").node(keyNode.string!!).set(bar.value)
            }
        }
    }

}
