package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("general")
class GeneralSection {

    var prefix: String = "<bold><aqua>EnhancedGlist ></aqua></bold><reset>"

    var language: String = "en"

    var playersPerRow = 2

    var playersPerPage = 16

    var cache: CacheSection = CacheSection()

    var hideEmptyServers = true

    var serversPerPage = 8

    var minPlayersRequiredToDisplayServer = 1

    var displayServerNameUppercase = true

    @ConfigSerializable
    class CacheSection {

        var serverPlayers: ServerPlayersSection = ServerPlayersSection()

        @ConfigSerializable
        class ServerPlayersSection {

            var enable = true

            var time = 20

        }

    }

}
