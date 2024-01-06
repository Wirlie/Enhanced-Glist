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
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

/**
 * Serializer used by Configurate to handle [Regex].
 */
class RegexSerializer: TypeSerializer<Regex> {

    override fun deserialize(type: Type, node: ConfigurationNode): Regex {
        val value = node.string!!
        return Regex(value, RegexOption.IGNORE_CASE)
    }

    override fun serialize(type: Type, obj: Regex?, node: ConfigurationNode) {
        if(obj == null) {
            node.set(null)
        } else {
            node.set(obj.pattern)
        }
    }

}
