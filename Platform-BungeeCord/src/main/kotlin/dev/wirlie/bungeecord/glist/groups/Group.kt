package dev.wirlie.bungeecord.glist.groups

import java.util.*

class Group(var id: String) : Comparable<Group> {

    var weight = 0
    var prefix: String? = null
    var prefixPermission: String? = null
    var isVisibleToUsers = true
    var nameColor = "&7"
    var isDefaultGroup = false

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Group) {
            return other.id == id
        }
        return super.equals(other)
    }

    override fun compareTo(o: Group): Int {
        return weight.compareTo(o.weight)
    }

}
