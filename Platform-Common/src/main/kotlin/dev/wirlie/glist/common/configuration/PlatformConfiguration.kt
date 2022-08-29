package dev.wirlie.glist.common.configuration

import dev.wirlie.glist.common.Platform
import net.kyori.adventure.text.Component
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Files

class PlatformConfiguration(
    val platform: Platform<*, *, *>
) {

    private lateinit var configurationFile: File
    private lateinit var yaml: YamlConfigurationLoader
    private lateinit var yamlConfiguration: ConfigurationNode

    fun setup() {
        platform.logger.info(Component.text("Loading configuration..."))
        configurationFile = File(platform.pluginFolder, "config.yml")

        if(!configurationFile.exists()) {
            saveDefault()
            load()
        } else {
            load()
            applyUpdates()
        }
    }

    fun saveDefault() {
        platform.logger.info(Component.text("Configuration not found, saving default configuration..."))
        if(!configurationFile.parentFile.exists()) {
            Files.createDirectories(configurationFile.parentFile.toPath())
        }
        Files.copy(this::class.java.getResourceAsStream("/config.yml")!!, configurationFile.toPath())
    }

    fun applyUpdates() {

    }

    fun load() {
        yaml = YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .path(configurationFile.toPath())
            .build()

        yamlConfiguration = yaml.load()
        platform.logger.info(Component.text("Configuration loaded."))
    }

    fun save() {
        yamlConfiguration
        yaml.save(yamlConfiguration)
    }

    fun <T> getSection(clazz: Class<T>): T? {
        val rootAnnotation = clazz.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        if(!yamlConfiguration.hasChild(rootPath)) {
            throw IllegalStateException("Cannot resolve root node '${rootPath}', configuration modified or corrupted?")
        }

        return if(ConfigHandler::class.java.isAssignableFrom(clazz)) {
            val instance = clazz.getDeclaredConstructor().newInstance()
            (instance as ConfigHandler).handle(yamlConfiguration.node(rootPath))
            instance
        } else {
            yamlConfiguration.node(rootPath).get(clazz)
        }
    }

    fun setSection(section: Any) {
        val rootAnnotation = section::class.java.annotations.firstOrNull { it is ConfigRootPath } as ConfigRootPath? ?:
            throw IllegalArgumentException("Class is not annotated with @ConfigRootPath")

        val rootPath = rootAnnotation.path

        yamlConfiguration.node(rootPath).set(section)
    }

}