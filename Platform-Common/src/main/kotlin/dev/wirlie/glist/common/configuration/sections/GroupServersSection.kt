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
