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

import dev.wirlie.glist.common.configuration.sections.GroupServersSection
import org.spongepowered.configurate.BasicConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class GroupServersSectionSerializer: TypeSerializer<GroupServersSection> {

    override fun deserialize(type: Type, node: ConfigurationNode): GroupServersSection {
        val servers = mutableListOf<GroupServersSection.ServerSection>()
        node.childrenMap().forEach {
            val key = it.key
            val subNode = it.value

            if(key is String) {
                if(subNode.hasChild("patterns")) {
                    servers.add(GroupServersSection.ServerSection().also { sv ->
                        sv.serverName = key
                        sv.byPattern = subNode.node("patterns").getList(String::class.java, listOf())
                    })
                } else {
                    servers.add(GroupServersSection.ServerSection().also { sv ->
                        sv.serverName = key
                        sv.byName = subNode.getList(String::class.java, listOf())
                    })
                }
            } else {
                throw IllegalArgumentException("Unexpected key type, key=$key, type=${key::class.java}")
            }
        }

        return GroupServersSection(servers)
    }

    override fun serialize(type: Type, obj: GroupServersSection?, node: ConfigurationNode) {
        if(obj == null) {
            node.set(null)
            return
        }

        val servers = obj.servers
        for(server in servers) {
            val newNode = BasicConfigurationNode.root()
            if(server.byName.isNotEmpty() || server.byPattern.isEmpty()) {
                newNode.set(server.byName)
            } else {
                newNode.node("patterns").set(server.byPattern)
            }

            node.node(server.serverName).set(newNode)
        }
    }

}
