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

package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigHandler
import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.ConfigurationNode

@ConfigRootPath("group-servers")
class GroupServersSection: ConfigHandler() {

    var servers = mutableListOf<ServerSection>()

    override fun handle(node: ConfigurationNode) {
        node.childrenMap().forEach {
            val key = it.key
            val subNode = it.value

            if(key is String) {
                val serverName = key as String
                if(subNode.hasChild("patterns")) {
                    servers.add(ServerSection().also { sv ->
                        sv.serverName = serverName
                        sv.byPattern = subNode.node("patterns").getList(String::class.java, listOf())
                    })
                } else {
                    servers.add(ServerSection().also { sv ->
                        sv.serverName = serverName
                        sv.byName = subNode.getList(String::class.java, listOf())
                    })
                }
            } else {
                throw IllegalArgumentException("Unexpected key type, key=$key, type=${key::class.java}")
            }
        }
    }

    class ServerSection {

        var serverName: String = ""

        var byName: MutableList<String> = mutableListOf()

        var byPattern: MutableList<String> = mutableListOf()

    }

}
