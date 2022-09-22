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

    var updates = UpdatesConfiguration()

    var communication = CommunicationConfiguration()

    @ConfigSerializable
    class CommunicationConfiguration {

        var type = "plugin-messages"

        var rabbitmqServer = RabbitMQServerSection()

        var redisServer = RedisServer()

        @ConfigSerializable
        class RabbitMQServerSection {

            var host = "localhost"

            var port = 5672

            var user = "guest"

            var password = "guest"

        }

        @ConfigSerializable
        class RedisServer {

            var host = "localhost"

            var port = 6379

            var user = ""

            var password = ""

        }

    }

    @ConfigSerializable
    class HooksConfiguration {

        var jetsAntiAfkPro = JetsAntiAfkProConfiguration()

        var essentials = EssentialsConfiguration()

        var superVanish = SuperVanishConfiguration()

        var vanishNoPacket = VanishNoPacketConfiguration()

        var staffFacilities = StaffFacilitiesConfiguration()

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

        @ConfigSerializable
        class VanishNoPacketConfiguration {

            var enable = true

        }

        @ConfigSerializable
        class StaffFacilitiesConfiguration {

            var enable = true

        }

    }

    @ConfigSerializable
    class MessagesConfiguration {

        var noPermissionToUseCommand = ""

        var configurationReloaded = ""

        var usage = ""

    }

    @ConfigSerializable
    class UpdatesConfiguration {

        var checkForUpdates = true

        var checkInterval = 300

        var notify = NotifySection()

        @ConfigSerializable
        class NotifySection {

            var console = ConsoleSection()

            @ConfigSerializable
            class ConsoleSection {

                var enable = true

                var notificationInterval = 600

            }

        }

    }


}
