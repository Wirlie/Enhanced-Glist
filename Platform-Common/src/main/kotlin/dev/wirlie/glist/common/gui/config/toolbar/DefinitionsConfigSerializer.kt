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

package dev.wirlie.glist.common.gui.config.toolbar

import dev.simplix.protocolize.data.ItemType
import dev.wirlie.glist.common.gui.config.ConfigReference
import dev.wirlie.glist.common.gui.config.PlayerHeadConfig
import io.leangen.geantyref.TypeToken
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import kotlin.math.max
import kotlin.math.min

class DefinitionsConfigSerializer: TypeSerializer<DefinitionsCustomConfig> {

    override fun deserialize(type: Type, node: ConfigurationNode): DefinitionsCustomConfig {

        val data = node.get(object : TypeToken<Map<String, ConfigurationNode>>(){})!!
        val definitions = mutableListOf<AbstractDefinitionConfig>()

        data.forEach {
            if(it.key.length != 2) {
                throw SerializationException("Not expected key definition \"${it.key}\": key should start with 'i' or 'm' and have a total length of two characters.")
            }

            if(it.key.startsWith("i")) {
                definitions.add(loadItemDefinition(it.key, it.value))
            } else if(it.key.startsWith("m")) {
                definitions.add(loadMenuDefinition(it.key, it.value))
            } else {
                throw SerializationException("Not expected key definition \"${it.key}\": key should start with 'i' or 'm'")
            }
        }

        return DefinitionsCustomConfig().also {
            it.definitions = definitions
        }
    }

    private fun loadItemDefinition(key: String, node: ConfigurationNode): ItemDefinitionConfig {
        if(!node.hasChild("material")) {
            throw SerializationException("Cannot parse definition \"$key\": Missing \"material\" property.")
        }

        val material = try {
            node.node("material").run {
                ConfigReference(this, ItemType.valueOf(node.node("material").string!!))
            }
        } catch (ex: IllegalArgumentException) {
            throw SerializationException("Cannot parse definition \"$key\": \"${node.node("material").string!!}\" is not a valid material.")
        }

        val displayName = node.node("display-name").run { ConfigReference(this, string) }
        val lore = node.node("lore").run { ConfigReference(this, if(this.virtual()) null else getList(String::class.java)) }
        val amount = node.node("amount").run { ConfigReference(this, if(this.virtual()) 1 else min(max(1, int), 64)) }
        val onClick = if(node.hasChild("on-click")) {
            val onClickNode = node.node("on-click")
            val sendChat = onClickNode.node("send-chat").run { ConfigReference(this, string) }
            val runCommand = onClickNode.node("run-command").run { ConfigReference(this, string) }
            val closeMenu = onClickNode.node("close-menu").run { ConfigReference(this, if(this.virtual()) null else boolean) }
            if(sendChat.data == null && runCommand.data == null && closeMenu.data == null) {
                ConfigReference<ItemDefinitionConfig.OnClickConfiguration?>(node.node("on-click"), null)
            } else {
                ConfigReference<ItemDefinitionConfig.OnClickConfiguration?>(onClickNode, ItemDefinitionConfig.OnClickConfiguration(sendChat, runCommand, closeMenu))
            }
        } else ConfigReference<ItemDefinitionConfig.OnClickConfiguration?>(node.node("on-click"), null)
        val playerHead = if(node.hasChild("player-head")) {
            ConfigReference<PlayerHeadConfig?>(node.node("player-head"), PlayerHeadConfig().also {
                it.skullOwner = node.node("player-head", "skull-owner").string
                it.skinHash = node.node("player-head", "skin-hash").string
            })
        } else ConfigReference<PlayerHeadConfig?>(node.node("player-head"), null)

        return ItemDefinitionConfig(key, node, material, amount, displayName, lore, onClick, playerHead)
    }

    private fun loadMenuDefinition(key: String, node: ConfigurationNode): MenuDefinitionConfig {
        return MenuDefinitionConfig(key, node, node.string!!)
    }

    override fun serialize(type: Type, obj: DefinitionsCustomConfig?, node: ConfigurationNode) {
        if(obj == null) {
            node.set(null)
        } else {
            obj.definitions.forEach { def ->
                val key = def.key
                val valueNode = def.node

                if(def is MenuDefinitionConfig) {
                    valueNode.set(def.value)
                } else if(def is ItemDefinitionConfig) {
                    valueNode.node("material").set(def.material.node.also { it.set(def.material.data.toString()) })

                    if(def.displayName.data != null) {
                        valueNode.node("display-name").set(def.displayName.node.also { it.set(def.displayName.data) })
                    }

                    if(def.lore.data != null) {
                        valueNode.node("lore").set(def.lore.node.also { it.set(def.lore.data) })
                    }

                    if(def.onClick.data != null) {

                        val onClickNode = def.onClick.node
                        val onClick = def.onClick.data!!

                        if(onClick.sendChat.data != null) {
                            onClickNode.node("send-chat").set(onClick.sendChat.node.also { it.set(onClick.sendChat.data) })
                        }

                        if(onClick.runCommand.data != null) {
                            onClickNode.node("run-command").set(onClick.runCommand.node.also { it.set(onClick.runCommand.data) })
                        }

                        if(onClick.closeMenu.data != null) {
                            onClickNode.node("close-menu").set(onClick.closeMenu.node.also { it.set(onClick.closeMenu.data) })
                        }

                        valueNode.node("on-click").set(onClickNode)
                    }
                }

                node.node(key).set(valueNode)
            }
        }
    }


}
