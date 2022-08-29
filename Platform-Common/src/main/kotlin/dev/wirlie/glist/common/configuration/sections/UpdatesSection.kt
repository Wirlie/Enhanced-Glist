package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("updates")
class UpdatesSection {

    var checkForUpdates = true

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

        }

    }

}
