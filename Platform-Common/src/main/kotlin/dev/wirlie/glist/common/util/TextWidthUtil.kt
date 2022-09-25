package dev.wirlie.glist.common.util

object TextWidthUtil {

    private val charWidth112: MutableMap<Char, Int> = HashMap()
    private val charWidth113: MutableMap<Char, Int> = HashMap()

    private const val VERSION_112 = 1
    const val VERSION_113 = 2

    init {
        charWidth112['a'] = 12
        charWidth112['b'] = 12
        charWidth112['c'] = 12
        charWidth112['d'] = 12
        charWidth112['e'] = 12
        charWidth112['f'] = 10
        charWidth112['g'] = 12
        charWidth112['h'] = 12
        charWidth112['i'] = 4
        charWidth112['j'] = 12
        charWidth112['k'] = 10
        charWidth112['l'] = 6
        charWidth112['m'] = 12
        charWidth112['n'] = 12
        charWidth112['ñ'] = 12
        charWidth112['o'] = 12
        charWidth112['p'] = 12
        charWidth112['q'] = 12
        charWidth112['r'] = 12
        charWidth112['s'] = 12
        charWidth112['t'] = 8
        charWidth112['u'] = 12
        charWidth112['v'] = 12
        charWidth112['w'] = 12
        charWidth112['x'] = 12
        charWidth112['y'] = 12
        charWidth112['z'] = 12
        charWidth112['0'] = 12
        charWidth112['1'] = 12
        charWidth112['2'] = 12
        charWidth112['3'] = 12
        charWidth112['4'] = 12
        charWidth112['5'] = 12
        charWidth112['6'] = 12
        charWidth112['7'] = 12
        charWidth112['8'] = 12
        charWidth112['9'] = 12
        charWidth112['?'] = 12
        charWidth112['¿'] = 12
        charWidth112['¡'] = 4
        charWidth112['!'] = 5
        charWidth112['='] = 12
        charWidth112['$'] = 12
        charWidth112['%'] = 12
        charWidth112['#'] = 12
        charWidth112[','] = 4
        charWidth112[';'] = 4
        charWidth112[':'] = 4
        charWidth112['-'] = 12
        charWidth112['_'] = 12
        charWidth112['A'] = 12
        charWidth112['B'] = 12
        charWidth112['C'] = 12
        charWidth112['D'] = 12
        charWidth112['E'] = 12
        charWidth112['F'] = 12
        charWidth112['G'] = 12
        charWidth112['H'] = 12
        charWidth112['I'] = 8
        charWidth112['J'] = 12
        charWidth112['K'] = 12
        charWidth112['L'] = 12
        charWidth112['M'] = 12
        charWidth112['N'] = 12
        charWidth112['Ñ'] = 12
        charWidth112['O'] = 12
        charWidth112['P'] = 12
        charWidth112['Q'] = 12
        charWidth112['R'] = 12
        charWidth112['S'] = 12
        charWidth112['T'] = 12
        charWidth112['U'] = 12
        charWidth112['V'] = 12
        charWidth112['W'] = 12
        charWidth112['X'] = 12
        charWidth112['Y'] = 12
        charWidth112['Z'] = 12
        charWidth112['('] = 10
        charWidth112[')'] = 10
        charWidth112['&'] = 12
        charWidth112['/'] = 12
        charWidth112['+'] = 12
        charWidth112['°'] = 14
        charWidth112[' '] = 8


        charWidth113['a'] = 10
        charWidth113['b'] = 10
        charWidth113['c'] = 10
        charWidth113['d'] = 10
        charWidth113['e'] = 10
        charWidth113['f'] = 8
        charWidth113['g'] = 10
        charWidth113['h'] = 10
        charWidth113['i'] = 2
        charWidth113['j'] = 10
        charWidth113['k'] = 8
        charWidth113['l'] = 4
        charWidth113['m'] = 10
        charWidth113['n'] = 10
        charWidth113['ñ'] = 10
        charWidth113['o'] = 10
        charWidth113['p'] = 10
        charWidth113['q'] = 10
        charWidth113['r'] = 10
        charWidth113['s'] = 10
        charWidth113['t'] = 6
        charWidth113['u'] = 10
        charWidth113['v'] = 10
        charWidth113['w'] = 10
        charWidth113['x'] = 10
        charWidth113['y'] = 10
        charWidth113['z'] = 10
        charWidth113['0'] = 10
        charWidth113['1'] = 10
        charWidth113['2'] = 10
        charWidth113['3'] = 10
        charWidth113['4'] = 10
        charWidth113['5'] = 10
        charWidth113['6'] = 10
        charWidth113['7'] = 10
        charWidth113['8'] = 10
        charWidth113['9'] = 10
        charWidth113['?'] = 10
        charWidth113['¿'] = 10
        charWidth113['¡'] = 2
        charWidth113['!'] = 2
        charWidth113['='] = 10
        charWidth113['$'] = 10
        charWidth113['%'] = 10
        charWidth113['#'] = 10
        charWidth113[','] = 2
        charWidth113[';'] = 2
        charWidth113[':'] = 2
        charWidth113['-'] = 10
        charWidth113['_'] = 10
        charWidth113['A'] = 10
        charWidth113['B'] = 10
        charWidth113['C'] = 10
        charWidth113['D'] = 10
        charWidth113['E'] = 10
        charWidth113['F'] = 10
        charWidth113['G'] = 10
        charWidth113['H'] = 10
        charWidth113['I'] = 6
        charWidth113['J'] = 10
        charWidth113['K'] = 10
        charWidth113['L'] = 10
        charWidth113['M'] = 10
        charWidth113['N'] = 10
        charWidth113['Ñ'] = 10
        charWidth113['O'] = 10
        charWidth113['P'] = 10
        charWidth113['Q'] = 10
        charWidth113['R'] = 10
        charWidth113['S'] = 10
        charWidth113['T'] = 10
        charWidth113['U'] = 10
        charWidth113['V'] = 10
        charWidth113['W'] = 10
        charWidth113['X'] = 10
        charWidth113['Y'] = 10
        charWidth113['Z'] = 10
        charWidth113['('] = 6
        charWidth113[')'] = 6
        charWidth113['&'] = 10
        charWidth113['/'] = 10
        charWidth113['+'] = 10
        charWidth113['°'] = 8
        charWidth113[' '] = 6
        charWidth113['.'] = 2
    }

    const val lineMaxWidth = 490

    fun estimateWidth(char: Char, version: Int): Int {
        val charsToUse = when (version) {
            VERSION_112 -> {
                charWidth112
            }
            VERSION_113 -> {
                charWidth113
            }
            else -> {
                throw IllegalArgumentException("Unknown version, use VERSION_112 or VERSION_113")
            }
        }

        return charsToUse.getOrDefault(char, 10)
    }

    fun estimateWidth(text: String, version: Int): Int {
        val charsToUse = when (version) {
            VERSION_112 -> {
                charWidth112
            }
            VERSION_113 -> {
                charWidth113
            }
            else -> {
                throw IllegalArgumentException("Unknown version, use VERSION_112 or VERSION_113")
            }
        }

        val debug = false
        var totalWidth = 0
        var colorToken = false
        var currentBold = false
        for (ch in text.toCharArray()) {
            if (debug) {
                println("Next char: $ch")
            }
            if (ch == '§') {
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
                        totalWidth += charsToUse.getOrDefault('&', 12)
                        if (currentBold) {
                            if (debug) {
                                println("Is bold, adding +2")
                            }
                            totalWidth += 2
                        }
                    }
                }
            }
            val letterWidth = charsToUse.getOrDefault(ch, 10)
            totalWidth += letterWidth + 1
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
        val spaceWidth = charWidth112[' ']!!
        val totalWidth = estimateWidth(text, VERSION_113)
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
