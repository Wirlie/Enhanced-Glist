package dev.wirlie.glist.common.configuration.sections

import dev.wirlie.glist.common.configuration.ConfigRootPath
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
@ConfigRootPath("ignore-servers")
class IgnoreServersSection {

    var byName: MutableList<String> = mutableListOf()

    var byPattern: MutableList<String> = mutableListOf()

}
