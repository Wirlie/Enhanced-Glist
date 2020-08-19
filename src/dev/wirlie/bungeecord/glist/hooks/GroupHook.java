package dev.wirlie.bungeecord.glist.hooks;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class GroupHook {

    private int priority;

    public GroupHook(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Nullable
    public abstract String getPrefix(ProxiedPlayer player);

    public abstract void reload();

}
