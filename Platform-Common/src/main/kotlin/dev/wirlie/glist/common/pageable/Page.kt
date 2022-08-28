package dev.wirlie.glist.common.pageable

data class Page<T>(
    val pageNumber: Int,
    val totalPages: Int,
    val items: List<T>
)
