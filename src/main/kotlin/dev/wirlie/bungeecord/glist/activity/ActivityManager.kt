package dev.wirlie.bungeecord.glist.activity

import dev.wirlie.bungeecord.glist.executor.GlistCommand
import java.util.*

class ActivityManager {

    private val activitiesByPlayer = Collections.synchronizedMap(HashMap<UUID, MutableSet<ActivityType>>())

    fun addActivity(player: UUID, activityType: ActivityType) {
        val activities = activitiesByPlayer.computeIfAbsent(player) { HashSet() }
        activities.add(activityType)
        synchronized(GlistCommand.serversPaginators) {
            for (paginator in GlistCommand.serversPaginators.values) {
                for (entry in paginator.fullData) {
                    if (entry.player.uniqueId == player) {
                        entry.activities.add(activityType)
                    }
                }
            }
        }
    }

    fun getActivities(player: UUID): MutableSet<ActivityType> {
        return activitiesByPlayer[player]
            ?: return HashSet()
    }

    fun removeActivity(player: UUID, activityType: ActivityType) {
        val activities = activitiesByPlayer[player] ?: return
        activities.remove(activityType)
        if (activities.size == 0) {
            activitiesByPlayer.remove(player)
        }
        synchronized(GlistCommand.serversPaginators) {
            for (paginator in GlistCommand.serversPaginators.values) {
                for (entry in paginator.fullData) {
                    if (entry.player.uniqueId == player) {
                        entry.activities.remove(activityType)
                    }
                }
            }
        }
    }

    fun removePlayer(player: UUID) {
        activitiesByPlayer.remove(player)
        synchronized(GlistCommand.serversPaginators) {
            for (paginator in GlistCommand.serversPaginators.values) {
                for (entry in paginator.fullData) {
                    if (entry.player.uniqueId == player) {
                        entry.activities.clear()
                    }
                }
            }
        }
    }
}
