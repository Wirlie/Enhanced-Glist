package dev.wirlie.bungeecord.glist.executor;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.TemporalPaginator;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.hooks.GroupHook;
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

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GlistCommand extends Command implements TabExecutor {

	private final NumberFormat format = NumberFormat.getNumberInstance();
	private final Map<String, TemporalPaginator<String>> serversPaginators = new HashMap<>();
	private final EnhancedBCL plugin;

	public GlistCommand(EnhancedBCL plugin, String name, String permission, String... aliases) {
		super(name, permission, aliases);
		this.plugin = plugin;
		this.format.setMaximumFractionDigits(2);
	}

	public void reload() {
		serversPaginators.clear();
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

		int page;
		String[] partsController;
		if (args.length == 0) {
			List<ServerInfo> servers = BungeeCord.getInstance()
					.getServers()
					.values()
					.stream()
					.sorted((o, o1) -> Integer.compare(o1.getPlayers().size(), o.getPlayers().size()))
					.filter((o) -> {
						if(Config.BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS.get().contains(o.getName())) {
							return false;
						}

						if(options.contains("-a")) {
							if(o.getPlayers().size() > 0) {
								return true;
							}
						}

						if (options.contains("-g")) {
							return true;
						} else if (Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get() >= 1) {
							return o.getPlayers().size() >= Config.BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER.get();
						} else {
							return !o.getPlayers().isEmpty() || o.getPlayers().isEmpty() && !Config.BEHAVIOUR__GLOBAL_LIST__HIDE_EMPTY_SERVERS.get();
						}
					})
					.limit(Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get() < 1 ? Integer.MAX_VALUE : options.contains("-g") ? Integer.MAX_VALUE : Config.BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS.get()).collect(Collectors.toList());
			StringBuilder rowsBuilder = new StringBuilder();
			int totalPlayers = BungeeCord.getInstance().getPlayers().size();
			if (servers.isEmpty()) {
				rowsBuilder.append(Config.FORMATS__GLOBAL_LIST__NO_SERVERS_FORMAT.get());
			} else {
				page = (servers.get(0)).getPlayers().size();
				Iterator<ServerInfo> serversIterator = servers.iterator();

				mainWhile:
				while(true) {
					ServerInfo serverInfo;
					do {
						do {
							if (!serversIterator.hasNext()) {
								break mainWhile;
							}

							serverInfo = serversIterator.next();
							float percent = totalPlayers == 0 ? 0.0F : (serverInfo.getPlayers().size() * 100.0F / totalPlayers);
							float percentGraphic = page == 0 ? 0.0F : (serverInfo.getPlayers().size() * 100F / page);
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

							rowsBuilder.append(Config.FORMATS__GLOBAL_LIST__SERVER_ROW_FORMAT.get().replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYER_AMOUNT}", String.valueOf(serverInfo.getPlayers().size())).replace("{GRAPHIC_BAR}", graphicBarBuilder.toString()).replace("{PERCENT}", this.format.format(percent) + "%")).append("\n");
						} while(!options.contains("-sp"));
					} while(serverInfo.getPlayers().isEmpty());

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

					rowsBuilder.append(mainFormat.replace("{SERVER_NAME}", Config.BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES.get() ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYERS_FORMAT}", playersString.toString())).append("\n");
				}
			}

			page = BungeeCord.getInstance().getServers().size() - servers.size();
			List<String> fullMessageCopy = new ArrayList<>(Config.FORMATS__GLOBAL_LIST__FULL_MESSAGE_FORMAT.get());

			for(String line : fullMessageCopy) {
				sender.sendMessage(TextUtil.fromLegacy(ChatColor.translateAlternateColorCodes('&', line.replace("{SERVERS_ROWS}", rowsBuilder.toString()).replace("{NOT_DISPLAYED_AMOUNT}", String.valueOf(page)).replace("{TOTAL_PLAYER_AMOUNT}", String.valueOf(totalPlayers)).replace("{LABEL}", this.getName()))));
			}
		} else {
			String serverName = args[0];
			ServerInfo serverInfo = BungeeCord.getInstance().getServerInfo(serverName);
			if (serverInfo == null) {
				sender.sendMessage(TextUtil.fromLegacy(Config.MESSAGES__CANNOT_FOUND_SERVER.get().replace("{NAME}", serverName)));
			} else {
				TemporalPaginator<String> temporalPaginator = this.serversPaginators.computeIfAbsent(serverInfo.getName(), (k) -> new TemporalPaginator<>(serverInfo.getPlayers().stream().map(cs -> {
					String prefix = plugin.getPrefix(cs);
					if(prefix != null) {
						if(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix)).isEmpty()) {
							return prefix + cs.getName();
						} else {
							return prefix + " " + cs.getName();
						}
					} else {
						return cs.getName();
					}
				}).collect(Collectors.toList()), Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get()));

				if (temporalPaginator.shouldUpdate(60000L)) {
					temporalPaginator.update(serverInfo.getPlayers().stream().map(cs -> {
						String prefix = plugin.getPrefix(cs);
						if(prefix != null) {
							if(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', prefix)).isEmpty()) {
								return prefix + cs.getName();
							} else {
								return prefix + " " + cs.getName();
							}
						} else {
							return cs.getName();
						}
					}).collect(Collectors.toList()));
				}

				page = 1;
				if (args.length > 1) {
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException ignored) {

					}
				}

				List<String> pageData = options.contains("-g") ? temporalPaginator.getFullData() : temporalPaginator.getPage(page);
				if (pageData.isEmpty()) {
					if (temporalPaginator.getTotalPages() > 0) {
						for (String line : Config.FORMATS__SERVER_LIST__NO_PAGE_DATA_MESSAGE.get()) {
							sender.sendMessage(TextUtil.fromLegacy(line.replace("{TOTAL_PAGES}", String.valueOf(temporalPaginator.getTotalPages()))));
						}
					}else {
						for(String line : Config.FORMATS__SERVER_LIST__NO_PLAYERS_MESSAGE.get()) {
							sender.sendMessage(TextUtil.fromLegacy(line));
						}
					}
				} else {
					String[] namesData = pageData.toArray(new String[0]);
					String message = String.join("\n", new ArrayList<>(Config.FORMATS__SERVER_LIST__FULL_MESSAGE_FORMAT.get())).replace("{PLAYERS_ROWS}", TextUtil.makeRows(2, 10, (page - 1) * Config.BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE.get() + 1, ChatColor.GRAY, namesData)).replace("{SERVER_NAME}", Config.BEHAVIOUR__SERVER_LIST__UPPER_CASE_NAME.get() ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYERS_COUNT}", String.valueOf(temporalPaginator.dataSize())).replace("{PAGE}", options.contains("-g") ? Config.MESSAGES__ALL_PAGES.get() : String.valueOf(page)).replace("{TOTAL_PAGES}", String.valueOf(temporalPaginator.getTotalPages()));

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
						if (page > 1 && page < temporalPaginator.getTotalPages()) {
							cb = new ComponentBuilder("");

							if (isPlayerExecutor) {
								cb.append("<<")
										.color(ChatColor.WHITE)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page - 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page - 1)) )))
										.append(" " + Config.MESSAGES__PREVIOUS_PAGE.get() + " ")
										.color(ChatColor.GOLD)
										.append("|", FormatRetention.NONE)
										.color(ChatColor.DARK_GRAY)
										.append(" " + Config.MESSAGES__NEXT_PAGE.get() + " ")
										.color(ChatColor.GOLD)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
										.append(">>").color(ChatColor.WHITE);
							} else {
								cb.append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getName() + " " + (page - 1)).color(ChatColor.WHITE).append(" to go to the previous page.\n").color(ChatColor.GOLD).append("Use ").color(ChatColor.GOLD).append("/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1)).color(ChatColor.WHITE).append(" to go to the next page.").color(ChatColor.GOLD);
							}
						} else if (page <= 1) {
							if (page + 1 <= temporalPaginator.getTotalPages()) {
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
											.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1)))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(Config.MESSAGES__NEXT_PAGE_HOVER_MESSAGE.get().replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
											.append(">>").color(ChatColor.WHITE);
								} else {
									cb.append("Use ").color(ChatColor.GOLD)
											.append("/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1))
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
						} else if (page >= temporalPaginator.getTotalPages()) {
							if (isPlayerExecutor) {
								cb = new ComponentBuilder("<<")
										.color(ChatColor.WHITE)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page - 1)))
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
										.append("/" + this.getName() + " " + serverInfo.getName() + " " + (page - 1))
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

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

		if(args.length == 1) {
			List<String> suggestions = BungeeCord.getInstance().getServers().values().stream().filter(s -> s.getName().toLowerCase().contains(args[0].toLowerCase())).map(ServerInfo::getName).collect(Collectors.toList());
			if(args[0].isEmpty() || args[0].startsWith("-")) {
				suggestions.add("-g");
				suggestions.add("-sp");
				suggestions.add("-a");
			}
			return suggestions;
		}

		return Collections.emptyList();
	}

}
