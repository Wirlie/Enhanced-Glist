package dev.wirlie.bungeecord.glist.util;

import dev.wirlie.bungeecord.glist.activity.ActivityType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Set;

public class PlayerGlistEntry {

    private final ProxiedPlayer player;
    private final String prefix;
    private Set<ActivityType> activities;

    public PlayerGlistEntry(ProxiedPlayer player, String prefix, Set<ActivityType> activities) {
        this.player = player;
        this.prefix = prefix;
        this.activities = activities;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public String getPrefix() {
        return prefix;
    }

    public Set<ActivityType> getActivities() {
        return activities;
    }

    public void setActivities(Set<ActivityType> activities) {
        this.activities = activities;
    }
}
