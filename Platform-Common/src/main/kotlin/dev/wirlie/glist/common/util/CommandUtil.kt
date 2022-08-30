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

package dev.wirlie.glist.common.util

object CommandUtil {

    fun removeOptionsFromArguments(args: Array<String>): Array<String> {
        return args.toMutableList().also { it.removeIf { a -> a.startsWith("-") } }.toTypedArray()
    }

    fun extractOptionsFromArguments(args: Array<String>): List<String> {
        return args.toMutableList().filter { it.startsWith("-") }.map { it.substring(1) }
    }

}
