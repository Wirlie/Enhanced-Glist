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

package dev.wirlie.glist.common.gui.config

import dev.simplix.protocolize.data.ItemType
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class CustomItemConfig {

    var byName = mutableListOf<String>()

    var byRegex = mutableListOf<Regex>()

    var material = ItemType.NETHER_STAR

    var amount = -1

    var displayName = "<yellow>Lobby Server <white><server-name></white>"

    var lore = mutableListOf("<player-count> <aqua>players online</aqua>", "<yellow>Click to view player list.")

    var playerHead: PlayerHeadConfig? = null

}
