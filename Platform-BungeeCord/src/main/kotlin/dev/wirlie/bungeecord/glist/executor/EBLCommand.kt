package dev.wirlie.bungeecord.glist.executor

import dev.wirlie.bungeecord.glist.EnhancedBCL
import dev.wirlie.bungeecord.glist.util.TextUtil.fromLegacy
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import java.io.IOException

class EBLCommand(private val plugin: EnhancedBCL) :
    Command("enhancedbungeelist", "ebl.command.reload", "enhancedbl", "ebl", "ebcl") {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(
                *fromLegacy(
                    """ 
&7&m&l----------------------------&r
 &6Enhanced Bunge List &bv${plugin.description.version}
 
 &fUsage:
 &e/ebl reload &7| &aReload Configuration.
 &e/glist &7| &aList all servers.
&7&m&l----------------------------&r
 """
                )
            )
        } else if (args[0].equals("reload", ignoreCase = true)) {
            try {
                plugin.reloadConfig()
                GlistCommand.serversPaginators.clear()
                sender.sendMessage(
                    *fromLegacy(
                        """ 
&7&m&l----------------------------&r
 &6Enhanced Bunge List &bv${plugin.description.version}
 
 &aConfiguration reloaded!
&7&m&l----------------------------&r
 """
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
                sender.sendMessage(*fromLegacy("&cAn internal error has occurred, check the console"))
            }
        } else {
            sender.sendMessage(
                *fromLegacy(
                    """ 
&7&m&l----------------------------&r
 &6Enhanced Bunge List &bv${plugin.description.version}
 
 &cUnknown argument &f${args[0]} &c. Use &b/ebl &cfor help.
&7&m&l----------------------------&r
 """
                )
            )
        }
    }
}
