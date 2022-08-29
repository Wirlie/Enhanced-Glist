package dev.wirlie.glist.common

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class PlatformLogger(
    val console: Audience
) {

    val prefix = Component.text("[EnhancedGlist]", NamedTextColor.GRAY)

    fun info(message: Component) {
        info(message, null)
    }

    fun info(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text("Info", NamedTextColor.AQUA))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.GRAY))
        }
    }

    fun warning(message: Component) {
        warning(message, null)
    }

    fun warning(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.YELLOW))
                .append(Component.text("Warning", NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.YELLOW))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.YELLOW))
        }
    }

    fun error(message: Component) {
        error(message, null)
    }

    fun error(message: Component, throwable: Throwable?) {
        console.sendMessage(
            Component.empty()
                .append(prefix)
                .append(Component.text("[", NamedTextColor.DARK_RED))
                .append(Component.text("Error", NamedTextColor.RED))
                .append(Component.text("] ", NamedTextColor.DARK_RED))
                .append(message)
        )
        if (throwable != null) {
            console.sendMessage(Component.text(throwable.stackTraceToString(), NamedTextColor.RED))
        }
    }

}
