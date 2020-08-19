package dev.wirlie.bungeecord.glist.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextUtil {
   public static BaseComponent[] fromLegacy(String text) {
      return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
   }

   private static String makeRow(int columnSize, int startIndex, ChatColor columnColor, String... data) {
      StringBuilder stringBuilder = new StringBuilder();
      int j = startIndex;

      for (String aData : data) {
         String str = aData;
         if (str != null) {
            String sanitized = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str));

            if (sanitized.length() > columnSize - 2) {
               char[] chars = str.toCharArray();

               int absoluteIndex = 0;
               int strippedIndex = 0;
               boolean colorToken = false;

               for(char ch : chars) {
                  absoluteIndex++;

                  if(ch == '&' || ch == '§') {
                     if(colorToken) {
                        //subsequents & will not be parsed
                        strippedIndex++;
                     }

                     colorToken = true;
                  } else if(colorToken) {
                     if(ch != '1' && ch != '2' && ch != '3' && ch != '4' && ch != '5' && ch != '6' && ch != '7' && ch != '8' && ch != '9'
                             && ch != 'a' && ch != 'b' && ch != 'c' && ch != 'd' && ch != 'e' && ch != 'f'
                             && ch != 'k' && ch != 'l' && ch != 'm' && ch != 'n' && ch != 'o' && ch != 'r') {
                        strippedIndex++;
                     }

                     colorToken = false;
                  } else {
                     strippedIndex++;
                  }

                  if(strippedIndex >= columnSize - 2) {
                     break;
                  }
               }

               str = str.substring(0, absoluteIndex);
            }

            int fillSpaces = columnSize - str.length();
            stringBuilder.append(ChatColor.DARK_AQUA.toString()).append("#").append(j).append(" ").append(columnColor.toString()).append(str);

            for (int i = 0; i < fillSpaces; ++i) {
               stringBuilder.append(" ");
            }

            ++j;
         }
      }

      return stringBuilder.toString();
   }

   public static String makeRows(int columns, int columnSize, int startIndex, ChatColor columnColor, String... data) {
      int column = 1;
      int j = startIndex;
      StringBuilder stringBuilder = new StringBuilder();
      String[] helperJoin = new String[columns];

      for (String str : data) {
         helperJoin[column - 1] = str;
         if (column == columns) {
            column = 1;
            stringBuilder.append(makeRow(columnSize, j, columnColor, helperJoin)).append("\n");
            helperJoin = new String[columns];
            j += columns;
         } else {
            ++column;
         }
      }

      if (column != 1) {
         stringBuilder.append(makeRow(columnSize, j, columnColor, helperJoin)).append("\n");
      }

      return stringBuilder.toString();
   }
}
