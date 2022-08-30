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

import kotlin.math.ceil

open class Pageable<T>(
    initialPageSize: Int,
    initialData: MutableList<T> = mutableListOf()
) {

    var pageSize: Int = initialPageSize
        set(value) {
            field = value
            calculateTotalPages()
        }

    var data: MutableList<T> = initialData
        set(value) {
            field = value
            calculateTotalPages()
        }

    var totalPages = 0

    init {
        calculateTotalPages()
    }

    private fun calculateTotalPages() {
        totalPages = ceil(data.size / pageSize.toDouble()).toInt()
    }

    fun refresh() {
        calculateTotalPages()
    }

    fun getPage(pageNumber: Int): Page<T> {
        if(pageNumber < 0) throw IndexOutOfBoundsException("Page number cannot be less than 0.")
        if(pageNumber >= totalPages) throw IndexOutOfBoundsException("Page number cannot be equals or greater than total number of pages.")

        val start = pageSize * pageNumber
        var end = start + pageSize

        if(end > data.size) {
           end = data.size
        }

        val elements = data.subList(start, end).toList()

        return Page(pageNumber, totalPages, elements, pageNumber > 0, pageNumber < totalPages - 1)
    }

}
