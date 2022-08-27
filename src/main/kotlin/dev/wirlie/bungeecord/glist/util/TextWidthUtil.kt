package dev.wirlie.bungeecord.glist.util

import net.md_5.bungee.api.ChatColor

object TextWidthUtil {

    private val charWidth: MutableMap<Char, Int> = HashMap()

    init {
        charWidth['a'] = 12
        charWidth['b'] = 12
        charWidth['c'] = 12
        charWidth['d'] = 12
        charWidth['e'] = 12
        charWidth['f'] = 10
        charWidth['g'] = 12
        charWidth['h'] = 12
        charWidth['i'] = 4
        charWidth['j'] = 12
        charWidth['k'] = 10
        charWidth['l'] = 6
        charWidth['m'] = 12
        charWidth['n'] = 12
        charWidth['ñ'] = 12
        charWidth['o'] = 12
        charWidth['p'] = 12
        charWidth['q'] = 12
        charWidth['r'] = 12
        charWidth['s'] = 12
        charWidth['t'] = 8
        charWidth['u'] = 12
        charWidth['v'] = 12
        charWidth['w'] = 12
        charWidth['x'] = 12
        charWidth['y'] = 12
        charWidth['z'] = 12
        charWidth['0'] = 12
        charWidth['1'] = 12
        charWidth['2'] = 12
        charWidth['3'] = 12
        charWidth['4'] = 12
        charWidth['5'] = 12
        charWidth['6'] = 12
        charWidth['7'] = 12
        charWidth['8'] = 12
        charWidth['9'] = 12
        charWidth['?'] = 12
        charWidth['¿'] = 12
        charWidth['¡'] = 4
        charWidth['!'] = 5
        charWidth['='] = 12
        charWidth['$'] = 12
        charWidth['%'] = 12
        charWidth['#'] = 12
        charWidth[','] = 4
        charWidth[';'] = 4
        charWidth[':'] = 4
        charWidth['-'] = 12
        charWidth['_'] = 12
        charWidth['A'] = 12
        charWidth['B'] = 12
        charWidth['C'] = 12
        charWidth['D'] = 12
        charWidth['E'] = 12
        charWidth['F'] = 12
        charWidth['G'] = 12
        charWidth['H'] = 12
        charWidth['I'] = 8
        charWidth['J'] = 12
        charWidth['K'] = 12
        charWidth['L'] = 12
        charWidth['M'] = 12
        charWidth['N'] = 12
        charWidth['Ñ'] = 12
        charWidth['O'] = 12
        charWidth['P'] = 12
        charWidth['Q'] = 12
        charWidth['R'] = 12
        charWidth['S'] = 12
        charWidth['T'] = 12
        charWidth['U'] = 12
        charWidth['V'] = 12
        charWidth['W'] = 12
        charWidth['X'] = 12
        charWidth['Y'] = 12
        charWidth['Z'] = 12
        charWidth['('] = 10
        charWidth[')'] = 10
        charWidth['&'] = 12
        charWidth['/'] = 12
        charWidth['+'] = 12
        charWidth['°'] = 14
        charWidth[' '] = 8
    }

    const val lineMaxWidth = 638

    private fun removeColors(text: String): String {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text))
    }

    fun estimateWidth(text: String): Int {
        val debug = false
        var totalWidth = 0
        var colorToken = false
        var currentBold = false
        for (ch in text.toCharArray()) {
            if (debug) {
                println("Next char: $ch")
            }
            if (ch == '&' || ch == '§') {
                if (!colorToken) {
                    if (debug) {
                        println("Mark as ColorTag, Skip")
                    }
                    colorToken = true
                    continue
                }
            } else {
                if (colorToken) {
                    if (isColorTag(ch) || isFormatTag(ch)) {
                        colorToken = false
                        if (ch == 'l') {
                            if (debug) {
                                println("Flag bold")
                            }
                            currentBold = true
                        } else {
                            if (isColorTag(ch) || ch == 'r') {
                                if (debug) {
                                    println("Unflag bold")
                                }
                                currentBold = false
                            }
                        }
                        if (debug) {
                            println("Skip color tag")
                        }
                        continue
                    } else {
                        if (debug) {
                            println("That is not a color tag, adding & to the sum")
                        }
                        totalWidth += charWidth.getOrDefault('&', 12)
                        if (currentBold) {
                            if (debug) {
                                println("Is bold, adding +2")
                            }
                            totalWidth += 2
                        }
                    }
                }
            }
            val letterWidth = charWidth.getOrDefault(ch, 12)
            totalWidth += letterWidth
            if (debug) {
                println("Char width => $letterWidth | total = $totalWidth")
            }
            if (currentBold) {
                if (debug) {
                    println("Is bold, adding +2")
                }
                totalWidth += 2
            }
        }
        return totalWidth
    }

    private fun isColorTag(ch: Char): Boolean {
        return ch == 'a' || ch == 'b' || ch == 'c' || ch == 'd' || ch == 'e' || ch == 'f' || ch == '0' || ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9'
    }

    private fun isFormatTag(ch: Char): Boolean {
        return ch == 'k' || ch == 'l' || ch == 'm' || ch == 'n' || ch == 'o' || ch == 'r'
    }

    fun center(text: String): String {
        val realText = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text))
        val spaceWidth = charWidth[' ']!!
        val totalWidth = estimateWidth(realText)
        if (totalWidth + spaceWidth >= lineMaxWidth) {
            return text //no se puede centrar
        }
        val remainingSpace = lineMaxWidth - totalWidth
        val spacesToFit = remainingSpace / spaceWidth
        val initialSpacesToFit = spacesToFit / 2
        val finalString = StringBuilder()
        for (i in 0 until initialSpacesToFit) {
            finalString.append(' ')
        }
        finalString.append(text)
        return finalString.toString()
    }

}
