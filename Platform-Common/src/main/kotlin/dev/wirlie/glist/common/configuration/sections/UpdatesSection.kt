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

package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

/**
 * Configuration for `updates{}` section.
 */
@ConfigSerializable
@ConfigRootPath("updates")
class UpdatesSection: ConfigurationSection {

    var checkForUpdates = true

    var checkInterval = 300

    var notify = NotifySection()

    @ConfigSerializable
    class NotifySection {

        var onJoin = OnJoinSection()

        var console = ConsoleSection()

        @ConfigSerializable
        class OnJoinSection {

            var enable = true

            var delay = 2500

            var permission = "ebcl.update.notify"

        }

        @ConfigSerializable
        class ConsoleSection {

            var enable = true

            var notificationInterval = 600

        }

    }

}
