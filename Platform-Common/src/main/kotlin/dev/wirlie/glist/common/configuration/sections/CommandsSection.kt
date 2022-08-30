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

package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("commands")
class CommandsSection {

    var glist: GlistSection = GlistSection()

    var slist: SlistSection = SlistSection()

    @ConfigSerializable
    class GlistSection {

        var label: String = "glist"

        var permission: String = "egl.commands.glist"

        var aliases: Array<String> = arrayOf()
    }

    @ConfigSerializable
    class SlistSection {

        var label: String = "slist"

        var permission: String = "egl.commands.slist"

        var aliases: Array<String> = arrayOf()
    }

}
