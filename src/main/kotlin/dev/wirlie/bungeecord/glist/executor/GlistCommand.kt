package dev.wirlie.bungeecord.glist.executor

import dev.wirlie.bungeecord.glist.EnhancedBCL
import dev.wirlie.bungeecord.glist.TemporalPaginator
import dev.wirlie.bungeecord.glist.config.Config
import dev.wirlie.bungeecord.glist.servers.BungeecordInfoProvider
import dev.wirlie.bungeecord.glist.servers.ServerGroup
import dev.wirlie.bungeecord.glist.servers.ServerInfoProvider
import dev.wirlie.bungeecord.glist.util.PlayerGlistEntry
import dev.wirlie.bungeecord.glist.util.TextUtil.makeRowsNew
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor
import java.text.Collator
import java.text.NumberFormat
import java.util.*
import java.util.stream.Collectors

class GlistCommand(private val plugin: EnhancedBCL, name: String?, permission: String?, vararg aliases: String?) :
    Command(name, permission, *aliases), TabExecutor {
    private val format = NumberFormat.getNumberInstance()

    init {
        format.maximumFractionDigits = 2
    }

    fun reload() {
        synchronized(serversPaginators) { serversPaginators.clear() }
    }

    override fun execute(sender: CommandSender, args: Array<String>) {

        val arguments = args.toMutableList()
        val isPlayerExecutor = sender is ProxiedPlayer
        val audience = plugin.adventure().sender(sender)
        val options: MutableSet<String> = HashSet()

        val argumentsIterator = arguments.iterator()

        while (argumentsIterator.hasNext()) {
            val arg = argumentsIterator.next()

            if (arg.equals("-g", ignoreCase = true)) {
                options.add("-g")
                argumentsIterator.remove()
            } else if (arg.equals("-sp", ignoreCase = true)) {
                options.add("-sp")
                argumentsIterator.remove()
            } else if (arg.equals("-a", ignoreCase = true)) {
                options.add("-a")
                argumentsIterator.remove()
            }
        }

        if (arguments.size == 0) {
            printGlobal(audience, options)
        } else {
            var page: Int
            val partsController: Array<String>
            val serverName = arguments[0]

            //is blacklisted?
            if (Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get().stream()
                    .anyMatch { s: String -> s.equals(serverName, ignoreCase = true) }
            ) {
                audience.sendMessage(
                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                        Config.MESSAGES__CANNOT_FOUND_SERVER.get().replace("{NAME}", serverName)
                    )
                )
                return
            }

            var serverInfoPre: ServerInfoProvider?

            //try with group first
            serverInfoPre = plugin.getServerGroups().stream()
                .filter { g: ServerGroup -> g.id.equals(serverName, ignoreCase = true) }
                .findFirst().orElse(null)

            if (serverInfoPre == null) {
                //try with bungeecord
                val bungeeServer = BungeeCord.getInstance().getServerInfo(serverName)
                if (bungeeServer != null) {
                    serverInfoPre = BungeecordInfoProvider(bungeeServer)
                }
            }

            val serverInfo = serverInfoPre

            if (serverInfo == null) {
                audience.sendMessage(
                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                        Config.MESSAGES__CANNOT_FOUND_SERVER.get().replace("{NAME}", serverName)
                    )
                )
            } else {

                val temporalPaginator = serversPaginators.computeIfAbsent(serverInfo.id) { _: String? ->
                    TemporalPaginator(serverInfo.players.stream().map { cs: ProxiedPlayer ->
                        var prefix = plugin.getPrefix(cs)

                        if (prefix == null || prefix.trim { it <= ' ' }.equals("null", ignoreCase = true)) {
                            prefix = ""
                        }

                        PlayerGlistEntry(cs, prefix, plugin.activityManager.getActivities(cs.uniqueId))
                    }.collect(Collectors.toList()), Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get())
                }

                if (temporalPaginator.shouldUpdate(Config.BEHAVIOUR__CACHE_TIME__PLAYER_LIST_PAGES.get() * 1000L)) {
                    temporalPaginator.update(serverInfo.players.stream().map { cs: ProxiedPlayer ->
                        var prefix = plugin.getPrefix(cs)

                        if (prefix == null || prefix!!.trim { it <= ' ' }.equals("null", ignoreCase = true)) {
                            prefix = ""
                        }

                        PlayerGlistEntry(cs, prefix!!, plugin.activityManager.getActivities(cs.uniqueId))
                    }.collect(Collectors.toList()))
                }

                page = 1

                if (arguments.size > 1) {
                    try {
                        page = arguments[1].toInt()
                    } catch (ignored: NumberFormatException) {
                    }
                }

                val pageData = if (options.contains("-g")) if (isPlayerExecutor) temporalPaginator.getFullDataVisible(
                    (sender as ProxiedPlayer)
                ) else temporalPaginator.fullData else if (isPlayerExecutor) temporalPaginator.getVisiblePage(
                    page,
                    (sender as ProxiedPlayer)
                ) else temporalPaginator.getFullPage(page)

                if (pageData.isEmpty()) {
                    val data = if (isPlayerExecutor) temporalPaginator.getFullDataVisible(
                        (sender as ProxiedPlayer)
                    ) else temporalPaginator.fullData

                    val totalPages = temporalPaginator.resolveNumOfPages(data)

                    if (totalPages > 0) {
                        for (line in Config.FORMATS__SERVER_LIST__NO_PAGE_DATA_MESSAGE.get()) {
                            audience.sendMessage(
                                EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                    line.replace(
                                        "{TOTAL_PAGES}",
                                        totalPages.toString()
                                    )
                                )
                            )
                        }
                    } else {
                        for (line in Config.FORMATS__SERVER_LIST__NO_PLAYERS_MESSAGE.get()) {
                            audience.sendMessage(EnhancedBCL.defaultLegacyDeserializer.deserialize(line!!))
                        }
                    }
                } else {
                    val data = if (isPlayerExecutor) temporalPaginator.getFullDataVisible(
                        (sender as ProxiedPlayer)
                    ) else temporalPaginator.fullData

                    val totalPages = temporalPaginator.resolveNumOfPages(data)

                    var message =
                        java.lang.String.join("\n", ArrayList(Config.FORMATS__SERVER_LIST__FULL_MESSAGE_FORMAT.get()))
                            .replace(
                                "{PLAYERS_ROWS}", makeRowsNew(
                                    Config.FORMATS__SERVER_LIST__PLAYERS_PER_ROW.get(),
                                    (page - 1) * Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get() + 1,
                                    pageData
                                )
                            ).replace(
                            "{SERVER_NAME}",
                            if (Config.BEHAVIOUR__SERVER_LIST__UPPER_CASE_NAME.get()) serverInfo.displayName.uppercase(
                                Locale.getDefault()
                            ) else serverInfo.displayName
                        ).replace("{PLAYERS_COUNT}", data.size.toString()).replace(
                            "{PAGE}",
                            if (options.contains("-g")) Config.MESSAGES__ALL_PAGES.get() else page.toString()
                        ).replace("{TOTAL_PAGES}", totalPages.toString())
                    if (options.contains("-g")) {
                        partsController = message.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                        for (line in partsController) {
                            if (!line.contains("{PAGINATION_CONTROLLER}")) {
                                audience.sendMessage(EnhancedBCL.defaultLegacyDeserializer.deserialize(line))
                            }
                        }
                    } else if (message.contains("{PAGINATION_CONTROLLER}")) {
                        partsController =
                            message.split("\\{PAGINATION_CONTROLLER}".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var mainComponent = EnhancedBCL.defaultLegacyDeserializer.deserialize(partsController[0])

                        var cb: TextComponent? = null

                        if (page in 2 until totalPages) {
                            cb = Component.empty()
                            cb = if (isPlayerExecutor) {
                                cb.append(
                                    Component.text("<<", NamedTextColor.WHITE)
                                        .clickEvent(ClickEvent.runCommand("/" + name + " " + serverInfo.id + " " + (page - 1)))
                                        .hoverEvent(
                                            HoverEvent.showText(
                                                EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                    Config.MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE.get()
                                                        .replace("{PAGE_NUMBER}", (page - 1).toString())
                                                )
                                            )
                                        )
                                        .append(
                                            Component.text(
                                                " " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ",
                                                NamedTextColor.GOLD
                                            )
                                        )
                                )
                                    .append(Component.text("|", NamedTextColor.DARK_GRAY))
                                    .append(
                                        Component.text(
                                            " " + Config.MESSAGES__NEXT_PAGE.get() + " ",
                                            NamedTextColor.GOLD
                                        )
                                            .clickEvent(ClickEvent.runCommand("/" + name + " " + serverInfo.id + " " + (page + 1)))
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get()
                                                            .replace("{PAGE_NUMBER}", (page + 1).toString())
                                                    )
                                                )
                                            )
                                            .append(Component.text(">>", NamedTextColor.WHITE))
                                    )

                                //TODO: REMOVE
                                /*cb.append("<<")
										.color(ChatColor.WHITE)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page - 1)) )))
										.append(" " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ")
										.color(ChatColor.GOLD)
										.append("|", FormatRetention.NONE)
										.color(ChatColor.DARK_GRAY)
										.append(" " + Config.MESSAGES__NEXT_PAGE.get() + " ")
										.color(ChatColor.GOLD)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getId() + " " + (page + 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
										.append(">>").color(ChatColor.WHITE);*/
                            } else {
                                //TODO: MAKE TRANSLATABLE
                                cb.append(
                                    Component.text("Use ", NamedTextColor.GOLD)
                                        .append(
                                            Component.text(
                                                "/" + name + " " + serverInfo.id + " " + (page - 1),
                                                NamedTextColor.WHITE
                                            )
                                        )
                                        .append(Component.text(" to go to the previous page.\n", NamedTextColor.GOLD))
                                        .append(Component.text("Use ", NamedTextColor.GOLD))
                                        .append(
                                            Component.text(
                                                "/" + name + " " + serverInfo.id + " " + (page + 1),
                                                NamedTextColor.WHITE
                                            )
                                        )
                                        .append(Component.text("to go to the next page.", NamedTextColor.GOLD))
                                )

                                //TODO: REMOVE
                                //cb.append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1)).color(ChatColor.WHITE).append(" to go to the previous page.\n").color(ChatColor.GOLD).append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getId() + " " + (page + 1)).color(ChatColor.WHITE).append(" to go to the next page.").color(ChatColor.GOLD);
                            }
                        } else if (page <= 1) {
                            if (page + 1 <= totalPages) {
                                cb = Component.empty()
                                cb = if (isPlayerExecutor) {
                                    cb.append(
                                        Component.text("<<", NamedTextColor.DARK_RED)
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__NO_PREVIOUS_PAGE.get()
                                                    )
                                                )
                                            )
                                            .append(
                                                Component.text(
                                                    " " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ",
                                                    NamedTextColor.RED
                                                )
                                            )
                                    )
                                        .append(Component.text("|", NamedTextColor.DARK_GRAY))
                                        .append(
                                            Component.text(
                                                " " + Config.MESSAGES__NEXT_PAGE.get() + " ",
                                                NamedTextColor.GOLD
                                            )
                                                .clickEvent(ClickEvent.runCommand("/" + name + " " + serverInfo.id + " " + (page + 1)))
                                                .hoverEvent(
                                                    HoverEvent.showText(
                                                        EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                            Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get()
                                                                .replace("{PAGE_NUMBER}", (page + 1).toString())
                                                        )
                                                    )
                                                )
                                                .append(Component.text(">>", NamedTextColor.WHITE))
                                        )

                                    //TODO: REMOVE
                                    /*cb.append("<<")
											.color(ChatColor.DARK_RED)
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NO_PREVIOUS_PAGE.get())))
											.append(" " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ")
											.color(ChatColor.RED)
											.append("|", FormatRetention.NONE)
											.color(ChatColor.DARK_GRAY)
											.append(" " + Config.MESSAGES__NEXT_PAGE.get() + " ")
											.color(ChatColor.GOLD)
											.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getId() + " " + (page + 1)))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
											.append(">>").color(ChatColor.WHITE);*/
                                } else {
                                    cb.append(
                                        Component.text("Use ", NamedTextColor.GOLD)
                                            .append(
                                                Component.text(
                                                    "/" + name + " " + serverInfo.id + " " + (page + 1),
                                                    NamedTextColor.WHITE
                                                )
                                            )
                                            .append(Component.text(" to go to the next page.", NamedTextColor.GOLD))
                                    )
                                }
                            } else if (isPlayerExecutor) {
                                cb = Component.empty()
                                    .append(
                                        Component.text("<<", NamedTextColor.DARK_RED)
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__NO_PREVIOUS_PAGE.get()
                                                    )
                                                )
                                            )
                                            .append(
                                                Component.text(
                                                    " " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ",
                                                    NamedTextColor.RED
                                                )
                                            )
                                    )
                                    .append(Component.text("|", NamedTextColor.DARK_GRAY))
                                    .append(
                                        Component.text(" " + Config.MESSAGES__NEXT_PAGE.get() + " ", NamedTextColor.RED)
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__NO_NEXT_PAGE.get()
                                                    )
                                                )
                                            )
                                            .append(Component.text(">>", NamedTextColor.DARK_RED))
                                    )

                                //TODO: REMOVE
                                /*
								cb = new ComponentBuilder("<<")
										.color(ChatColor.DARK_RED)
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NO_PREVIOUS_PAGE.get())))
										.append(" " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ")
										.color(ChatColor.RED)
										.append("|", FormatRetention.NONE)
										.color(ChatColor.DARK_GRAY)
										.append(" " + Config.MESSAGES__NEXT_PAGE.get() + " ")
										.color(ChatColor.RED)
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NO_NEXT_PAGE.get())))
										.append(">>")
										.color(ChatColor.DARK_RED);*/
                            }
                        } else if (page >= totalPages) {
                            cb = if (isPlayerExecutor) {
                                Component.empty()
                                    .append(
                                        Component.text("<<", NamedTextColor.WHITE)
                                            .clickEvent(ClickEvent.runCommand("/" + name + " " + serverInfo.id + " " + (page - 1)))
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE.get()
                                                            .replace("{PAGE_NUMBER}", (page - 1).toString())
                                                    )
                                                )
                                            )
                                            .append(
                                                Component.text(
                                                    " " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ",
                                                    NamedTextColor.GOLD
                                                )
                                            )
                                    )
                                    .append(Component.text("|", NamedTextColor.DARK_GRAY))
                                    .append(
                                        Component.text(" " + Config.MESSAGES__NEXT_PAGE.get() + " ", NamedTextColor.RED)
                                            .hoverEvent(
                                                HoverEvent.showText(
                                                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                                        Config.MESSAGES__NO_NEXT_PAGE.get()
                                                    )
                                                )
                                            )
                                            .append(Component.text(">>", NamedTextColor.DARK_RED))
                                    )
                                //TODO: REMOVE
                                /*
								cb = new ComponentBuilder("<<")
										.color(ChatColor.WHITE)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page - 1)))))
										.append(" " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ")
										.color(ChatColor.GOLD)
										.append("|", FormatRetention.NONE)
										.color(ChatColor.DARK_GRAY)
										.append(" " + Config.MESSAGES__NEXT_PAGE.get() + " ")
										.color(ChatColor.RED)
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NO_NEXT_PAGE.get())))
										.append(">>")
										.color(ChatColor.DARK_RED);*/
                            } else {
                                Component.text("Use ", NamedTextColor.GOLD)
                                    .append(
                                        Component.text(
                                            "/" + name + " " + serverInfo.id + " " + (page - 1),
                                            NamedTextColor.WHITE
                                        )
                                    )
                                    .append(Component.text(" to go to the previous page.", NamedTextColor.GOLD))
                                //TODO: REMOVE
                                /*
								cb = new ComponentBuilder("Use ")
										.color(ChatColor.GOLD)
										.append("/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1))
										.color(ChatColor.WHITE)
										.append(" to go to the previous page.")
										.color(ChatColor.GOLD);*/
                            }
                        }
                        if (cb != null) {
                            //TODO: REMOVE
                            /*TextComponent[] bcObject = cb.create();

							for(int j = 0; j < bcObject.length; ++j) {
								TextComponent bc = bcObject[j];
								mainComponent.addExtra(bc);
							}*/
                            mainComponent = mainComponent.append(cb)
                        }
                        if (partsController.size > 1) {
                            //TODO: REMOVE
                            /*TextComponent[] bcObject = TextUtil.fromLegacy(partsController[1]);

							for(int j = 0; j < bcObject.length; ++j) {
								TextComponent bc = bcObject[j];
								mainComponent.addExtra(bc);
							}*/
                            mainComponent = mainComponent.append(
                                EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                    partsController[1]
                                )
                            )
                        }
                        audience.sendMessage(mainComponent)
                    } else {
                        if (message.endsWith("\n")) {
                            message = message.substring(0, message.length - 2)
                        }
                        audience.sendMessage(EnhancedBCL.defaultLegacyDeserializer.deserialize(message))
                    }
                }
            }
        }
    }

    private fun printGlobal(audience: Audience, options: Set<String>) {
        val allServers: MutableList<ServerInfoProvider> = ArrayList()

        //bungeecord servers
        for (sv in BungeeCord.getInstance().servers.values) {
            if (!plugin.isInGroup(sv)) {
                allServers.add(BungeecordInfoProvider(sv))
            }
        }

        //groups servers
        allServers.addAll(plugin.getServerGroups())
        val servers = allServers.stream()
            .sorted { o: ServerInfoProvider, o1: ServerInfoProvider -> Integer.compare(o1.playerCount, o.playerCount) }
            .filter { o: ServerInfoProvider ->
                if (Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get().contains(o.id)) {
                    return@filter false
                }
                if (options.contains("-a")) {
                    if (o.playerCount > 0) {
                        return@filter true
                    }
                }
                if (options.contains("-g")) {
                    return@filter true
                } else if (Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get() >= 1) {
                    return@filter o.playerCount >= Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get()
                } else {
                    return@filter o.playerCount > 0 || o.playerCount == 0 && !Config.BEHAVIOUR__GLOBAL_LIST__HIDE_EMPTY_SERVERS.get()
                }
            }
            .limit(
                if (Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get() < 1) Long.MAX_VALUE else if (options.contains("-g")) Long.MAX_VALUE else Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get().toLong()
            ).collect(
                Collectors.toList()
            )

        val componentsServerRow: MutableList<TextComponent> = ArrayList()
        val totalPlayers = BungeeCord.getInstance().players.size

        if (servers.isEmpty()) {
            componentsServerRow.add(EnhancedBCL.defaultLegacyDeserializer.deserialize(Config.FORMATS__GLOBAL_LIST__NO_SERVERS_FORMAT.get()))
        } else {
            val playerCount = servers[0].playerCount
            val serversIterator = servers.iterator()

            mainWhile@ while (true) {
                var serverInfo: ServerInfoProvider
                do {
                    do {
                        if (!serversIterator.hasNext()) {
                            break@mainWhile
                        }
                        serverInfo = serversIterator.next()
                        val percent = if (totalPlayers == 0) 0.0f else serverInfo.playerCount * 100.0f / totalPlayers
                        val percentGraphic = if (playerCount == 0) 0.0f else serverInfo.playerCount * 100f / playerCount
                        val graphicBarBuilder = StringBuilder()
                        val barPercent = 5.0f
                        val totalBars = (percentGraphic / barPercent).toInt()
                        for (i in 0..19) {
                            if (i < totalBars) {
                                graphicBarBuilder.append(Config.FORMATS__GLOBAL_LIST__GRAPHIC_BAR_COLOR.get())
                                    .append("|")
                            } else {
                                graphicBarBuilder.append(Config.FORMATS__GLOBAL_LIST__GRAPHIC_BACKGROUND_COLOR.get())
                                    .append("|")
                            }
                        }
                        componentsServerRow.add(
                            EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                Config.FORMATS__GLOBAL_LIST__SERVER_ROW_FORMAT.get().replace(
                                    "{SERVER_NAME}",
                                    if (Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get()) serverInfo.id.uppercase(
                                        Locale.getDefault()
                                    ) else serverInfo.id
                                ).replace("{PLAYER_AMOUNT}", serverInfo.players.size.toString())
                                    .replace("{GRAPHIC_BAR}", graphicBarBuilder.toString())
                                    .replace("{PERCENT}", format.format(percent.toDouble()) + "%")
                            )
                                .clickEvent(ClickEvent.runCommand("/glist " + serverInfo.id))
                                .hoverEvent(
                                    HoverEvent.showText(
                                        EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                            Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get()
                                                .replace("{SERVER_NAME}", serverInfo.id)
                                        )
                                    )
                                )
                        )
                        //TODO: REMOVE
                        /*
                        componentsServerRow.add(
                            new ComponentBuilder("")
                            .event(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append(TextUtil.fromLegacy(Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get().replace("{SERVER_NAME}", serverInfo.getId()))).create())
                            )
                            .event(
                                new ClickEvent(Action.RUN_COMMAND, "/glist " + serverInfo.getId())
                            )
                            .append(
                                TextUtil.fromLegacy(Config.FORMATS__GLOBAL_LIST__SERVER_ROW_FORMAT.get().replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getId().toUpperCase() : serverInfo.getId()).replace("{PLAYER_AMOUNT}", String.valueOf(serverInfo.getPlayers().size())).replace("{GRAPHIC_BAR}", graphicBarBuilder.toString()).replace("{PERCENT}", this.format.format(percent) + "%"))
                            )
                            .create()
                        );*/
                    } while (!options.contains("-sp"))
                } while (serverInfo.playerCount == 0)

                val mainFormat = Config.FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__MAIN_FORMAT.get()
                val playersFormat = Config.FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__PLAYERS_FORMAT.get()
                val playersString = StringBuilder()
                val players: List<ProxiedPlayer> = ArrayList(serverInfo.players)

                for (i in players.indices) {
                    val player = players[i]

                    if (i == players.size - 1) {
                        //eliminar formato
                        val indexOf = playersFormat.indexOf("{PLAYER_NAME}")
                        if (indexOf != -1) {
                            val prefix = plugin.getPrefix(player)
                            val isEmptyPrefix = prefix != null && ChatColor.stripColor(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    prefix
                                )
                            ).isEmpty()
                            val playerName =
                                (if (prefix == null) "" else prefix + if (isEmptyPrefix) "" else " ") + player.name
                            playersString.append(
                                playersFormat.substring(0, indexOf + "{PLAYER_NAME}".length)
                                    .replace("{PLAYER_NAME}", playerName)
                            )
                        } else {
                            val prefix = plugin.getPrefix(player)
                            val isEmptyPrefix = prefix != null && ChatColor.stripColor(
                                ChatColor.translateAlternateColorCodes(
                                    '&',
                                    prefix
                                )
                            ).isEmpty()
                            val playerName =
                                (if (prefix == null) "" else prefix + if (isEmptyPrefix) "" else " ") + player.name
                            playersString.append(playersFormat.replace("{PLAYER_NAME}", playerName))
                        }
                    } else {
                        val prefix = plugin.getPrefix(player)
                        val isEmptyPrefix =
                            prefix != null && ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix))
                                .isEmpty()
                        val playerName =
                            (if (prefix == null) "" else prefix + if (isEmptyPrefix) "" else " ") + player.name
                        playersString.append(playersFormat.replace("{PLAYER_NAME}", playerName))
                    }
                }

                componentsServerRow.add(
                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                        mainFormat.replace(
                            "{SERVER_NAME}",
                            if (Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get()) serverInfo.id.uppercase(
                                Locale.getDefault()
                            ) else serverInfo.id
                        ).replace("{PLAYERS_FORMAT}", playersString.toString())
                    )
                        .hoverEvent(
                            HoverEvent.showText(
                                EnhancedBCL.defaultLegacyDeserializer.deserialize(
                                    Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get().replace("{SERVER_NAME}", serverInfo.id)
                                )
                            )
                        )
                        .clickEvent(ClickEvent.runCommand("/glist " + serverInfo.id))
                )

                //TODO: REMOVE
                /*
                componentsServerRow.add(
                    new ComponentBuilder("")
                    .event(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append(TextUtil.fromLegacy(Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get().replace("{SERVER_NAME}", serverInfo.getId()))).create())
                    )
                    .event(
                        new ClickEvent(Action.RUN_COMMAND, "/glist " + serverInfo.getId())
                    )
                    .append(
                        TextUtil.fromLegacy(mainFormat.replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getId().toUpperCase() : serverInfo.getId()).replace("{PLAYERS_FORMAT}", playersString.toString()))
                    )
                    .create()
                );*/
            }
        }
        val notDisplayedCount = BungeeCord.getInstance().servers.size - servers.size
        val fullMessageCopy: List<String> = ArrayList(Config.FORMATS__GLOBAL_LIST__FULL_MESSAGE_FORMAT.get())

        for (line in fullMessageCopy) {
            if (line.contains("{SERVERS_ROWS}")) {
                for (message in componentsServerRow) {
                    audience.sendMessage(message)
                }
            } else {
                audience.sendMessage(
                    EnhancedBCL.defaultLegacyDeserializer.deserialize(
                        ChatColor.translateAlternateColorCodes(
                            '&',
                            line.replace("{NOT_DISPLAYED_AMOUNT}", notDisplayedCount.toString())
                                .replace("{TOTAL_PLAYER_AMOUNT}", totalPlayers.toString()).replace("{LABEL}", name)
                        )
                    )
                )
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<String>): Iterable<String> {
        if (args.size == 1) {
            val blackListedServers = Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get()
            val suggestions = BungeeCord.getInstance().servers.values.stream().filter { s: ServerInfo ->
                //hide blacklisted
                if (blackListedServers.stream().anyMatch { ss: String -> ss.equals(s.name, ignoreCase = true) }) {
                    return@filter false
                }
                s.name.lowercase(Locale.getDefault()).contains(args[0].lowercase(Locale.getDefault()))
            }.map { s: ServerInfo -> s.name.lowercase(Locale.getDefault()) }.collect(Collectors.toSet())

            //add groups as suggestions
            val groupSuggestions =
                plugin.getServerGroups().stream().map { s: ServerGroup -> s.id.lowercase(Locale.getDefault()) }
                    .collect(Collectors.toList())
            //remove blacklisted
            groupSuggestions.removeIf { g: String? ->
                blackListedServers.stream().anyMatch { ss: String -> ss.equals(g, ignoreCase = true) }
            }
            suggestions.addAll(groupSuggestions)
            val sortedSuggestions: MutableList<String> = ArrayList(suggestions)
            sortedSuggestions.sortedWith { s1, s2 -> Collator.getInstance(Locale.US).compare(s1, s2) }

            if (args[0].isEmpty() || args[0].startsWith("-")) {
                sortedSuggestions.add(0, "-g")
                sortedSuggestions.add(0, "-sp")
                sortedSuggestions.add(0, "-a")
            }
            return sortedSuggestions
        }
        return emptyList()
    }

    companion object {
		val serversPaginators: MutableMap<String, TemporalPaginator> = Collections.synchronizedMap(HashMap())
    }
}
