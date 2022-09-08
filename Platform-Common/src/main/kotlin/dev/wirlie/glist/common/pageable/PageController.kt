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

package dev.wirlie.glist.common.pageable

/**
 * Pagination controller.
 * @param initialPageSize Page size.
 * @param initialData Data to use.
 */
open class PageController<T>(
    initialPageSize: Int,
    initialData: MutableList<T> = mutableListOf()
): Pageable<T>(
    initialPageSize,
    initialData
) {

    var currentPage = 0

    fun tryNextPage(): Page<T>? {
        if(currentPage >= totalPages) return null
        currentPage++
        return getPage(currentPage)
    }

    fun tryPreviousPage(): Page<T>? {
        if(currentPage <= 0) return null
        currentPage--
        return getPage(currentPage)
    }

}
