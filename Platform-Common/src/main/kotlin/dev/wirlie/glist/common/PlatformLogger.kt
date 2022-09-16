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

package dev.wirlie.glist.common

import dev.wirlie.glist.updater.SimpleLogger
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class PlatformLogger(
    val console: Audience
): SimpleLogger {

    val prefix = Component.text("[EnhancedGlist]", NamedTextColor.GRAY)

    fun info(message: Component) {
        info(message, null)
    }

    fun info(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Info", NamedTextColor.AQUA))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.GRAY))
        }
    }

    fun warning(message: Component) {
        warning(message, null)
    }

    fun warning(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.YELLOW))
                .append(Component.text("Warning", NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.YELLOW))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.YELLOW))
        }
    }

    fun error(message: Component) {
        error(message, null)
    }

    fun error(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.DARK_RED))
                .append(Component.text("Error", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.DARK_RED))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.RED))
        }
    }

    override fun info(message: String) {
        info(Component.text(message, NamedTextColor.DARK_AQUA))
    }

    override fun warning(message: String) {
        warning(Component.text(message, NamedTextColor.YELLOW))
    }

    override fun severe(message: String) {
        error(Component.text(message, NamedTextColor.RED))
    }

}
