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

package dev.wirlie.glist.common.commands

import dev.wirlie.glist.common.platform.PlatformExecutor

/**
 * Abstract implementation for commands.
 * @param name Label of command.
 * @param aliases Aliases of command.
 * @param permission Permission required to execute command.
 */
abstract class PlatformCommand<S>(
    val name: String,
    val aliases: MutableList<String>,
    val permission: String
) {

    /**
     * Try to handle execution with provided arguments.
     * @param executor Command executor.
     * @param args Arguments used to execute command.
     */
    abstract fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>)

}
