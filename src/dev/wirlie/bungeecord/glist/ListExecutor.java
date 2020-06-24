package dev.wirlie.bungeecord.glist;

import com.google.common.collect.Iterators;
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

public class ListExecutor extends Command implements TabExecutor {

	private String globalListSpOptionMainFormat = DefaultValues.getDefaultString("formats.global-list.server-sp-option.main-format");
	private String globalListSpOptionPlayersFormat = DefaultValues.getDefaultString("formats.global-list.server-sp-option.players-format");
	private String globalListFullFormat = DefaultValues.getDefaultString("formats.global-list.full-message-format");
	private String globalListServerRowFormat = DefaultValues.getDefaultString("formats.global-list.server-row-format");
	private String globalListNoServersFormat = DefaultValues.getDefaultString("formats.global-list.no-servers-format");
	private String globalListBackgroundGraphicColor = DefaultValues.getDefaultString("formats.global-list.graphic-background-color");
	private String globalListGraphicBarColor = DefaultValues.getDefaultString("formats.global-list.graphic-bar-color");
	private boolean globalListBehaviourHideEmptyServer = DefaultValues.getDefaultBoolean("behaviour.global-list.hide-empty-servers");
	private int globalListBehaviourMaxRows = DefaultValues.getDefaultInt("behaviour.global-list.max-servers-rows");
	private int globalListBehaviourMinPlayerToDisplay = DefaultValues.getDefaultInt("behaviour.global-list.min-player-count-to-display-server");
	private String serverListFullFormat = DefaultValues.getDefaultString("formats.server-list.full-message-format");
	private String serverListNoPageDataFormat = DefaultValues.getDefaultString("formats.server-list.no-page-data-message");
	private String serverListNoPlayersFormat = DefaultValues.getDefaultString("formats.server-list.no-players-message");
	private int serverListBehaviourPlayerPerPage = DefaultValues.getDefaultInt("behaviour.server-list.players-per-page");
	private List<String> blacklistedServers = DefaultValues.getDefaultStringList("behaviour.blacklisted-servers");
	private boolean globalListUpperCaseServerNames = false;
	private boolean serverListUpperCaseServerName = false;

	//Messages
	private String messagesServerPlayers = DefaultValues.getDefaultString("messages.server-players");
	private String messagesCannotFoundServer = DefaultValues.getDefaultString("messages.cannot-found-server");
	private String messagesPreviousPageHover = DefaultValues.getDefaultString("messages.previous-page-hover-message");
	private String messagesNextPageHover = DefaultValues.getDefaultString("messages.next-page-hover-message");
	private String messagesPreviousPage = DefaultValues.getDefaultString("messages.previous-page");
	private String messagesNextPage = DefaultValues.getDefaultString("messages.next-page");
	private String messagesNoPreviousPage = DefaultValues.getDefaultString("messages.no-previous-page");
	private String messagesNoNextPage = DefaultValues.getDefaultString("messages.no-next-page");

	private NumberFormat format = NumberFormat.getNumberInstance();
	private EnhancedBCL plugin;
	private Map<String, TemporalPaginator<String>> serversPaginators = new HashMap<>();

	ListExecutor(EnhancedBCL plugin, String name, String permission, String... aliases) {
		super(name, permission, aliases);
		this.plugin = plugin;
		this.format.setMaximumFractionDigits(2);
		this.loadFromConfig();
	}

	private void loadFromConfig() {
		this.globalListServerRowFormat = this.plugin.getConfig().getString("formats.global-list.server-row-format", DefaultValues.getDefaultString("formats.global-list.server-row-format"));
		List<String> fullMessageLines = this.plugin.getConfig().getStringList("formats.global-list.full-message-format");
		if (!fullMessageLines.isEmpty()) {
			StringBuilder messageBuilder = new StringBuilder();

			for (int i = 0; i < fullMessageLines.size(); i++) {
				String line = fullMessageLines.get(i);
				messageBuilder.append(line);
				if(i < (fullMessageLines.size() - 1)){
					messageBuilder.append("\n");
				}
			}

			this.globalListFullFormat = messageBuilder.toString();
		} else {
			this.globalListFullFormat = DefaultValues.getDefaultString("formats.global-list.full-message-format");
		}

		this.globalListSpOptionMainFormat = this.plugin.getConfig().getString("formats.global-list.server-sp-option.main-format", DefaultValues.getDefaultString("formats.global-list.server-sp-option.main-format"));
		this.globalListSpOptionPlayersFormat = this.plugin.getConfig().getString("formats.global-list.server-sp-option.players-format", DefaultValues.getDefaultString("formats.global-list.server-sp-option.players-format"));
		this.globalListNoServersFormat = this.plugin.getConfig().getString("formats.global-list.no-servers-format", DefaultValues.getDefaultString("formats.global-list.no-servers-format"));
		this.globalListBackgroundGraphicColor = this.plugin.getConfig().getString("formats.global-list.graphic-background-color", DefaultValues.getDefaultString("formats.global-list.graphic-background-color"));
		this.globalListGraphicBarColor = this.plugin.getConfig().getString("formats.global-list.graphic-bar-color", DefaultValues.getDefaultString("formats.global-list.graphic-bar-color"));
		this.globalListBehaviourHideEmptyServer = this.plugin.getConfig().getBoolean("behaviour.global-list.hide-empty-servers", DefaultValues.getDefaultBoolean("behaviour.global-list.hide-empty-servers"));
		this.globalListBehaviourMaxRows = this.plugin.getConfig().getInt("behaviour.global-list.max-servers-rows", DefaultValues.getDefaultInt("behaviour.global-list.max-servers-rows"));
		this.globalListBehaviourMinPlayerToDisplay = this.plugin.getConfig().getInt("behaviour.global-list.min-player-count-to-display-server", DefaultValues.getDefaultInt("behaviour.global-list.min-player-count-to-display-server"));
		this.blacklistedServers =  this.plugin.getConfig().getStringList("behaviour.blacklisted-servers");

		List<String> serverListFullMessage = this.plugin.getConfig().getStringList("formats.server-list.full-message-format");
		if (!serverListFullMessage.isEmpty()) {
			StringBuilder messageBuilder = new StringBuilder();

			for (int i = 0; i < serverListFullMessage.size(); i++) {
				String line = serverListFullMessage.get(i);
				messageBuilder.append(line);
				if(i < (serverListFullMessage.size() - 1)){
					messageBuilder.append("\n");
				}
			}

			this.serverListFullFormat = messageBuilder.toString();
		} else {
			this.serverListFullFormat = DefaultValues.getDefaultString("formats.server-list.full-message-format");
		}

		List<String> noPageDataMessage = this.plugin.getConfig().getStringList("formats.server-list.no-page-data-message");
		if (!noPageDataMessage.isEmpty()) {
			StringBuilder messageBuilder = new StringBuilder();

			for (int i = 0; i < noPageDataMessage.size(); i++) {
				String line = noPageDataMessage.get(i);
				messageBuilder.append(line);
				if(i < (noPageDataMessage.size() - 1)){
					messageBuilder.append("\n");
				}
			}

			this.serverListNoPageDataFormat = messageBuilder.toString();
			if(serverListNoPageDataFormat.endsWith("\n")) {
				serverListNoPageDataFormat = serverListNoPageDataFormat.substring(0, serverListNoPageDataFormat.length() - 2);
			}
		} else {
			this.serverListNoPageDataFormat = DefaultValues.getDefaultString("formats.server-list.no-page-data-message");
		}

		List<String> noPlayersMessage = this.plugin.getConfig().getStringList("formats.server-list.no-players-message");
		if (!noPlayersMessage.isEmpty()) {
			StringBuilder messageBuilder = new StringBuilder();

			for (int i = 0; i < noPlayersMessage.size(); i++) {
				String line = noPlayersMessage.get(i);
				messageBuilder.append(line);
				if(i < (noPlayersMessage.size() - 1)){
					messageBuilder.append("\n");
				}
			}

			this.serverListNoPlayersFormat = messageBuilder.toString();
			if(serverListNoPlayersFormat.endsWith("\n")) {
				serverListNoPlayersFormat = serverListNoPlayersFormat.substring(0, serverListNoPlayersFormat.length() - 2);
			}
		} else {
			this.serverListNoPlayersFormat = DefaultValues.getDefaultString("formats.server-list.no-players-message");
		}

		this.serverListBehaviourPlayerPerPage = this.plugin.getConfig().getInt("behaviour.server-list.players-per-page", DefaultValues.getDefaultInt("behaviour.server-list.players-per-page"));

		this.globalListUpperCaseServerNames = plugin.getConfig().getBoolean("behaviour.global-list-uppercase-server-names");
		this.serverListUpperCaseServerName = plugin.getConfig().getBoolean("behaviour.server-list-uppercase-server-name");

		this.messagesServerPlayers = plugin.getConfig().getString("messages.server-players", DefaultValues.getDefaultString("messages.server-players"));
		this.messagesCannotFoundServer = plugin.getConfig().getString("messages.cannot-found-server", DefaultValues.getDefaultString("messages.cannot-found-server"));
		this.messagesPreviousPageHover = plugin.getConfig().getString("messages.previous-page-hover-message", DefaultValues.getDefaultString("messages.previous-page-hover-message"));
		this.messagesNextPageHover = plugin.getConfig().getString("messages.next-page-hover-message", DefaultValues.getDefaultString("messages.next-page-hover-message"));
		this.messagesPreviousPage = plugin.getConfig().getString("messages.previous-page" ,DefaultValues.getDefaultString("messages.previous-page"));
		this.messagesNextPage = plugin.getConfig().getString("messages.next-page", DefaultValues.getDefaultString("messages.next-page"));
		this.messagesNoPreviousPage = plugin.getConfig().getString("messages.no-previous-page", DefaultValues.getDefaultString("messages.no-previous-page"));
		this.messagesNoNextPage = plugin.getConfig().getString("messages.no-next-page", DefaultValues.getDefaultString("messages.no-next-page"));
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
						if(blacklistedServers.contains(o.getName())) {
							return false;
						}

						if(options.contains("-a")) {
							if(o.getPlayers().size() > 0) {
								return true;
							}
						}

						if (options.contains("-g")) {
							return true;
						} else if (this.globalListBehaviourMinPlayerToDisplay >= 1) {
							return o.getPlayers().size() >= this.globalListBehaviourMinPlayerToDisplay;
						} else {
							return !o.getPlayers().isEmpty() || o.getPlayers().isEmpty() && !this.globalListBehaviourHideEmptyServer;
						}
					})
					.limit(this.globalListBehaviourMaxRows < 1 ? Integer.MAX_VALUE : options.contains("-g") ? Integer.MAX_VALUE : this.globalListBehaviourMaxRows).collect(Collectors.toList());
			StringBuilder rowsBuilder = new StringBuilder();
			int totalPlayers = BungeeCord.getInstance().getPlayers().size();
			if (servers.isEmpty()) {
				rowsBuilder.append(this.globalListNoServersFormat);
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
									graphicBarBuilder.append(this.globalListGraphicBarColor).append("|");
								} else {
									graphicBarBuilder.append(this.globalListBackgroundGraphicColor).append("|");
								}
							}

							rowsBuilder.append(this.globalListServerRowFormat.replace("{SERVER_NAME}", globalListUpperCaseServerNames ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYER_AMOUNT}", String.valueOf(serverInfo.getPlayers().size())).replace("{GRAPHIC_BAR}", graphicBarBuilder.toString()).replace("{PERCENT}", this.format.format(percent) + "%")).append("\n");
						} while(!options.contains("-sp"));
					} while(serverInfo.getPlayers().isEmpty());

					String mainFormat = globalListSpOptionMainFormat;
					String playersFormat = globalListSpOptionPlayersFormat;

					StringBuilder playersString = new StringBuilder();
					List<ProxiedPlayer> players = new ArrayList<>(serverInfo.getPlayers());
					for(int i = 0; i < players.size(); i++) {
						ProxiedPlayer player = players.get(i);

						if(i == players.size() - 1) {
							//eliminar formato
							int indexOf = playersFormat.indexOf("{PLAYER_NAME}");
							if(indexOf != -1) {
								playersString.append(playersFormat.substring(0, indexOf + "{PLAYER_NAME}".length()).replace("{PLAYER_NAME}", player.getName()));
							} else {
								playersString.append(playersFormat.replace("{PLAYER_NAME}", player.getName()));
							}
						} else {
							playersString.append(playersFormat.replace("{PLAYER_NAME}", player.getName()));
						}
					}

					rowsBuilder.append(mainFormat.replace("{SERVER_NAME}", globalListUpperCaseServerNames ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYERS_FORMAT}", playersString.toString())).append("\n");

					/*rowsBuilder.append(" &e").append(messagesServerPlayers.replace("{SERVER_NAME}", globalListUpperCaseServerNames ? serverInfo.getName().toUpperCase() : serverInfo.getName())).append(": &8[&7");

					for (ProxiedPlayer player : serverInfo.getPlayers()) {
						rowsBuilder.append(player.getName()).append("&8,&7 ");
					}

					rowsBuilder.append("&8]\n");*/
				}
			}

			page = BungeeCord.getInstance().getServers().size() - servers.size();
			String fullMessageCopy = this.globalListFullFormat;
			String message = ChatColor.translateAlternateColorCodes('&', fullMessageCopy.replace("{SERVERS_ROWS}", rowsBuilder.toString()).replace("{NOT_DISPLAYED_AMOUNT}", String.valueOf(page)).replace("{TOTAL_PLAYER_AMOUNT}", String.valueOf(totalPlayers)).replace("{LABEL}", this.getName()));

			if(message.endsWith("\n")) {
				message = message.substring(message.length() - 2);
			}

			String[] lines = message.split("\\n");
			partsController = lines;

			for(int totalBars = 0; totalBars < lines.length; ++totalBars) {
				String line = partsController[totalBars];
				sender.sendMessage(TextUtil.fromLegacy(line));
			}
		} else {
			String serverName = args[0];
			ServerInfo serverInfo = BungeeCord.getInstance().getServerInfo(serverName);
			if (serverInfo == null) {
				sender.sendMessage(TextUtil.fromLegacy(messagesCannotFoundServer.replace("{NAME}", serverName)));
			} else {
				TemporalPaginator<String> temporalPaginator = this.serversPaginators.computeIfAbsent(serverInfo.getName(), (k) -> new TemporalPaginator<>(serverInfo.getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList()), this.serverListBehaviourPlayerPerPage));

				if (temporalPaginator.shouldUpdate(60000L)) {
					temporalPaginator.update(serverInfo.getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList()));
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
						sender.sendMessage(TextUtil.fromLegacy(this.serverListNoPageDataFormat.replace("{TOTAL_PAGES}", String.valueOf(temporalPaginator.getTotalPages()))));
					} else {
						sender.sendMessage(TextUtil.fromLegacy(this.serverListNoPlayersFormat));
					}
				} else {
					String[] namesData = pageData.toArray(new String[0]);
					String message = this.serverListFullFormat.replace("{PLAYERS_ROWS}", TextUtil.makeRows(2, 25, (page - 1) * this.serverListBehaviourPlayerPerPage + 1, ChatColor.GRAY, namesData)).replace("{SERVER_NAME}", serverListUpperCaseServerName ? serverInfo.getName().toUpperCase() : serverInfo.getName()).replace("{PLAYERS_COUNT}", String.valueOf(temporalPaginator.dataSize())).replace("{PAGE}", options.contains("-g") ? "All Pages" : String.valueOf(page)).replace("{TOTAL_PAGES}", String.valueOf(temporalPaginator.getTotalPages()));
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
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesPreviousPageHover.replace("{PAGE_NUMBER}", String.valueOf(page - 1)) )))
									.append(" " + messagesPreviousPage + " ")
										.color(ChatColor.GOLD)
									.append("|", FormatRetention.NONE)
										.color(ChatColor.DARK_GRAY)
									.append(" " + messagesNextPage + " ")
										.color(ChatColor.GOLD)
										.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1)))
										.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNextPageHover.replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
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
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNoPreviousPage)))
										.append(" " + messagesPreviousPage + " ")
											.color(ChatColor.RED)
										.append("|", FormatRetention.NONE)
											.color(ChatColor.DARK_GRAY)
										.append(" " + messagesNextPage + " ")
											.color(ChatColor.GOLD)
											.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page + 1)))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNextPageHover.replace("{PAGE_NUMBER}", String.valueOf(page + 1)))))
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
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNoPreviousPage)))
										.append(" " + messagesPreviousPage + " ")
											.color(ChatColor.RED)
										.append("|", FormatRetention.NONE)
											.color(ChatColor.DARK_GRAY)
										.append(" " + messagesNextPage + " ")
											.color(ChatColor.RED)
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNoNextPage)))
										.append(">>")
											.color(ChatColor.DARK_RED);
							}
						} else if (page >= temporalPaginator.getTotalPages()) {
							if (isPlayerExecutor) {
								cb = new ComponentBuilder("<<")
											.color(ChatColor.WHITE)
											.event(new ClickEvent(Action.RUN_COMMAND, "/" + this.getName() + " " + serverInfo.getName() + " " + (page - 1)))
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesPreviousPageHover.replace("{PAGE_NUMBER}", String.valueOf(page - 1)))))
										.append(" " + messagesPreviousPage + " ")
											.color(ChatColor.GOLD)
										.append("|", FormatRetention.NONE)
											.color(ChatColor.DARK_GRAY)
										.append(" " + messagesNextPage + " ")
											.color(ChatColor.RED)
											.event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, TextUtil.fromLegacy(messagesNoNextPage)))
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