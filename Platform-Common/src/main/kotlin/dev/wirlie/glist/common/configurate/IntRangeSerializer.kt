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

package dev.wirlie.glist.common.configurate

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

/**
 * Serializer used by Configurate to handle [IntRange].
 */
class IntRangeSerializer: TypeSerializer<IntRange> {

    override fun deserialize(type: Type, node: ConfigurationNode): IntRange {
        val value = node.string!!
        val parts = value.split("_")

        if(parts.size != 2) {
            throw SerializationException("Cannot deserialize IntRange from string '$value'")
        }

        val start = parts[0].toIntOrNull() ?: throw SerializationException("Cannot deserialize IntRange from string '$value'")
        val end = parts[1].toIntOrNull() ?: throw SerializationException("Cannot deserialize IntRange from string '$value'")

        if(end < start) {
            throw SerializationException("Cannot deserialize IntRange from string '$value': start is greater than end")
        }

        return IntRange(start, end)
    }

    override fun serialize(type: Type, obj: IntRange?, node: ConfigurationNode) {
        if(obj == null) {
            node.set(null)
        } else {
            node.set("${padZeros(obj.first)}_${padZeros(obj.last)}")
        }
    }

    private fun padZeros(value: Int): String {
        return if(value < 10) {
            "0$value"
        } else {
            "$value"
        }
    }

}
