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

package dev.wirlie.glist.common.gui.config.toolbar

import dev.simplix.protocolize.data.ItemType
import dev.wirlie.glist.common.gui.config.ConfigReference
import dev.wirlie.glist.common.gui.config.PlayerHeadConfig
import org.spongepowered.configurate.ConfigurationNode

class ItemDefinitionConfig(
    key: String,
    node: ConfigurationNode,
    var material: ConfigReference<ItemType>,
    var amount: ConfigReference<Int>,
    var displayName: ConfigReference<String?>,
    var lore: ConfigReference<MutableList<String>?>,
    var onClick: ConfigReference<OnClickConfiguration?>,
    var playerHead: ConfigReference<PlayerHeadConfig?>
): AbstractDefinitionConfig(
    key, key.toCharArray()[1], node
) {

    class OnClickConfiguration(
        var sendChat: ConfigReference<String?>,
        var runCommand: ConfigReference<String?>,
        var closeMenu: ConfigReference<Boolean?>
    ) {

        override fun toString(): String {
            return "OnClickConfiguration{sendChat=${sendChat.data}, runCommand=${runCommand.data}, closeMenu=${closeMenu.data}}"
        }

    }

    override fun toString(): String {
        return "ItemDefinitionConfig{key=$key, material=${material.data}, displayName=${displayName.data}, lore=[${lore.data?.joinToString(", ")}], onClick=$onClick}"
    }

}
