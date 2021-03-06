package dev.wirlie.bungeecord.glist.activity;

import dev.wirlie.bungeecord.glist.TemporalPaginator;
import dev.wirlie.bungeecord.glist.executor.GlistCommand;
import dev.wirlie.bungeecord.glist.util.PlayerGlistEntry;
import org.bukkit.entity.Player;

import java.util.*;

public class ActivityManager {

    private final Map<UUID, Set<ActivityType>> activitiesByPlayer = new HashMap<>();

    public void addActivity(UUID player, ActivityType activityType) {
        Set<ActivityType> activities = activitiesByPlayer.computeIfAbsent(player, k -> new HashSet<>());
        activities.add(activityType);

        synchronized (GlistCommand.serversPaginators) {
            for (TemporalPaginator paginator : GlistCommand.serversPaginators.values()) {
                for (PlayerGlistEntry entry : paginator.getFullData()) {
                    if (entry.getPlayer().getUniqueId().equals(player)) {
                        entry.getActivities().add(activityType);
                    }
                }
            }
        }
    }

    public Set<ActivityType> getActivities(UUID player) {
        Set<ActivityType> activities = activitiesByPlayer.get(player);
        if(activities == null) {
            return new HashSet<>();
        }
        return activities;
    }

    public void removeActivity(UUID player, ActivityType activityType) {
        Set<ActivityType> activities = activitiesByPlayer.get(player);
        if(activities == null) {
            return;
        }

        activities.remove(activityType);

        if(activities.size() == 0) {
            activitiesByPlayer.remove(player);
        }

        synchronized (GlistCommand.serversPaginators) {
            for (TemporalPaginator paginator : GlistCommand.serversPaginators.values()) {
                for (PlayerGlistEntry entry : paginator.getFullData()) {
                    if (entry.getPlayer().getUniqueId().equals(player)) {
                        entry.getActivities().remove(activityType);
                    }
                }
            }
        }
    }

    public void removePlayer(UUID player) {
        activitiesByPlayer.remove(player);
        synchronized (GlistCommand.serversPaginators) {
            for (TemporalPaginator paginator : GlistCommand.serversPaginators.values()) {
                for (PlayerGlistEntry entry : paginator.getFullData()) {
                    if (entry.getPlayer().getUniqueId().equals(player)) {
                        entry.getActivities().clear();
                    }
                }
            }
        }
    }

}
