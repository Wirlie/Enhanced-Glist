/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022 Josue Acevedo and the Enhanced Glist contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: wirlie.dev@gmail.com
 */

package dev.wirlie.glist.spigot

import dev.wirlie.glist.spigot.util.AdventureUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class GlistExecutor(
    val plugin: EnhancedGlistSpigot
): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val messages = plugin.configurationManager.getConfiguration().messages
        val audience = AdventureUtil.sender(sender)

        if(!sender.hasPermission("egls.command.reload")) {
            audience.sendMessage(AdventureUtil.parseMiniMessage(messages.noPermissionToUseCommand))
            return true
        }

        if(args.isEmpty() || !args[0].equals("reload", true)) {
            audience.sendMessage(AdventureUtil.parseMiniMessage(messages.usage))
            return true
        }

        plugin.performReload()
        audience.sendMessage(AdventureUtil.parseMiniMessage(messages.configurationReloaded))

        return true
    }

}
