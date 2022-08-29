package dev.wirlie.glist.common.configuration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ConfigRootPath(
    val path: String
)
