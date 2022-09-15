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

package dev.wirlie.glist.common.commands

import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.util.AdventureUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * Implementation for /glist command.
 * @param platform Platform instance.
 * @param name Label of command.
 * @param aliases Aliases of command.
 * @param permission Permission required to execute command.
 */
class EglCommand<S>(
    val platform: Platform<S, *, *>,
    name: String,
    aliases: MutableList<String>,
    permission: String
): PlatformCommand<S>(
    name,
    aliases,
    permission
) {

    override fun tryExecution(executor: PlatformExecutor<S>, args: Array<String>) {

        var sendUsageMessage = true

        if(args.isNotEmpty()) {
            when(args[0].lowercase()) {
                "reload" -> {
                    sendUsageMessage = false
                    platform.reload()
                    executor.asAudience().sendMessage(
                        AdventureUtil.parseMiniMessage(
                            platform.translatorManager.getTranslator().getMessages().egl.pluginReloaded,
                            TagResolver.resolver(
                                "label", Tag.selfClosingInserting(Component.text(name))
                            )
                        )
                    )
                }
            }
        }

        if(sendUsageMessage) {
            executor.asAudience().sendMessage(
                AdventureUtil.parseMiniMessage(
                    platform.translatorManager.getTranslator().getMessages().egl.usage.joinToString("<newline>"),
                    TagResolver.resolver(
                        "label", Tag.selfClosingInserting(Component.text(name))
                    )
                )
            )
        }

    }

}