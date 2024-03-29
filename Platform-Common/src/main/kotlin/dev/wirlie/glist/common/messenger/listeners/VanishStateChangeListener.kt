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

package dev.wirlie.glist.common.messenger.listeners

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.messenger.messages.VanishStateUpdateMessage
import dev.wirlie.glist.messenger.api.MessageListener
import java.util.*

/**
 * Messenger listener for AFK state change.
 * @param platform Platform instance.
 */
class VanishStateChangeListener<S>(
    val platform: Platform<S, *, *>
): MessageListener<VanishStateUpdateMessage>(
    VanishStateUpdateMessage::class.java
) {

    override fun onAsyncMessage(message: VanishStateUpdateMessage, fromPlayerUUID: UUID?, fromServerId: String?) {
        val state = message.state!!
        val playerUUID = message.playerUUID!!

        if(
            state && platform.playerManager.hasVanishState(playerUUID) ||
            !state && !platform.playerManager.hasVanishState(playerUUID)
        ) {
            return
        }

        val player = platform.getPlayerByUUID(playerUUID) ?: return

        platform.callVanishStateChangeEvent(player, state).whenComplete { stateRes, ex ->
            if(ex != null) {
                ex.printStackTrace()
                return@whenComplete
            }
            platform.playerManager.setVanishState(player, stateRes)
        }
    }

}
