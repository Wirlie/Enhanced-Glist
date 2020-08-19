package dev.wirlie.bungeecord.glist.executor;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;

public class EBLCommand extends Command {
   private final EnhancedBCL plugin;

   public EBLCommand(EnhancedBCL plugin) {
      super("enhancedbungeelist", "ebl.command.reload","enhancedbl", "ebl", "ebcl");
      this.plugin = plugin;
   }

   public void execute(CommandSender sender, String[] args) {
      if (args.length == 0) {
         sender.sendMessage(TextUtil.fromLegacy(" \n&7&m&l----------------------------&r\n &6Enhanced Bunge List &bv" + this.plugin.getDescription().getVersion() + "\n \n &fUsage:\n &e/ebl reload &7| &aReload Configuration.\n &e/glist &7| &aList all servers.\n&7&m&l----------------------------&r\n "));
      } else if (args[0].equalsIgnoreCase("reload")) {
         try {
            this.plugin.reloadConfig();
            sender.sendMessage(TextUtil.fromLegacy(" \n&7&m&l----------------------------&r\n &6Enhanced Bunge List &bv" + this.plugin.getDescription().getVersion() + "\n \n &aConfiguration reloaded!\n&7&m&l----------------------------&r\n "));
         } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(TextUtil.fromLegacy("&cAn internal error has occurred, check the console"));
         }
      } else {
         sender.sendMessage(TextUtil.fromLegacy(" \n&7&m&l----------------------------&r\n &6Enhanced Bunge List &bv" + this.plugin.getDescription().getVersion() + "\n \n &cUnknown argument &f" + args[0] + " &c. Use &b/ebl &cfor help.\n&7&m&l----------------------------&r\n "));
      }

   }
}
