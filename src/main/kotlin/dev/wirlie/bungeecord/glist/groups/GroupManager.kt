package dev.wirlie.bungeecord.glist.groups

import dev.wirlie.bungeecord.glist.EnhancedBCL
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import java.util.logging.Level
import java.util.stream.Collectors
import kotlin.ClassCastException
import kotlin.Exception
import kotlin.NumberFormatException
import kotlin.String
import kotlin.toString

class GroupManager(plugin: EnhancedBCL) {
    private val groups: MutableList<Group> = ArrayList()
    private val groupFile: File
    private val plugin: EnhancedBCL

    init {
        groupFile = File(plugin.dataFolder, "GroupsPrefix.yml")
        this.plugin = plugin
    }

    fun loadGroups() {
        if (!groupFile.exists()) {
            plugin.logger.info("File not found: GroupsPrefix.yml | Generating file...")
            val parentFolder = groupFile.parentFile
            if (!parentFolder.exists()) {
                parentFolder.mkdirs()
            }
            try {
                Files.copy(javaClass.getResourceAsStream("/GroupsPrefix.yml")!!, groupFile.toPath())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (!groupFile.exists()) {
            plugin.logger.warning("File: GroupsPrefix.yml not found, check folder permissions, plugin cannot create default file...")
            return
        }

        plugin.logger.info("GroupsPrefix.yml found, reading configuration ...")
        try {
            groups.clear()
            val config =
                plugin.yamlProvider!!.load(InputStreamReader(FileInputStream(groupFile), StandardCharsets.UTF_8))

            //TODO: Esta variable no se usa, ¿por que?
            var staffGroupPermission = config.getString("staff-group.permission", null)

            if (staffGroupPermission == null) {
                plugin.logger.warning("No permission defined for staff members. Using default permission: 'ebl.groups.staff'")
                staffGroupPermission = "ebl.groups.staff"
            }

            if (!config.contains("groups")) {
                plugin.logger.warning("No groups defined. Adding default group for users.")
                val group = Group("default")
                group.isDefaultGroup = true
                group.nameColor = "&7"
                groups.add(group)
                return
            }

            try {
                @Suppress("UNCHECKED_CAST")
                val allData = config["groups"] as List<Map<*, *>>

                for (groupData in allData) {
                    try {
                        var id: String? = null
                        var weight = 0
                        var prefix: String? = null
                        var prefixPermission: String? = null
                        var prefixVisibleToUsers = true
                        var nameColor = ""
                        var defaultGroup = false

                        for ((key1, value1) in groupData) {
                            val key = key1.toString()
                            val value = value1.toString()
                            if (key.equals("id", ignoreCase = true)) {
                                id = value
                            } else if (key.equals("weight", ignoreCase = true)) {
                                try {
                                    weight = value.toInt()
                                } catch (e: NumberFormatException) {
                                    plugin.logger.warning("Unexpected weight value: $value")
                                }
                            } else if (key.equals("prefix", ignoreCase = true)) {
                                prefix = value
                            } else if (key.equals("prefix-permission", ignoreCase = true)) {
                                prefixPermission = value
                            } else if (key.equals("prefix-visible-to-users", ignoreCase = true)) {
                                prefixVisibleToUsers = value.toBoolean()
                            } else if (key.equals("name-color", ignoreCase = true)) {
                                nameColor = value
                            } else if (key.equals("default-group", ignoreCase = true)) {
                                try {
                                    defaultGroup = value.toBoolean()
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            }
                        }

                        if (id == null) {
                            plugin.logger.warning("Group not loaded: id must be defined")
                        } else {
                            val group = Group(id)
                            group.isDefaultGroup = defaultGroup
                            group.weight = weight
                            group.prefix = prefix
                            group.prefixPermission = prefixPermission
                            group.isVisibleToUsers = prefixVisibleToUsers
                            group.nameColor = nameColor
                            if (groups.contains(group)) {
                                plugin.logger.warning("Group '" + group.id + "' already loaded! Fixing id to: '" + group.id + "~1'")
                                group.id = "$id~1"
                            }
                            var fixedId = 1
                            while (groups.contains(group)) {
                                fixedId++
                                plugin.logger.warning("Group '" + group.id + "' already loaded! Fixing id to: '" + id + "~" + fixedId + "'")
                                group.id = "$id~$fixedId"
                            }
                            plugin.logger.info("Group '" + group.id + "' loaded. Weight: " + group.weight + ", permission: " + group.prefixPermission)
                            groups.add(group)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: ClassCastException) {
                plugin.logger.log(Level.SEVERE, "Cannot read GroupsPrefix.yml (ClassCastException)", e)
            }

            groups.sortBy { it }
            plugin.logger.info(groups.size.toString() + " groups loaded.")
            plugin.logger.info(groups.stream().map { obj: Group -> obj.id }
                .collect(Collectors.joining(", ", "[", "]")))
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Cannot read GroupsPrefix.yml (IOException)", e)
        }
    }

    fun getGroup(player: ProxiedPlayer): Optional<Group> {
        var defaultGroup: Group? = null
        for (group in groups) {
            if (group.isDefaultGroup) {
                defaultGroup = group
                continue
            }

            if (group.prefixPermission == null || group.prefixPermission!!.isEmpty() || player.hasPermission(group.prefixPermission)) {
                return Optional.of(group)
            }
        }
        return Optional.ofNullable(defaultGroup)
    }
}
