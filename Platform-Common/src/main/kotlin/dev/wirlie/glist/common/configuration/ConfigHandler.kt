package dev.wirlie.glist.common.configuration

import org.spongepowered.configurate.ConfigurationNode

abstract class ConfigHandler {

    abstract fun handle(node: ConfigurationNode)

}
