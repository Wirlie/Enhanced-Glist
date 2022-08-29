package dev.wirlie.glist.common.util

object CommandUtil {

    fun removeOptionsFromArguments(args: Array<String>): Array<String> {
        return args.toMutableList().also { it.removeIf { a -> a.startsWith("-") } }.toTypedArray()
    }

    fun extractOptionsFromArguments(args: Array<String>): List<String> {
        return args.toMutableList().filter { it.startsWith("-") }.map { it.substring(1) }
    }

}
