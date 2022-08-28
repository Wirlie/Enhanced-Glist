package dev.wirlie.glist.common.pageable

import net.kyori.adventure.audience.Audience

abstract class PageDisplay<T>(
    val audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<T> = mutableListOf()
): PageController<T>(
    initialPageSize,
    initialData
) {

    abstract fun showPage(page: Page<T>)

    fun showNextPage(): Boolean {
        val nextPage = tryNextPage() ?: return false
        showPage(nextPage)
        return true
    }

    fun showPreviousPage(): Boolean {
        val previousPage = tryPreviousPage() ?: return false
        showPage(previousPage)
        return true
    }

    fun showCurrentPage() {
        showPage(getPage(currentPage))
    }

    fun showPage(pageNumber: Int) {
        showPage(getPage(pageNumber))
    }

}
