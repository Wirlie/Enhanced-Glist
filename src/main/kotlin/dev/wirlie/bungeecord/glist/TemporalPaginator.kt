package dev.wirlie.bungeecord.glist

import dev.wirlie.bungeecord.glist.activity.ActivityType
import dev.wirlie.bungeecord.glist.config.Config
import dev.wirlie.bungeecord.glist.util.PlayerGlistEntry
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.stream.Collectors
import kotlin.math.ceil

class TemporalPaginator(data: Collection<PlayerGlistEntry>, pageSize: Int) {

    var fullData: List<PlayerGlistEntry>
        private set

    private val pageSize: Int = if(pageSize < 1) 1 else pageSize
    private val createdAt = System.currentTimeMillis()

    init {
        fullData = ArrayList(data)
    }

    fun getFullPage(page: Int): List<PlayerGlistEntry> {
        val totalPages = resolveNumOfPages(fullData)
        return if (page in 1..totalPages) {
            val index = page - 1
            fullData.stream().skip(index.toLong() * pageSize).limit(pageSize.toLong())
                .collect(Collectors.toList())
        } else {
            ArrayList()
        }
    }

    fun getVisiblePage(page: Int, player: ProxiedPlayer): List<PlayerGlistEntry> {
        val data = getFullDataVisible(player)
        val totalPages = resolveNumOfPages(data)
        return if (page in 1..totalPages) {
            val index = page - 1
            data.stream().skip(index.toLong() * pageSize).limit(pageSize.toLong())
                .collect(Collectors.toList())
        } else {
            ArrayList()
        }
    }

    fun getFullDataVisible(player: ProxiedPlayer): List<PlayerGlistEntry> {
        return if (!Config.BEHAVIOUR__PLAYER_STATUS__VANISH__HIDE_VANISHED_USERS.get() || player.hasPermission(Config.BEHAVIOUR__PLAYER_STATUS__VANISH__BYPASS_PERMISSION.get())) {
            fullData
        } else fullData.stream().filter { d: PlayerGlistEntry -> !d.activities.contains(ActivityType.VANISH) }.collect(
            Collectors.toList()
        )
    }

    fun resolveNumOfPages(data: List<PlayerGlistEntry>): Int {
        return ceil(data.size.toDouble() / pageSize.toDouble()).toInt()
    }

    fun shouldUpdate(millisToKeep: Long): Boolean {
        return System.currentTimeMillis() - createdAt >= millisToKeep
    }

    fun update(data: Collection<PlayerGlistEntry>) {
        fullData = ArrayList(data)
    }

    fun dataSize(): Int {
        return fullData.size
    }
}
