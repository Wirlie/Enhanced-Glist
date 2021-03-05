package dev.wirlie.bungeecord.glist.util;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class TextWidthUtil {

    private static Map<Character, Integer> charWidth = new HashMap<>();

    static {
        charWidth.put('a', 12);
        charWidth.put('b', 12);
        charWidth.put('c', 12);
        charWidth.put('d', 12);
        charWidth.put('e', 12);
        charWidth.put('f', 10);
        charWidth.put('g', 12);
        charWidth.put('h', 12);
        charWidth.put('i', 4);
        charWidth.put('j', 12);
        charWidth.put('k', 10);
        charWidth.put('l', 6);
        charWidth.put('m', 12);
        charWidth.put('n', 12);
        charWidth.put('ñ', 12);
        charWidth.put('o', 12);
        charWidth.put('p', 12);
        charWidth.put('q', 12);
        charWidth.put('r', 12);
        charWidth.put('s', 12);
        charWidth.put('t', 8);
        charWidth.put('u', 12);
        charWidth.put('v', 12);
        charWidth.put('w', 12);
        charWidth.put('x', 12);
        charWidth.put('y', 12);
        charWidth.put('z', 12);
        charWidth.put('0', 12);
        charWidth.put('1', 12);
        charWidth.put('2', 12);
        charWidth.put('3', 12);
        charWidth.put('4', 12);
        charWidth.put('5', 12);
        charWidth.put('6', 12);
        charWidth.put('7', 12);
        charWidth.put('8', 12);
        charWidth.put('9', 12);
        charWidth.put('?', 12);
        charWidth.put('¿', 12);
        charWidth.put('¡', 4);
        charWidth.put('!', 5);
        charWidth.put('=', 12);
        charWidth.put('$', 12);
        charWidth.put('%', 12);
        charWidth.put('#', 12);
        charWidth.put(',', 4);
        charWidth.put(';', 4);
        charWidth.put(':', 4);
        charWidth.put('-', 12);
        charWidth.put('_', 12);
        charWidth.put('A', 12);
        charWidth.put('B', 12);
        charWidth.put('C', 12);
        charWidth.put('D', 12);
        charWidth.put('E', 12);
        charWidth.put('F', 12);
        charWidth.put('G', 12);
        charWidth.put('H', 12);
        charWidth.put('I', 8);
        charWidth.put('J', 12);
        charWidth.put('K', 12);
        charWidth.put('L', 12);
        charWidth.put('M', 12);
        charWidth.put('N', 12);
        charWidth.put('Ñ', 12);
        charWidth.put('O', 12);
        charWidth.put('P', 12);
        charWidth.put('Q', 12);
        charWidth.put('R', 12);
        charWidth.put('S', 12);
        charWidth.put('T', 12);
        charWidth.put('U', 12);
        charWidth.put('V', 12);
        charWidth.put('W', 12);
        charWidth.put('X', 12);
        charWidth.put('Y', 12);
        charWidth.put('Z', 12);
        charWidth.put('(', 10);
        charWidth.put(')', 10);
        charWidth.put('&', 12);
        charWidth.put('/', 12);
        charWidth.put('+', 12);
        charWidth.put('°', 14);
        charWidth.put(' ', 8);
    }

    public static final Integer lineMaxWidth = 638;

    private static String removeColors(String text) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));
    }

    public static int estimateWidth(String text) {
        boolean debug = false;
        int totalWidth = 0;
        boolean colorToken = false;
        boolean currentBold = false;
        for(char ch : text.toCharArray()) {
            if(debug) {
                System.out.println("Next char: " + ch);
            }
            if(ch == '&' || ch == '§') {
                if(!colorToken) {
                    if(debug) {
                        System.out.println("Mark as ColorTag, Skip");
                    }
                    colorToken = true;
                    continue;
                }
            } else {
                if(colorToken) {
                    if(isColorTag(ch) || isFormatTag(ch)) {
                        colorToken = false;

                        if(ch == 'l') {
                            if(debug) {
                                System.out.println("Flag bold");
                            }
                            currentBold = true;
                        } else {
                            if(isColorTag(ch) || ch == 'r') {
                                if(debug) {
                                    System.out.println("Unflag bold");
                                }
                                currentBold = false;
                            }
                        }

                        if(debug) {
                            System.out.println("Skip color tag");
                        }
                        continue;
                    } else {
                        if(debug) {
                            System.out.println("That is not a color tag, adding & to the sum");
                        }

                        totalWidth += charWidth.getOrDefault('&', 12);
                        if(currentBold) {
                            if(debug) {
                                System.out.println("Is bold, adding +2");
                            }
                            totalWidth += 2;
                        }
                    }
                }
            }

            int letterWidth = charWidth.getOrDefault(ch, 12);

            totalWidth += letterWidth;
            if(debug) {
                System.out.println("Char width => " + letterWidth + " | total = " + totalWidth);
            }

            if(currentBold) {
                if(debug) {
                    System.out.println("Is bold, adding +2");
                }
                totalWidth += 2;
            }
        }
        return totalWidth;
    }

    private static boolean isColorTag(Character ch) {
        return ch == 'a' || ch == 'b' || ch == 'c' || ch == 'd' || ch == 'e' || ch == 'f' || ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9';
    }

    private static boolean isFormatTag(Character ch) {
        return ch == 'k' || ch == 'l' || ch == 'm' || ch == 'n' || ch == 'o' || ch == 'r';
    }

    public static String center(String text) {
        String realText = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text));
        int spaceWidth = charWidth.get(' ');
        int totalWidth = estimateWidth(realText);

        if((totalWidth + spaceWidth) >= lineMaxWidth) {
            return text; //no se puede centrar
        }

        int remainingSpace = lineMaxWidth - totalWidth;
        int spacesToFit = remainingSpace / spaceWidth;
        int initialSpacesToFit = spacesToFit / 2;

        StringBuilder finalString = new StringBuilder();
        for(int i = 0; i < initialSpacesToFit; i++) {
            finalString.append(' ');
        }
        finalString.append(text);
        return finalString.toString();
    }

}
