package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("commands")
class CommandsSection {

    var glist: GlistSection = GlistSection()

    var slist: SlistSection = SlistSection()

    @ConfigSerializable
    class GlistSection {

        var label: String = "glist"

        var permission: String = "egl.commands.glist"

        var aliases: Array<String> = arrayOf()
    }

    @ConfigSerializable
    class SlistSection {

        var label: String = "glist"

        var permission: String = "egl.commands.glist"

        var aliases: Array<String> = arrayOf()
    }

}
