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

package dev.wirlie.spigot.glist

import dev.wirlie.spigot.glist.hooks.AbstractHook
import dev.wirlie.spigot.glist.hooks.EssentialsHook
import dev.wirlie.spigot.glist.hooks.SuperVanishHook
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class EnhancedGlistSpigot : JavaPlugin() {
    private val hooks: MutableList<AbstractHook> = ArrayList()

    override fun onEnable() {
        val logger = logger
        logger.info("---------------- EnhancedBungeeList - (Bridge) ----------------")
        logger.info("This plugin will send player state changes to BungeeCord like")
        logger.info("afk and vanish state.")
        logger.info("---------------------------------------------------------------")
        tryEssentialsHook()
        trySuperVanishHook()
        if (hooks.isEmpty()) {
            isEnabled = false
            logger.warning("Cannot find one of these plugins in this server: Essentials, SuperVanish or PremiumVanish")
            logger.warning("Plugin not enabled.")
            return
        }
        logger.info("Initializing communication to BungeeCord...")
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "ebcl:bridge")
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "ebcl:bridge", BridgeListener(this))

        //send all player state
        for (hook in hooks) {
            hook.registerListeners(this)
            hook.sendAllPlayersStateToBridge()
        }
        logger.info("Plugin enabled and ready to work!")
        Bukkit.getPluginManager().registerEvents(JoinListener(this), this)
    }

    fun getHooks(): List<AbstractHook> {
        return hooks
    }

    private fun tryEssentialsHook() {
        val essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials")
        if (essentialsPlugin != null) {
            logger.info("Hooked into Essentials!")
            hooks.add(EssentialsHook(this, essentialsPlugin))
        }
    }

    private fun trySuperVanishHook() {
        var superVanishPlugin = Bukkit.getPluginManager().getPlugin("SuperVanish")
        if (superVanishPlugin != null) {
            logger.info("Hooked into SuperVanish!")
            hooks.add(SuperVanishHook(this))
        } else {
            //try with PremiumVanish
            superVanishPlugin = Bukkit.getPluginManager().getPlugin("PremiumVanish")
            if (superVanishPlugin != null) {
                logger.info("Hooked into PremiumVanish!")
                hooks.add(SuperVanishHook(this))
            }
        }
    }
}
