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

package dev.wirlie.glist.common.messenger.listeners

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.messenger.messages.AFKStateUpdateMessage
import dev.wirlie.glist.messenger.MessageListener
import java.util.*

/**
 * Messenger listener for AFK state change.
 * @param platform Platform instance.
 */
class AfkStateChangeListener<S>(
    val platform: Platform<S, *, *>
): MessageListener<AFKStateUpdateMessage>(
    AFKStateUpdateMessage::class.java
) {

    override fun onAsyncMessage(message: AFKStateUpdateMessage, fromPlayerUUID: UUID, fromServerId: String) {
        val state = message.state!!

        if(
            state && platform.playerManager.hasAFKState(fromPlayerUUID) ||
            !state && !platform.playerManager.hasAFKState(fromPlayerUUID)
        ) {
            return
        }

        val player = platform.getPlayerByUUID(fromPlayerUUID) ?: return

        platform.callAFKStateChangeEvent(player, state).whenComplete { stateRes, ex ->
            if(ex != null) {
                ex.printStackTrace()
                return@whenComplete
            }
            platform.playerManager.setAFKState(player, stateRes)
        }
    }

}
