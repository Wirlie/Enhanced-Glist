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

package dev.wirlie.glist.common

import dev.wirlie.glist.common.commands.EglCommand
import dev.wirlie.glist.common.commands.GlistCommand
import dev.wirlie.glist.common.commands.SlistCommand
import dev.wirlie.glist.common.configuration.sections.CommandsSection

abstract class PlatformCommandManager<S>(
    val platform: Platform<S, *, *>
) {

    lateinit var glistCommand: GlistCommand<S>
    lateinit var slistCommand: SlistCommand<S>
    lateinit var eglCommand: EglCommand<S>

    fun setup() {
        glistCommand = platform.configuration.getSection(CommandsSection::class.java).run {
            GlistCommand(
                platform,
                this.glist.label,
                this.glist.aliases.toMutableList(),
                this.glist.permission
            )
        }

        slistCommand = platform.configuration.getSection(CommandsSection::class.java).run {
            SlistCommand(
                platform,
                this.slist.label,
                this.slist.aliases.toMutableList(),
                this.slist.permission
            )
        }

        eglCommand = platform.configuration.getSection(CommandsSection::class.java).run {
            EglCommand(
                platform,
                this.egl.label,
                this.egl.aliases.toMutableList(),
                this.egl.permission
            )
        }
    }

    fun reload() {
        unregisterCommands()
        setup()
        registerCommands()
    }

    abstract fun registerCommands()

    abstract fun unregisterCommands()

}
