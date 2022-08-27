package dev.wirlie.bungeecord.glist.config

import dev.wirlie.bungeecord.glist.EnhancedBCL

class ConfigEntry<T>(val key: String) {
    private var value: T? = null

    init {
        EnhancedBCL.CONFIGURATIONS_REGISTRY.add(this)
    }

    fun setValue(value: Any) {
        @Suppress("UNCHECKED_CAST")
        this.value = value as T
    }

    fun get(): T {
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
