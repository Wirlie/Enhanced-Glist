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

package dev.wirlie.glist.spigot.configuration

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
class PluginConfiguration {

    var hooks = HooksConfiguration()

    var messages = MessagesConfiguration()

    @ConfigSerializable
    class HooksConfiguration {

        var jetsAntiAfkPro = JetsAntiAfkProConfiguration()

        var essentials = EssentialsConfiguration()

        var superVanish = SuperVanishConfiguration()

        @ConfigSerializable
        class JetsAntiAfkProConfiguration {

            var enable = true

            var settings = SettingsConfiguration()

            @ConfigSerializable
            class SettingsConfiguration {

                var checkPeriod = 20

                var timeToTreatPlayerAsAfk = 300

            }

        }

        @ConfigSerializable
        class EssentialsConfiguration {

            var enable = true

            var settings = SettingsConfiguration()

            @ConfigSerializable
            class SettingsConfiguration {

                var handleVanishUpdates = true

                var handleAfkUpdates = true

            }

        }

        @ConfigSerializable
        class SuperVanishConfiguration {

            var enable = true

        }

    }

    @ConfigSerializable
    class MessagesConfiguration {

        var noPermissionToUseCommand = ""

        var configurationReloaded = ""

        var usage = ""

    }

}
