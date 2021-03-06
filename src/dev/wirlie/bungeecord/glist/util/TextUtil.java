package dev.wirlie.bungeecord.glist.util;

import dev.wirlie.bungeecord.glist.activity.ActivityType;
import dev.wirlie.bungeecord.glist.config.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class TextUtil {

   public static BaseComponent[] fromLegacy(String text) {
      return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
   }

   public static String makeRowsNew(int columns, int startIndex, List<PlayerGlistEntry> dataPair) {
      int widthPerColumn = TextWidthUtil.lineMaxWidth / columns;

      StringBuilder finalLine = new StringBuilder();
      StringBuilder currentLine = new StringBuilder();
      int columnIndex = 0;
      boolean exceded = false;
      boolean first = true;

      for(int i = 0; i < dataPair.size(); i++) {
         PlayerGlistEntry nextDataPair = dataPair.get(i);
         String nextData = Config.FORMATS__SERVER_LIST__PLAYER_ROW_FORMAT.get()
                           .replace("{INDEX}", String.valueOf(startIndex + i))
                           .replace("{PREFIX}", nextDataPair.getPrefix())
                           .replace("{PLAYER_NAME}", nextDataPair.getPlayer().getName())
                           .replace("{AFK_PREFIX}", (Config.BEHAVIOUR__PLAYER_STATUS__AFK__SHOW_AFK_STATE.get() && nextDataPair.getActivities().contains(ActivityType.AFK) ? Config.BEHAVIOUR__PLAYER_STATUS__AFK__AFK_PREFIX.get() : ""))
                           .replace("{VANISH_PREFIX}", (nextDataPair.getActivities().contains(ActivityType.VANISH) ? Config.BEHAVIOUR__PLAYER_STATUS__VANISH__VANISH_PREFIX.get() : ""));

         if(nextData.trim().isEmpty()) continue;

         int nextDataWidth = TextWidthUtil.estimateWidth(nextData);
         if(nextDataWidth > widthPerColumn) {
            //salto de linea
            finalLine.append(currentLine).append(!exceded && !first ? "\n" : "").append(nextData).append("\n");
            currentLine = new StringBuilder();
            columnIndex = 0;
            exceded = true;
         } else {
            exceded = false;
            currentLine.append(nextData);

            //espacios necesarios
            int spaceWidth = TextWidthUtil.estimateWidth(" ");
            int totalSpaces = (widthPerColumn - nextDataWidth) / spaceWidth;

            for(int j = 0; j < totalSpaces; j++) {
               currentLine.append(" ");
            }

            columnIndex++;

            if(columnIndex == columns) {
               finalLine.append(currentLine).append("\n");
               currentLine = new StringBuilder();
               columnIndex = 0;
            }
         }

         first = false;
      }

      if(columnIndex != 0) {
         finalLine.append(currentLine).append("\n");
      }

      return finalLine.toString();
   }
}
