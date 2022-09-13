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

import dev.wirlie.glist.common.gui.config.toolbar.DefinitionsCustomConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class ToolBarConfig {

    var previousPageItem = PreviousPageItemConfig()

    var nextPageItem = NextPageItemConfig()

    var background = BackgroundConfig()

    @ConfigSerializable
    class PreviousPageItemConfig {

        var slot = 2

        var displayName = "<yellow>« Go to the previous page"

    }

    @ConfigSerializable
    class NextPageItemConfig {

        var slot = 8

        var displayName = "<yellow>Go to the next page »"

    }

    @ConfigSerializable
    class BackgroundConfig {

        var pattern = "1 B 1 2 1 2 1 N 1"

        var definitions = DefinitionsCustomConfig()

    }

}
