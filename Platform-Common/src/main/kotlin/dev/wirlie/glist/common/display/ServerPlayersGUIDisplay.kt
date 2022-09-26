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
import dev.simplix.protocolize.api.player.ProtocolizePlayer
import dev.simplix.protocolize.data.ItemType
import dev.simplix.protocolize.data.inventory.InventoryType
import dev.wirlie.glist.common.Platform
import dev.wirlie.glist.common.gui.GUIInventory
import dev.wirlie.glist.common.gui.config.toolbar.ItemDefinitionConfig
import dev.wirlie.glist.common.gui.config.toolbar.MenuDefinitionConfig
import dev.wirlie.glist.common.pageable.Page
import dev.wirlie.glist.common.platform.PlatformExecutor
import dev.wirlie.glist.common.platform.PlatformServerGroup
import dev.wirlie.glist.common.util.AdventureUtil
import dev.wirlie.glist.common.util.ProtocolizeUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.querz.nbt.tag.StringTag
import kotlin.math.max
import kotlin.math.min

// TODO: Inventories should be notified when toolbar configuration is changed to prevent visual inconsistencies
/**
 * Display for connected players (server).
 * @param platform Platform instance.
 * @param serverGroup Server group to use for players list.
 * @param executor Command executor to filter vanished players if executor does not have permission to see vanished players.
 * @param audience Audience to send the result of this display.
 * @param menuRows Number of rows.
 */
class ServerPlayersGUIDisplay<S>(
    platform: Platform<S, *, *>,
    serverGroup: PlatformServerGroup<S>,
    executor: PlatformExecutor<S>,
    audience: Audience,
    private val menuRows: Int
): ServerPlayersAbstractDisplay<S>(
    platform, serverGroup, executor, audience, (menuRows - 1) * 9
) {

    val configuration = platform.guiManager!!.slistConfig

    // Optimization, prevent multiple page calculations and only calculate once per update
    private var temporalTotalPages = 0
    private var shouldRegisterClickActions = true

    private lateinit var inventory : Inventory
    private val protocolPlayer: ProtocolizePlayer = Protocolize.playerProvider().player(executor.getUUID())

    override fun buildPageDisplay(page: Page<PlatformExecutor<S>>) {
        temporalTotalPages = calculateTotalPages()

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
                val playerItem = page.items[i]
                val item = ItemStack(generalItem.material, min(max(generalItem.amount, 1), 64))

                item.displayName(
                    platform.toPlatformComponent(
                        AdventureUtil.parseMiniMessage(
                            generalItem.displayName,
                            *commonTagResolvers(page),
                            *playerTagResolvers(playerItem)
                        ).decoration(TextDecoration.ITALIC, false)
                    )
                )
                item.lore(
                    generalItem.lore.map {
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                it,
                                *commonTagResolvers(page),
                                *playerTagResolvers(playerItem)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    },
                    false
                )

                if (generalItem.material == ItemType.PLAYER_HEAD) {
                    item.nbtData().put("SkullOwner", StringTag(playerItem.getName()))
                }

                inventory.item(i, item)
            } else {
                val item = ItemStack(emptyItem.material, emptyItem.amount)

                if(item.itemType() == ItemType.PLAYER_HEAD) {
                    ProtocolizeUtil.setHeadProperties(item, emptyItem.playerHead)
                }

                item.displayName(
                    platform.toPlatformComponent(
                        AdventureUtil.parseMiniMessage(
                            emptyItem.displayName,
                            *commonTagResolvers(page)
                        ).decoration(TextDecoration.ITALIC, false)
                    )
                )
                item.lore(
                    emptyItem.lore.map {
                        platform.toPlatformComponent(
                            AdventureUtil.parseMiniMessage(
                                it,
                                *commonTagResolvers(page)
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                    },
                    false
                )

                inventory.item(i, item)
            }
        }

        fillToolbar()
        shouldRegisterClickActions = true

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
                    if(shouldRegisterClickActions) {
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
                        if(shouldRegisterClickActions) {
                            inv.onClick {
                                if(it.slot() == finalSlot) {
                                    tryPreviousPage()?.also { page ->
                                        buildPageDisplay(page)
                                    }
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
                        if(shouldRegisterClickActions) {
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
    }

    private fun commonTagResolvers(page: Page<PlatformExecutor<S>>?): Array<TagResolver> {
        return mutableListOf(
            TagResolver.resolver("server-name", Tag.inserting(Component.text(serverGroup.getName()))),
            TagResolver.resolver("player", Tag.inserting(Component.text(executor.getName()))),
            TagResolver.resolver("total-pages", Tag.inserting(Component.text(temporalTotalPages))),
        ).also {
            if(page != null) {
                it.add(TagResolver.resolver("page", Tag.inserting(Component.text(page.pageNumber + 1))))
                it.add(TagResolver.resolver("prev-page", Tag.inserting(Component.text(max(1, page.pageNumber + 1)))))
                it.add(TagResolver.resolver("next-page", Tag.inserting(Component.text(min(temporalTotalPages, page.pageNumber + 1)))))
            }
        }.toTypedArray()
    }

    private fun playerTagResolvers(player: PlatformExecutor<S>): Array<TagResolver> {

        return mutableListOf(
            TagResolver.resolver("player-name", Tag.inserting(Component.text(player.getName()))),
            TagResolver.resolver("player-prefix", Tag.inserting(platform.playerManager.getPrefix(player))),
        ).also {
            if(platform.playerManager.hasAFKState(player.getUUID())) {
                it.add(TagResolver.resolver("afk-status", Tag.selfClosingInserting(
                    AdventureUtil.parseMiniMessage(configuration.format.afkStatus)
                )))
            } else {
                it.add(TagResolver.resolver("afk-status", Tag.inserting(Component.empty())))
            }

            if(platform.playerManager.hasVanishState(player.getUUID())) {
                it.add(TagResolver.resolver("vanish-status", Tag.selfClosingInserting(
                    AdventureUtil.parseMiniMessage(configuration.format.vanishStatus)
                )))
            } else {
                it.add(TagResolver.resolver("vanish-status", Tag.inserting(Component.empty())))
            }
        }.toTypedArray()
    }

}
