/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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

package dev.wirlie.glist.velocity.hooks

import de.myzelyam.api.vanish.VelocityVanishAPI
import dev.wirlie.glist.common.player.PlayerManager
import dev.wirlie.glist.velocity.platform.VelocityPlatform
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Since PremiumVanish do not offer a way to detect vanish updates (via events), we need to provide a modified
 * PlayerManager to handle this situation using the API of PremiumVanish (when required).
 */
class PlayerManagerUsingPremiumVanish(
    private val velocityPlatform: VelocityPlatform
) : PlayerManager(velocityPlatform) {

    override fun getVanishState(uuid: UUID): Boolean {
        // First, use our implementation to know if player is currently vanished...
        val isVanishedInternal = super.getVanishState(uuid)

        if(isVanishedInternal == true) {
            return true
        }

        // If not, use PremiumVanish
        val player = velocityPlatform.server.getPlayer(uuid).getOrNull()
        if(player != null) {
            val onlineVanishResult = VelocityVanishAPI.isInvisible(player)
            if(onlineVanishResult) {
                return true
            }
        }

        return false
    }
}
