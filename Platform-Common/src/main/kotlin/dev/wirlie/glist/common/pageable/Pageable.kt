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

        return Page(pageNumber, totalPages, elements)
    }

}
