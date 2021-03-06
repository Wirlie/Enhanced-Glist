package dev.wirlie.bungeecord.glist.executor;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.TemporalPaginator;
import dev.wirlie.bungeecord.glist.activity.ActivityType;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.servers.BungeecordInfoProvider;
import dev.wirlie.bungeecord.glist.servers.ServerGroup;
import dev.wirlie.bungeecord.glist.servers.ServerInfoProvider;
import dev.wirlie.bungeecord.glist.util.Pair;
import dev.wirlie.bungeecord.glist.util.PlayerGlistEntry;
import dev.wirlie.bungeecord.glist.util.TextUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GlistCommand extends Command implements TabExecutor {

	private final NumberFormat format = NumberFormat.getNumberInstance();
	public static final Map<String, TemporalPaginator> serversPaginators = Collections.synchronizedMap(new HashMap<>());
	private final EnhancedBCL plugin;

	public GlistCommand(EnhancedBCL plugin, String name, String permission, String... aliases) {
		super(name, permission, aliases);
		this.plugin = plugin;
		this.format.setMaximumFractionDigits(2);
	}

	public void reload() {
		synchronized (GlistCommand.serversPaginators) {
			serversPaginators.clear();
		}
	}

	public void execute(CommandSender sender, String[] args) {
		boolean isPlayerExecutor = sender instanceof ProxiedPlayer;
		Set<String> options = new HashSet<>();

		for(String arg : args) {
			String[] backup;
			boolean alreadySkipped;
			int i;
			String b;
			if (arg.equalsIgnoreCase("-g")) {
				options.add("-g");
				backup = args;
				args = new String[args.length - 1];
				alreadySkipped = false;
				i = 0;

				for(int j = 0; i < args.length; ++j) {
					b = backup[i];
					if (b.equalsIgnoreCase("-g") && !alreadySkipped) {
						alreadySkipped = true;
						--j;
					} else {
						args[j] = b;
					}

					++i;
				}
			} else if (arg.equalsIgnoreCase("-sp")) {
				options.add("-sp");
				backup = args;
				args = new String[args.length - 1];
				alreadySkipped = false;
				i = 0;

				for(int j = 0; i < args.length; ++j) {
					b = backup[i];
					if (b.equalsIgnoreCase("-sp") && !alreadySkipped) {
						alreadySkipped = true;
						--j;
					} else {
						args[j] = b;
					}

					++i;
				}
			} else if (arg.equalsIgnoreCase("-a")) {
				options.add("-a");
				backup = args;
				args = new String[args.length - 1];
				alreadySkipped = false;
				i = 0;

				for(int j = 0; i < args.length; ++j) {
					b = backup[i];
					if (b.equalsIgnoreCase("-a") && !alreadySkipped) {
						alreadySkipped = true;
						--j;
					} else {
						args[j] = b;
					}

					++i;
				}
			}
		}

		if (args.length == 0) {
			printGlobal(sender, options);
		} else {
			int page;
			String[] partsController;
			String serverName = args[0];

			//is blacklisted?
			if(Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get().stream().anyMatch(s -> s.equalsIgnoreCase(serverName))) {
				sender.sendMessage(TextUtil.fromLegacy(Config.MESSAGES__CANNOT_FOUND_SERVER.get().replace("{NAME}", serverName)));
				return;
			}

			ServerInfoProvider serverInfoPre = null;

			//try with group first
			serverInfoPre = plugin.getServerGroups().stream().filter(g -> g.getId().equalsIgnoreCase(serverName)).findFirst().orElse(null);

			if(serverInfoPre == null) {
				//try with bungeecord
				ServerInfo bungeeServer = BungeeCord.getInstance().getServerInfo(serverName);
				if(bungeeServer != null) {
					serverInfoPre = new BungeecordInfoProvider(bungeeServer);
				}
			}

			ServerInfoProvider serverInfo = serverInfoPre;

			if (serverInfo == null) {
				sender.sendMessage(TextUtil.fromLegacy(Config.MESSAGES__CANNOT_FOUND_SERVER.get().replace("{NAME}", serverName)));
			} else {
				TemporalPaginator temporalPaginator = this.serversPaginators.computeIfAbsent(serverInfo.getId(), (k) -> new TemporalPaginator(serverInfo.getPlayers().stream().map(cs -> {
					String prefix = plugin.getPrefix(cs);

					if(prefix == null || prefix.trim().equalsIgnoreCase("null")) {
						prefix = "";
					}

					return new PlayerGlistEntry(cs, prefix, plugin.getActivityManager().getActivities(cs.getUniqueId()));
				}).collect(Collectors.toList()), Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get()));

				if (temporalPaginator.shouldUpdate(Config.BEHAVIOUR__CACHE_TIME__PLAYER_LIST_PAGES.get() * 1000L)) {
					temporalPaginator.update(serverInfo.getPlayers().stream().map(cs -> {
						String prefix = plugin.getPrefix(cs);

						if(prefix == null || prefix.trim().equalsIgnoreCase("null")) {
							prefix = "";
						}

						return new PlayerGlistEntry(cs, prefix, plugin.getActivityManager().getActivities(cs.getUniqueId()));
					}).collect(Collectors.toList()));
				}

				page = 1;
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException ignored) {

					}
				}

				List<PlayerGlistEntry> pageData = options.contains("-g") ? (isPlayerExecutor ? temporalPaginator.getFullDataVisible((ProxiedPlayer) sender) : temporalPaginator.getFullData() ) : (isPlayerExecutor ? temporalPaginator.getVisiblePage(page, (ProxiedPlayer) sender) : temporalPaginator.getFullPage(page));

				if (pageData.isEmpty()) {
					List<PlayerGlistEntry> data = isPlayerExecutor ? temporalPaginator.getFullDataVisible((ProxiedPlayer) sender) : temporalPaginator.getFullData();
					int totalPages = temporalPaginator.resolveNumOfPages(data);
					if (totalPages > 0) {
						for (String line : Config.FORMATS__SERVER_LIST__NO_PAGE_DATA_MESSAGE.get()) {
							sender.sendMessage(TextUtil.fromLegacy(line.replace("{TOTAL_PAGES}", String.valueOf(totalPages))));
						}
					} else {
						for(String line : Config.FORMATS__SERVER_LIST__NO_PLAYERS_MESSAGE.get()) {
							sender.sendMessage(TextUtil.fromLegacy(line));
						}
					}
				} else {
					List<PlayerGlistEntry> data = isPlayerExecutor ? temporalPaginator.getFullDataVisible((ProxiedPlayer) sender) : temporalPaginator.getFullData();
					int totalPages = temporalPaginator.resolveNumOfPages(data);
					String message = String.join("\n", new ArrayList<>(Config.FORMATS__SERVER_LIST__FULL_MESSAGE_FORMAT.get())).replace("{PLAYERS_ROWS}", TextUtil.makeRowsNew(Config.FORMATS__SERVER_LIST__PLAYERS_PER_ROW.get(), (page - 1) * Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get() + 1, pageData)).replace("{SERVER_NAME}", Config.BEHAVIOUR__SERVER_LIST__UPPER_CASE_NAME.get() ? serverInfo.getDisplayName().toUpperCase() : serverInfo.getDisplayName()).replace("{PLAYERS_COUNT}", String.valueOf(data.size())).replace("{PAGE}", options.contains("-g") ? Config.MESSAGES__ALL_PAGES.get() : String.valueOf(page)).replace("{TOTAL_PAGES}", String.valueOf(totalPages));

					if (options.contains("-g")) {
						partsController = message.split("\\n");

						for (String line : partsController) {
							if (!line.contains("{PAGINATION_CONTROLLER}")) {
								sender.sendMessage(TextUtil.fromLegacy(line));
							}
						}
					} else if (message.contains("{PAGINATION_CONTROLLER}")) {
						partsController = message.split("\\{PAGINATION_CONTROLLER}");
						BaseComponent mainComponent = new TextComponent();

						for (BaseComponent bc : TextUtil.fromLegacy(partsController[0])) {
							mainComponent.addExtra(bc);
						}

						ComponentBuilder cb = null;
						if (page > 1 && page < totalPages) {
							cb = new ComponentBuilder("");

							if (isPlayerExecutor) {
								cb.append("<<")
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
										.append(">>").color(ChatColor.WHITE);
							} else {
								cb.append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1)).color(ChatColor.WHITE).append(" to go to the previous page.\n").color(ChatColor.GOLD).append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getId() + " " + (page + 1)).color(ChatColor.WHITE).append(" to go to the next page.").color(ChatColor.GOLD);
							}
						} else if (page <= 1) {
							if (page + 1 <= totalPages) {
								cb = new ComponentBuilder("");
								if (isPlayerExecutor) {
									cb.append("<<")
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
											.append(">>").color(ChatColor.WHITE);
								} else {
									cb.append("Use ").color(ChatColor.GOLD)
											.append("/" + this.getName() + " " + serverInfo.getId() + " " + (page + 1))
											.color(ChatColor.WHITE)
											.append(" to go to the next page.")
											.color(ChatColor.GOLD);
								}
							} else if (isPlayerExecutor) {
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
										.color(ChatColor.DARK_RED);
							}
						} else if (page >= totalPages) {
							if (isPlayerExecutor) {
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
										.color(ChatColor.DARK_RED);
							} else {
								cb = new ComponentBuilder("Use ")
										.color(ChatColor.GOLD)
										.append("/" + this.getName() + " " + serverInfo.getId() + " " + (page - 1))
										.color(ChatColor.WHITE)
										.append(" to go to the previous page.")
										.color(ChatColor.GOLD);
							}
						}

						BaseComponent bc;
						BaseComponent[] bcObject;
						int j;
						if (cb != null) {
							bcObject = cb.create();

							for(j = 0; j < bcObject.length; ++j) {
								bc = bcObject[j];
								mainComponent.addExtra(bc);
							}
						}

						if (partsController.length > 1) {
							bcObject = TextUtil.fromLegacy(partsController[1]);

							for(j = 0; j < bcObject.length; ++j) {
								bc = bcObject[j];
								mainComponent.addExtra(bc);
							}
						}

						sender.sendMessage(mainComponent);
					} else {
						if(message.endsWith("\n")) {
							message = message.substring(0, message.length() - 2);
						}

						sender.sendMessage(TextUtil.fromLegacy(message));
					}
				}
			}
		}
	}

	private void printGlobal(CommandSender sender, Set<String> options) {
		List<ServerInfoProvider> allServers = new ArrayList<>();

		//bungeecord servers
		for(ServerInfo sv : BungeeCord.getInstance().getServers().values()) {
			if(!plugin.isInGroup(sv)) {
				allServers.add(new BungeecordInfoProvider(sv));
			}
		}

		//groups servers
		allServers.addAll(plugin.getServerGroups());

		List<ServerInfoProvider> servers = allServers.stream()
		   .sorted((o,o1) -> Integer.compare(o1.getPlayerCount(), o.getPlayerCount()))
		   .filter((o) -> {
			   if(Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get().contains(o.getId())) {
				   return false;
			   }

			   if(options.contains("-a")) {
				   if(o.getPlayerCount() > 0) {
					   return true;
				   }
			   }

			   if (options.contains("-g")) {
				   return true;
			   } else if (Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get() >= 1) {
				   return o.getPlayerCount() >= Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get();
			   } else {
				   return o.getPlayerCount() > 0 || o.getPlayerCount() == 0 && !Config.BEHAVIOUR__GLOBAL_LIST__HIDE_EMPTY_SERVERS.get();
			   }
		   })
		   .limit(Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get() < 1 ? Integer.MAX_VALUE : options.contains("-g") ? Integer.MAX_VALUE : Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get()).collect(Collectors.toList());

		ComponentBuilder rowsBuilder = new ComponentBuilder("");

		int totalPlayers = BungeeCord.getInstance().getPlayers().size();
		if (servers.isEmpty()) {
			rowsBuilder.append(TextUtil.fromLegacy(Config.FORMATS__GLOBAL_LIST__NO_SERVERS_FORMAT.get()));
		} else {
			int playerCount = (servers.get(0)).getPlayerCount();
			Iterator<ServerInfoProvider> serversIterator = servers.iterator();

			mainWhile:
			while(true) {
				ServerInfoProvider serverInfo;
				do {
					do {
						if (!serversIterator.hasNext()) {
							break mainWhile;
						}

						serverInfo = serversIterator.next();
						float percent = totalPlayers == 0 ? 0.0F : (serverInfo.getPlayerCount() * 100.0F / totalPlayers);
						float percentGraphic = playerCount == 0 ? 0.0F : (serverInfo.getPlayerCount() * 100F / playerCount);
						StringBuilder graphicBarBuilder = new StringBuilder();
						float barPercent = 5.0F;
						int totalBars = (int)(percentGraphic / barPercent);

						for(int i = 0; i < 20; ++i) {
							if (i < totalBars) {
								graphicBarBuilder.append(Config.FORMATS__GLOBAL_LIST__GRAPHIC_BAR_COLOR.get()).append("|");
							} else {
								graphicBarBuilder.append(Config.FORMATS__GLOBAL_LIST__GRAPHIC_BACKGROUND_COLOR.get()).append("|");
							}
						}

						rowsBuilder
						.event(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append(TextUtil.fromLegacy(Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get().replace("{SERVER_NAME}", serverInfo.getId()))).create())
						)
						.event(
							new ClickEvent(Action.RUN_COMMAND, "/glist " + serverInfo.getId())
						)
						.append(
							TextUtil.fromLegacy(Config.FORMATS__GLOBAL_LIST__SERVER_ROW_FORMAT.get().replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getId().toUpperCase() : serverInfo.getId()).replace("{PLAYER_AMOUNT}", String.valueOf(serverInfo.getPlayers().size())).replace("{GRAPHIC_BAR}", graphicBarBuilder.toString()).replace("{PERCENT}", this.format.format(percent) + "%"))
						)
						.append("\n", FormatRetention.NONE);
					} while(!options.contains("-sp"));
				} while(serverInfo.getPlayerCount() == 0);

				String mainFormat = Config.FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__MAIN_FORMAT.get();
				String playersFormat = Config.FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__PLAYERS_FORMAT.get();

				StringBuilder playersString = new StringBuilder();
				List<ProxiedPlayer> players = new ArrayList<>(serverInfo.getPlayers());
				for(int i = 0; i < players.size(); i++) {
					ProxiedPlayer player = players.get(i);

					if(i == players.size() - 1) {
						//eliminar formato
						int indexOf = playersFormat.indexOf("{PLAYER_NAME}");
						if(indexOf != -1) {
							String prefix = plugin.getPrefix(player);
							boolean isEmptyPrefix = prefix != null && ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix)).isEmpty();
							String playerName = (prefix == null ? "" : prefix + (isEmptyPrefix ? "" : " ")) +  player.getName();
							playersString.append(playersFormat.substring(0, indexOf + "{PLAYER_NAME}".length()).replace("{PLAYER_NAME}", playerName));
						} else {
							String prefix = plugin.getPrefix(player);
							boolean isEmptyPrefix = prefix != null && ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix)).isEmpty();
							String playerName = (prefix == null ? "" : prefix + (isEmptyPrefix ? "" : " ")) +  player.getName();
							playersString.append(playersFormat.replace("{PLAYER_NAME}", playerName));
						}
					} else {
						String prefix = plugin.getPrefix(player);
						boolean isEmptyPrefix = prefix != null && ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix)).isEmpty();
						String playerName = (prefix == null ? "" : prefix + (isEmptyPrefix ? "" : " ")) +  player.getName();
						playersString.append(playersFormat.replace("{PLAYER_NAME}", playerName));
					}
				}

				rowsBuilder
				.event(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").append(TextUtil.fromLegacy(Config.MESSAGES__CLICK_TO_SHOW_PLAYERS.get().replace("{SERVER_NAME}", serverInfo.getId()))).create())
				)
				.event(
					new ClickEvent(Action.RUN_COMMAND, "/glist " + serverInfo.getId())
				)
				.append(
					TextUtil.fromLegacy(mainFormat.replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getId().toUpperCase() : serverInfo.getId()).replace("{PLAYERS_FORMAT}", playersString.toString()))
				).append("\n", FormatRetention.NONE);
			}
		}

		int notDisplayedCount = BungeeCord.getInstance().getServers().size() - servers.size();
		List<String> fullMessageCopy = new ArrayList<>(Config.FORMATS__GLOBAL_LIST__FULL_MESSAGE_FORMAT.get());

		for(String line : fullMessageCopy) {
			if (line.contains("{SERVERS_ROWS}")) {
				sender.sendMessage(rowsBuilder.create());
			} else {
				sender.sendMessage(TextUtil.fromLegacy(ChatColor.translateAlternateColorCodes('&', line.replace("{NOT_DISPLAYED_AMOUNT}", String.valueOf(notDisplayedCount)).replace("{TOTAL_PLAYER_AMOUNT}", String.valueOf(totalPlayers)).replace("{LABEL}", this.getName()))));
			}
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

		if(args.length == 1) {
			List<String> blackListedServers = Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get();
			Set<String> suggestions = BungeeCord.getInstance().getServers().values().stream().filter(s -> {
				//hide blacklisted
				if(blackListedServers.stream().anyMatch(ss -> ss.equalsIgnoreCase(s.getName()))) {
					return false;
				}

				return s.getName().toLowerCase().contains(args[0].toLowerCase());
			}).map(s -> s.getName().toLowerCase()).collect(Collectors.toSet());

			//add groups as suggestions
			List<String> groupSuggestions = plugin.getServerGroups().stream().map(s -> s.getId().toLowerCase()).collect(Collectors.toList());
			//remove blacklisted
			groupSuggestions.removeIf(g -> blackListedServers.stream().anyMatch(ss -> ss.equalsIgnoreCase(g)));

			suggestions.addAll(groupSuggestions);

			List<String> sortedSuggestions = new ArrayList<>(suggestions);
			sortedSuggestions.sort(Collator.getInstance(Locale.US));

			if(args[0].isEmpty() || args[0].startsWith("-")) {
				sortedSuggestions.add(0, "-g");
				sortedSuggestions.add(0, "-sp");
				sortedSuggestions.add(0, "-a");
			}

			return sortedSuggestions;
		}

		return Collections.emptyList();
	}

}
