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

import com.google.gson.JsonParser
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.messenger.NetworkMessageListener
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

/**
 * Messenger listener for AFK state change.
 * @param platform Platform instance.
 */
class AfkStateChangeListener<S>(
    val platform: Platform<S, *, *>
): NetworkMessageListener<S>(
    "enhanced-glist:general",
    "afk-state-update"
) {

    override fun onObjectReceive(dataObject: String, fromPlayer: PlatformExecutor<S>, fromServer: PlatformServer<S>) {
        platform.playerManager.setAFKState(fromPlayer, JsonParser.parseString(dataObject).asBoolean)
    }

}
