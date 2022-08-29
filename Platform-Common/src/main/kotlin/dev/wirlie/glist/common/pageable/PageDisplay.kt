package dev.wirlie.glist.common.pageable

import net.kyori.adventure.audience.Audience
import kotlin.math.max

abstract class PageDisplay<T>(
    val audience: Audience,
    initialPageSize: Int,
    initialData: MutableList<T> = mutableListOf()
): PageController<T>(
    initialPageSize,
    initialData
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
