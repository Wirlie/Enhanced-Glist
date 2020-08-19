package dev.wirlie.bungeecord.glist.config;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import org.jetbrains.annotations.NotNull;

public class ConfigEntry<T> {

    private final String key;

    private T value = null;

    public ConfigEntry(String key) {
        this.key = key;
        EnhancedBCL.CONFIGURATIONS_REGISTRY.add(this);
    }

    public void setValue(@NotNull Object value) {
        this.value = (T) value;
    }

    @NotNull
    public T get() {
        return value;
    }

    public String getKey() {
        return key;
    }

}
