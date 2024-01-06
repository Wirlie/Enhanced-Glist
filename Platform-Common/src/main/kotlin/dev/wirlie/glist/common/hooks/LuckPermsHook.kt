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

package dev.wirlie.glist.common.hooks

import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.query.QueryOptions

/**
 * Hook for LuckPerms plugin.
 */
class LuckPermsHook {

    val api = LuckPermsProvider.get()

    /**
     * Resolve player prefix using LuckPerms.
     * @param platformExecutor Command executor.
     * @return Adventure Component containing the prefix.
     */
    fun getPlayerPrefix(platformExecutor: PlatformExecutor<*>): Component {
        if(platformExecutor.isConsole()) {
            throw IllegalArgumentException("Console executor is not allowed here.")
        }

        val user = api.userManager.getUser(platformExecutor.getUUID())
        var prefix: Component = Component.empty()

        if (user != null) {
            val userPrefix = user
                .cachedData
                .getMetaData(QueryOptions.defaultContextualOptions())
                .prefix

            if (userPrefix != null) {
                prefix = AdventureUtil.legacyAmpersandDeserialize(userPrefix)
            }
        }

        return prefix
    }

}
