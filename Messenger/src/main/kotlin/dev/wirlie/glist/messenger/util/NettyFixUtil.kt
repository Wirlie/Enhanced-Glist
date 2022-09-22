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

package dev.wirlie.glist.messenger.util

import io.netty.util.AttributeKey
import io.netty.util.ConstantPool
import java.util.concurrent.ConcurrentMap

object NettyFixUtil {

    // Utility to fix an issue with Lettuce and Netty...
    fun unregisterLettuceRedisURI() {
        // No longer required because Netty is shaded and relocated to prevent conflict with Netty used by server
        // I will keep this fix if required in the future...

        val clazz = AttributeKey::class.java
        val poolClazz = ConstantPool::class.java

        val poolField = clazz.getDeclaredField("pool")
        poolField.isAccessible = true
        val poolValue = poolField.get(null) as ConstantPool<*>

        val constantsField = poolClazz.getDeclaredField("constants")
        constantsField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val constantsValue = constantsField.get(poolValue) as ConcurrentMap<String, *>

        constantsValue.remove("RedisURI")
    }

}
