package dev.wirlie.bungeecord.glist;

import java.util.*;

public class DefaultValues {
	private static Map<String, Object> defaults = new HashMap<>();

	public DefaultValues() {
		super();
	}

	public static String getDefaultString(final String path) {
		return (String) DefaultValues.defaults.get(path);
	}

	public static int getDefaultInt(final String path) {
		return (int) DefaultValues.defaults.get(path);
	}

	public static List<String> getDefaultStringList(final String path) {
		//noinspection unchecked
		return (List<String>) DefaultValues.defaults.get(path);
	}

	public static boolean getDefaultBoolean(final String path) {
		return (boolean) DefaultValues.defaults.get(path);
	}

	static {
		DefaultValues.defaults.put("command.global-list.label", "glist");
		DefaultValues.defaults.put("command.global-list.permission", "ebl.command.list");
		DefaultValues.defaults.put("command.global-list.aliases", Arrays.asList("blist", "bls"));
		DefaultValues.defaults.put("formats.global-list.server-row-format", " &b{SERVER_NAME} &6{PLAYER_AMOUNT} &8{GRAPHIC_BAR} &8[&6{PERCENT}&8]");
		DefaultValues.defaults.put("formats.global-list.graphic-background-color", "&8&l");
		DefaultValues.defaults.put("formats.global-list.graphic-bar-color", "&7&l");
		DefaultValues.defaults.put("formats.global-list.full-message-format", "&8&m&l----------------------------------&r\n &eList of all servers on the Network:\n\n{SERVERS_ROWS}\n\n &7&oAnd &f&o{NOT_DISPLAYED_AMOUNT} &7&oservers not displayed.&r\n &6Total Players: &f{TOTAL_PLAYER_AMOUNT}\n\n &fTIP: &7Use &e/{LABEL} <server> &7to display a list of all players in the specified server.\n&8&m&l----------------------------------");
		DefaultValues.defaults.put("formats.global-list.no-servers-format", " &cNo servers to display.");
		DefaultValues.defaults.put("formats.server-list.full-message-format", "&8&m--------------------------------------&r\n &6Server Name: &f{SERVER_NAME}\n &6Players: &f{PLAYERS_COUNT}\n &eDisplaying page &f{PAGE} &6of &f{TOTAL_PAGES}\n\n{PLAYERS_ROWS}\n{PAGINATION_CONTROLLER}\n&8&m--------------------------------------");
		DefaultValues.defaults.put("formats.server-list.no-page-data-message", "&8&m--------------------------------------&r\n &cNo data to show in this page! Please try with a another page between &f1 &cand &f{TOTAL_PAGES}&c.\n&8&m--------------------------------------");
		DefaultValues.defaults.put("formats.server-list.no-players-message", "&8&m--------------------------------------&r\n &cThis server not have players at this moment.\n&8&m--------------------------------------");
		DefaultValues.defaults.put("behaviour.global-list.hide-empty-servers", true);
		DefaultValues.defaults.put("behaviour.global-list.max-servers-rows", 20);
		DefaultValues.defaults.put("behaviour.global-list.min-player-count-to-display-server", 3);
		DefaultValues.defaults.put("behaviour.server-list.players-per-page", 16);
		DefaultValues.defaults.put("behaviour.blacklisted-servers", new ArrayList<String>());
		DefaultValues.defaults.put("messages.server-players", "{SERVER_NAME} players");
		DefaultValues.defaults.put("messages.cannot-found-server", "&cThe specified server name {NAME} &ccannot be found in the Network.");
		DefaultValues.defaults.put("messages.previous-page-hover-message", "&eClick here to go to the page &f#{PAGE_NUMBER}");
		DefaultValues.defaults.put("messages.next-page-hover-message", "&eClick here to go to the page &f#{PAGE_NUMBER}");
		DefaultValues.defaults.put("messages.previous-page", "Previous Page");
		DefaultValues.defaults.put("messages.next-page", "Next Page");
		DefaultValues.defaults.put("messages.no-previous-page", "&cNo previous page available.");
		DefaultValues.defaults.put("messages.no-next-page", "&cNo next page available.");
	}
}