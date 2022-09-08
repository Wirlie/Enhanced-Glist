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

package dev.wirlie.glist.common.hooks

import dev.wirlie.glist.common.Platform
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Manager for hooks with third-party plugins.
 * @param platform Platform instance.
 */
class HookManager(
    val platform: Platform<*, *, *>
) {

    private val enabledHooks = mutableSetOf<HookType>()

    private var luckPermsHook: LuckPermsHook? = null

    /**
     * Enable LuckPerms Hook.
     */
    fun enableLuckPermsHook() {
        if(enabledHooks.add(HookType.LUCKPERMS)) {
            platform.logger.info(Component.text("[HOOK] ", NamedTextColor.GREEN).append(Component.text("LuckPerms found.", NamedTextColor.WHITE)))
            luckPermsHook = LuckPermsHook()
        }
    }

    /**
     * Get LuckPerms Hook if available.
     */
    fun getLuckPermsHook() = luckPermsHook

    /**
     * Check if a certain hook is enabled.
     * @param type Type of Hook.
     */
    fun isHookEnabled(type: HookType): Boolean {
        return enabledHooks.contains(type)
    }

}
