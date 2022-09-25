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

package dev.wirlie.glist.common.util

import dev.simplix.protocolize.api.item.ItemStack
import dev.wirlie.glist.common.gui.config.PlayerHeadConfig
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import net.querz.nbt.tag.StringTag

object ProtocolizeUtil {

    fun setHeadProperties(item: ItemStack, playerHeadConfig: PlayerHeadConfig?) {
        if(playerHeadConfig == null) return
        if(playerHeadConfig.skullOwner != null) {
            setHeadOwner(item, playerHeadConfig.skullOwner!!)
        }
        if(playerHeadConfig.skinHash != null) {
            setHeadTexture(item, playerHeadConfig.skinHash!!)
        }
    }

    private fun setHeadOwner(item: ItemStack, owner: String) {
        (item.nbtData() as CompoundTag).put("SkullOwner", StringTag(owner))
    }

    private fun setHeadTexture(item: ItemStack, textureHash: String) {
        var skullOwner = (item.nbtData() as CompoundTag).getCompoundTag("SkullOwner")
        if (skullOwner == null) {
            skullOwner = CompoundTag()
        }
        skullOwner.put("Name", StringTag(textureHash))
        var properties = skullOwner.getCompoundTag("Properties")
        if (properties == null) {
            properties = CompoundTag()
        }
        val texture = CompoundTag()
        texture.put("Value", StringTag(textureHash))
        val textures: ListTag<CompoundTag> = ListTag(CompoundTag::class.java)
        textures.add(texture)
        properties.put("textures", textures)
        skullOwner.put("Properties", properties)
        (item.nbtData() as CompoundTag).put("SkullOwner", skullOwner)
    }

}
