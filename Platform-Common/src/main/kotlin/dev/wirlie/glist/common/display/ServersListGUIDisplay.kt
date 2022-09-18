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

package dev.wirlie.glist.common.display

import dev.simplix.protocolize.api.Protocolize
import dev.simplix.protocolize.api.inventory.Inventory
import dev.simplix.protocolize.api.item.ItemStack
import dev.simplix.protocolize.data.ItemType
import dev.simplix.protocolize.data.inventory.InventoryType
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.configuration.sections.CommandsSection
import dev.wirlie.glist.common.gui.GUIInventory
import dev.wirlie.glist.common.gui.config.toolbar.ItemDefinitionConfig
import dev.wirlie.glist.common.gui.config.toolbar.MenuDefinitionConfig
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.pageable.PageDisplay
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.common.util.ProtocolizeUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import kotlin.math.max
import kotlin.math.min

/**
 * Display for servers list.
 * @param platform Platform instance.
 * @param executor Command executor.
 * @param audience Audience to send the result of this display.
 * @param initialPageSize Elements per page.
 * @param data Data to use for pagination.
 */
class ServersListGUIDisplay<S>(
    val platform: Platform<S, *, *>,
    val executor: PlatformExecutor<S>,
    audience: Audience,
    data: MutableList<PlatformServerGroup<S>> = mutableListOf(),
    private val menuRows: Int
): PageDisplay<PlatformServerGroup<S>>(
    audience,
    (menuRows - 1) * 9,
    ServersDataProvider(data)
) {

    val configuration = platform.guiManager!!.glistConfig
    lateinit var inventory : Inventory
    val protocolPlayer = Protocolize.playerProvider().player(executor.getUUID())

    override fun buildPageDisplay(page: Page<PlatformServerGroup<S>>) {
        inventory = GUIInventory(InventoryType.chestInventoryWithRows(menuRows)).also {
            it.title(
                platform.toPlatformComponent(
                    AdventureUtil.parseMiniMessage(
                        configuration.title,
                        *commonTagResolvers(page),
                        *playerTagResolvers(executor)
                    )
                )
            )
        }

        val generalItem = configuration.dataFormat.generalItem
        val emptyItem = configuration.dataFormat.emptySlotItem

        for(i in 0 until (menuRows - 1) * 9) {
            if (i < page.items.size) {
                val serverItem = page.items[i]

                // Check if custom item is defined for this icon
                val customIcon = configuration.dataFormat.customItems.values.firstOrNull {
                    it.byName.any { name -> name.equals(serverItem.getName(), true) } ||
                            it.byRegex.any { regex -> regex.matches(serverItem.getName()) }
                }

                val item = if(customIcon == null) {
                    val itemSet = ItemStack(generalItem.material, min(max(if(generalItem.amount == -1) serverItem.getPlayersCount() else generalItem.amount, 1), 64))

                    itemSet.displayName(
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                generalItem.displayName,
                                *commonTagResolvers(page),
                                *serverTagResolvers(serverItem),
                                *playerTagResolvers(executor)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    )
                    itemSet.lore(
                        generalItem.lore.map {
                            platform.toPlatformComponent(
                                AdventureUtil.parseMiniMessage(
                                    it,
                                    *commonTagResolvers(page),
                                    *serverTagResolvers(serverItem),
                                    *playerTagResolvers(executor)
                                ).decoration(TextDecoration.ITALIC, false)
                            )
                        },
                        false
                    )

                    if(itemSet.itemType() == ItemType.PLAYER_HEAD) {
                        ProtocolizeUtil.setHeadProperties(itemSet, generalItem.playerHead)
                    }

                    itemSet
                } else {
                    val itemSet = ItemStack(customIcon.material, min(max(if(customIcon.amount == -1) serverItem.getPlayersCount() else customIcon.amount, 1), 64))

                    itemSet.displayName(
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                customIcon.displayName,
                                *commonTagResolvers(page),
                                *serverTagResolvers(serverItem),
                                *playerTagResolvers(executor)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    )
                    itemSet.lore(
                        customIcon.lore.map {
                            platform.toPlatformComponent(
                                AdventureUtil.parseMiniMessage(
                                    it,
                                    *commonTagResolvers(page),
                                    *serverTagResolvers(serverItem),
                                    *playerTagResolvers(executor)
                                ).decoration(TextDecoration.ITALIC, false)
                            )
                        },
                        false
                    )

                    if(itemSet.itemType() == ItemType.PLAYER_HEAD) {
                        ProtocolizeUtil.setHeadProperties(itemSet, customIcon.playerHead)
                    }

                    itemSet
                }

                @Suppress("UnnecessaryVariable") val finalSlot = i
                inventory.item(i, item).onClick {
                    if (it.slot() == finalSlot) {
                        val label = platform.configuration.getSection(CommandsSection::class.java).slist.label
                        platform.performCommandForPlayer(executor, "$label ${serverItem.getName().lowercase()}")
                    }
                }
            } else {
                val item = ItemStack(emptyItem.material, emptyItem.amount)

                item.displayName(
                    platform.toPlatformComponent(
                        AdventureUtil.parseMiniMessage(
                            emptyItem.displayName,
                            *commonTagResolvers(page),
                            *playerTagResolvers(executor)
                        ).decoration(TextDecoration.ITALIC, false)
                    )
                )
                item.lore(
                    emptyItem.lore.map {
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                it,
                                *commonTagResolvers(page),
                                *playerTagResolvers(executor)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    },
                    false
                )

                if(item.itemType() == ItemType.PLAYER_HEAD) {
                    ProtocolizeUtil.setHeadProperties(item, emptyItem.playerHead)
                }

                inventory.item(i, item)
            }
        }

        fillToolbar()

        protocolPlayer.openInventory(inventory)
    }

    private fun fillToolbar() {

        val toolbarConfig = configuration.toolbar
        val pattern = toolbarConfig.background.pattern
        val trimmedPattern = pattern.replace(" ", "")

        if(trimmedPattern.length != 9) {
            // Failed pattern!
            platform.logger.warning(Component.text("Failed to make toolbar for GUI -> pattern is not configured correctly, expected pattern: X X X X X X X X X"))
            return
        }

        val definitionChars = trimmedPattern.toCharArray()
        val slot = (menuRows - 1) * 9

        for(i in 0 until 9) {
            val definition = toolbarConfig.background.definitions.definitions.firstOrNull { it.definitionKey == definitionChars[i] } ?: continue

            if(definition is ItemDefinitionConfig) {
                val item = ItemStack(definition.material.data, definition.amount.data)

                if(item.itemType() == ItemType.PLAYER_HEAD) {
                    ProtocolizeUtil.setHeadProperties(item, definition.playerHead.data)
                }

                if(definition.displayName.data != null) {
                    item.displayName(
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                definition.displayName.data!!,
                                *commonTagResolvers(null),
                                *playerTagResolvers(executor)
                            )
                        )
                    )
                }

                if(definition.lore.data != null) {
                    item.lore(
                        definition.lore.data!!.map {
                            platform.toPlatformComponent(
                                AdventureUtil.parseMiniMessage(
                                    it,
                                    *commonTagResolvers(null),
                                    *playerTagResolvers(executor)
                                )
                            )
                        },
                        false
                    )
                }

                val finalSlot = slot + i
                inventory.item(finalSlot, item).also { inv ->
                    inv.onClick {
                        if(it.slot() != finalSlot) return@onClick

                        if(definition.onClick.data != null) {
                            val onClick = definition.onClick.data!!

                            if(onClick.closeMenu.data == true) {
                                protocolPlayer.closeInventory()
                            }

                            if(onClick.runCommand.data != null) {
                                platform.performCommandForPlayer(executor, onClick.runCommand.data!!)
                            }

                            if(onClick.sendChat.data != null) {
                                executor.asAudience().sendMessage(
                                    AdventureUtil.parseMiniMessage(
                                        onClick.sendChat.data!!,
                                        *commonTagResolvers(null),
                                        *playerTagResolvers(executor)
                                    )
                                )
                            }
                        }
                    }
                }
            } else if(definition is MenuDefinitionConfig) {
                if(definition.value.equals("previous-page-item", true)) {
                    val config = toolbarConfig.previousPageItem
                    val item = ItemStack(config.material, min(max(config.amount, 1), 64))

                    if(item.itemType() == ItemType.PLAYER_HEAD) {
                        ProtocolizeUtil.setHeadProperties(item, config.playerHead)
                    }

                    item.displayName(
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                config.displayName,
                                *commonTagResolvers(null),
                                *playerTagResolvers(executor)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    )

                    item.lore(
                        config.lore.map {
                            platform.toPlatformComponent(
                                AdventureUtil.parseMiniMessage(
                                    it,
                                    *commonTagResolvers(null),
                                    *playerTagResolvers(executor)
                                ).decoration(TextDecoration.ITALIC, false)
                            )
                        },
                        false
                    )

                    val finalSlot = slot + i
                    inventory.item(finalSlot, item).also { inv ->
                        inv.onClick {
                            if(it.slot() == finalSlot) {
                                tryPreviousPage()?.also { page ->
                                    buildPageDisplay(page)
                                }
                            }
                        }
                    }
                } else if(definition.value.equals("next-page-item", true)) {
                    val config = toolbarConfig.nextPageItem
                    val item = ItemStack(config.material, min(max(config.amount, 1), 64))

                    if(item.itemType() == ItemType.PLAYER_HEAD) {
                        ProtocolizeUtil.setHeadProperties(item, config.playerHead)
                    }

                    item.displayName(
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                config.displayName,
                                *commonTagResolvers(null),
                                *playerTagResolvers(executor)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    )

                    item.lore(
                        config.lore.map {
                            platform.toPlatformComponent(
                                AdventureUtil.parseMiniMessage(
                                    it,
                                    *commonTagResolvers(null),
                                    *playerTagResolvers(executor)
                                ).decoration(TextDecoration.ITALIC, false)
                            )
                        },
                        false
                    )

                    val finalSlot = slot + i
                    inventory.item(finalSlot, item).also { inv ->
                        inv.onClick {
                            if(it.slot() == finalSlot) {
                                tryNextPage()?.also { page ->
                                    buildPageDisplay(page)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun commonTagResolvers(page: Page<PlatformServerGroup<S>>?): Array<TagResolver> {
        return mutableListOf(
            TagResolver.resolver("player", Tag.inserting(Component.text(executor.getName()))),
            TagResolver.resolver("total-pages", Tag.inserting(Component.text(calculateTotalPages()))),
        ).also {
            if(page != null) {
                it.add(TagResolver.resolver("page", Tag.inserting(Component.text(page.pageNumber + 1))))
                it.add(TagResolver.resolver("prev-page", Tag.inserting(Component.text(max(1, page.pageNumber + 1)))))
                it.add(TagResolver.resolver("next-page", Tag.inserting(Component.text(min(calculateTotalPages(), page.pageNumber + 1)))))
            }
        }.toTypedArray()
    }

    private fun playerTagResolvers(player: PlatformExecutor<S>): Array<TagResolver> {
        return mutableListOf(
            TagResolver.resolver("player-name", Tag.inserting(Component.text(player.getName()))),
            TagResolver.resolver("player-prefix", Tag.inserting(platform.playerManager.getPrefix(player))),
        ).toTypedArray()
    }

    private fun serverTagResolvers(server: PlatformServerGroup<S>): Array<TagResolver> {
        return mutableListOf(
            TagResolver.resolver("server-name", Tag.inserting(Component.text(server.getName()))),
            TagResolver.resolver("player-count", Tag.inserting(Component.text(server.getPlayersCount()))),
        ).toTypedArray()
    }

}
