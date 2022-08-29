package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("behavior")
class BehaviorSection {

    var vanish: VanishSection = VanishSection()

    var afk: AfkSection = AfkSection()

    @ConfigSerializable
    class VanishSection {

        var enable = true

        var hideVanishedUsers = true

        var hideBypassPermission = true

        var prefix: String = "<dark_gray>[<aqua>V</aqua>]</dark_gray>"

    }

    @ConfigSerializable
    class AfkSection {

        var enable = true

        var prefix = "<dark_gray>[<yellow>AFK</yellow>]</dark_gray>"
    }

}
