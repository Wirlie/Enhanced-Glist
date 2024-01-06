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

package dev.wirlie.glist.common.pageable

import net.kyori.adventure.audience.Audience
import kotlin.math.max

/**
 * Display for multiples pages.
 * @param audience Target audience to send the result of this display.
 * @param pageSize Page size.
 * @param dataProvider Data to use.
 */
abstract class PageDisplay<T>(
    val audience: Audience,
    pageSize: Int,
    dataProvider: DataProvider<T>
): PageController<T>(
    pageSize,
    dataProvider
) {

    abstract fun buildPageDisplay(page: Page<T>)

    fun showNextPage(): Boolean {
        val nextPage = tryNextPage() ?: return false
        buildPageDisplay(nextPage)
        return true
    }

    fun showPreviousPage(): Boolean {
        val previousPage = tryPreviousPage() ?: return false
        buildPageDisplay(previousPage)
        return true
    }

    fun showCurrentPage() {
        buildPageDisplay(getPage(currentPage))
    }

    fun showPage(pageNumber: Int) {
        val totalPages = calculateTotalPages()

        currentPage = if(pageNumber < 0) {
            0
        } else if(pageNumber >= totalPages) {
            max(0, totalPages - 1)
        } else {
            pageNumber
        }

        buildPageDisplay(getPage(currentPage))
    }

}
