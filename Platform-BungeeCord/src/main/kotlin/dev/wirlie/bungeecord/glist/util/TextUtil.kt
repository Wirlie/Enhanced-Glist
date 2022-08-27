package dev.wirlie.bungeecord.glist.util

import dev.wirlie.bungeecord.glist.activity.ActivityType
import dev.wirlie.bungeecord.glist.config.Config
import dev.wirlie.bungeecord.glist.util.TextWidthUtil.estimateWidth
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import java.util.*

object TextUtil {

    fun fromLegacy(text: String?): Array<BaseComponent> {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text))
    }

    fun makeRowsNew(columns: Int, startIndex: Int, dataPair: List<PlayerGlistEntry>): String {
        val widthPerColumn = TextWidthUtil.lineMaxWidth / columns
        val finalLine = StringBuilder()
        var currentLine = StringBuilder()
        var columnIndex = 0
        var exceded = false
        var first = true

        for (i in dataPair.indices) {
            val nextDataPair = dataPair[i]

            val nextData = Config.FORMATS__SERVER_LIST__PLAYER_ROW_FORMAT.get()
                .replace("{INDEX}", (startIndex + i).toString())
                .replace("{PREFIX}", if (nextDataPair.prefix == null || nextDataPair.prefix.trim { it <= ' ' }
                        .equals("null", ignoreCase = true) || nextDataPair.prefix.trim { it <= ' ' }
                        .lowercase(Locale.getDefault()).contains("null")) "" else nextDataPair.prefix)
                .replace("{PLAYER_NAME}", nextDataPair.player.name)
                .replace(
                    "{AFK_PREFIX}",
                    if (Config.BEHAVIOUR__PLAYER_STATUS__AFK__SHOW_AFK_STATE.get() && nextDataPair.activities.contains(
                            ActivityType.AFK
                        )
                    ) Config.BEHAVIOUR__PLAYER_STATUS__AFK__AFK_PREFIX.get() else ""
                )
                .replace(
                    "{VANISH_PREFIX}",
                    if (nextDataPair.activities.contains(ActivityType.VANISH)) Config.BEHAVIOUR__PLAYER_STATUS__VANISH__VANISH_PREFIX.get() else ""
                )

            if (nextData.trim { it <= ' ' }.isEmpty()) continue
            val nextDataWidth = estimateWidth(nextData)

            if (nextDataWidth > widthPerColumn) {
                //salto de linea
                finalLine.append(currentLine).append(if (!exceded && !first) "\n" else "").append(nextData).append("\n")
                currentLine = StringBuilder()
                columnIndex = 0
                exceded = true
            } else {
                exceded = false
                currentLine.append(nextData)

                //espacios necesarios
                val spaceWidth = estimateWidth(" ")
                val totalSpaces = (widthPerColumn - nextDataWidth) / spaceWidth
                for (j in 0 until totalSpaces) {
                    currentLine.append(" ")
                }

                columnIndex++
                if (columnIndex == columns) {
                    finalLine.append(currentLine).append("\n")
                    currentLine = StringBuilder()
                    columnIndex = 0
                }
            }

            first = false
        }

        if (columnIndex != 0) {
            finalLine.append(currentLine).append("\n")
        }

        return finalLine.toString()
    }

}
