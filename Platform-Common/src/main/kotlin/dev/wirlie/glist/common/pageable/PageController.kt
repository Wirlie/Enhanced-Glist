package dev.wirlie.glist.common.pageable

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
