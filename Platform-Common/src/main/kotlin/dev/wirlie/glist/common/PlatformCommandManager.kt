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

package dev.wirlie.glist.common

import dev.wirlie.glist.common.commands.GlistCommand
import dev.wirlie.glist.common.configuration.sections.CommandsSection

abstract class PlatformCommandManager<S>(
    val platform: Platform<S, *, *>
) {

    lateinit var glistCommand: GlistCommand<S>

    fun setup() {
        glistCommand = platform.configuration.getSection(CommandsSection::class.java).run {
            if(this == null) {
                throw IllegalStateException("Corrupted configuration? Cannot find 'commands' section.")
            }

            GlistCommand(
                platform,
                this.glist.label,
                this.glist.aliases.toMutableList(),
                this.glist.permission
            )
        }
    }

    abstract fun registerCommands()

}
