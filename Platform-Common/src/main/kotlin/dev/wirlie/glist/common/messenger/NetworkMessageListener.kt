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

package dev.wirlie.glist.common.messenger

import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServer

/**
 * Messenger Listener for incoming messages using Plugin Messages.
 * @param channel Channel to use (namespace format -> foo:foo)
 * @param subject Subject to use.
 */
abstract class NetworkMessageListener<S>(
    val channel: String,
    val subject: String
) {

    /**
     * When an object is received.
     */
    abstract fun onObjectReceive(dataObject: String, fromPlayer: PlatformExecutor<S>, fromServer: PlatformServer<S>)

}
