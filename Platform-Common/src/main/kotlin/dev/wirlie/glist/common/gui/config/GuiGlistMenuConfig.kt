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

package dev.wirlie.glist.common.gui.config

import dev.simplix.protocolize.data.ItemType
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class GuiGlistMenuConfig {

    var title = "Global List - Page <page>/<total-pages>"

    var rows = 1

    var dataFormat = DataFormatConfig()

    var toolbar = ToolBarConfig()

    @ConfigSerializable
    class DataFormatConfig {

        var generalItem = GeneralItemConfig()

        var emptySlotItem = EmptySlotConfig()

        var customItems = mutableMapOf<String, CustomItemConfig>()

        @ConfigSerializable
        class GeneralItemConfig {

            var material: ItemType = ItemType.CHEST

            var amount = -1

            var displayName = "<yellow>Server <white><server-name></white>"

            var lore = mutableListOf("<player-count> <aqua>players online</aqua>", "<yellow>Click to view player list.")

        }

        @ConfigSerializable
        class EmptySlotConfig {

            var material: ItemType = ItemType.BLACK_STAINED_GLASS_PANE

            var amount = 1

            var displayName = " "

            var lore = mutableListOf<String>()

        }

    }

}
