package dev.wirlie.bungeecord.glist;

import net.md_5.bungee.config.Configuration;

import java.util.Arrays;
import java.util.Collections;

public class ConfigurationValidator {
	public static void validate(EnhancedBCL plugin) {
		Configuration configuration = plugin.getConfig();

		boolean needCommentsFix = false;

		if(!configuration.contains("command.global-list.label")) {
			needCommentsFix = true;
			configuration.set("command.global-list.label", "glist");
		}

		if(!configuration.contains("command.global-list.permission")) {
			needCommentsFix = true;
			configuration.set("command.global-list.permission", "bungeecord.command.list");
		}

		if(!configuration.contains("command.global-list.aliases")) {
			needCommentsFix = true;
			configuration.set("command.global-list.aliases", Arrays.asList("blist", "bls"));
		}

		if(!configuration.contains("formats.global-list.server-row-format")) {
			needCommentsFix = true;
			configuration.set("formats.global-list.server-row-format", " &b{SERVER_NAME} &6{PLAYER_AMOUNT} &8{GRAPHIC_BAR} &8[&6{PERCENT}&8]");
		}

		if(!configuration.contains("formats.global-list.graphic-background-color")) {
			needCommentsFix = true;
			configuration.set("formats.global-list.graphic-background-color", "&8&l");
		}

		if(!configuration.contains("formats.global-list.graphic-bar-color")) {
			needCommentsFix = true;
			configuration.set("formats.global-list.graphic-bar-color", "&7&l");
		}

		if(!configuration.contains("formats.global-list.full-message-format")) {
			needCommentsFix = true;
			configuration.set("formats.global-list.full-message-format", Arrays.asList("&8&m&l----------------------------------&r", " &eList of all servers on the Network:", "", "{SERVERS_ROWS}", "", " &7&oAnd &f&o{NOT_DISPLAYED_AMOUNT} &7&oservers not displayed.&r", " &6Total Players: &f{TOTAL_PLAYER_AMOUNT}", "", " &fTIP: &7Use &e/{LABEL} <server> &7to display a list of all players in the specified server.", "&8&m&l----------------------------------"));
		}

		if(!configuration.contains("formats.global-list.no-servers-format")) {
			needCommentsFix = true;
			configuration.set("formats.global-list.no-servers-format", " &cNo servers to display.");
		}

		if(!configuration.contains("formats.server-list.full-message-format")) {
			needCommentsFix = true;
			configuration.set("formats.server-list.full-message-format", Arrays.asList("&8&m--------------------------------------&r", " &6Server Name: &f{SERVER_NAME}", " &6Players: &f{PLAYERS_COUNT}", " &eDisplaying page &f{PAGE} &6of &f{TOTAL_PAGES}", "", "{PLAYERS_ROWS}", "{PAGINATION_CONTROLLER}", "&8&m--------------------------------------"));
		}

		if(!configuration.contains("formats.server-list.no-page-data-message")) {
			needCommentsFix = true;
			configuration.set("formats.server-list.no-page-data-message", Arrays.asList("&8&m--------------------------------------&r", " &cNo data to show in this page! Please try with a another page between &f1 &cand &f{TOTAL_PAGES}&c.", "&8&m--------------------------------------"));
		}

		if(!configuration.contains("formats.server-list.no-players-message")) {
			needCommentsFix = true;
			configuration.set("formats.server-list.no-players-message", Arrays.asList("&8&m--------------------------------------&r", " &cThis server not have players at this moment.", "&8&m--------------------------------------"));
		}

		if(!configuration.contains("behaviour.global-list.hide-empty-servers")) {
			needCommentsFix = true;
			configuration.set("behaviour.global-list.hide-empty-servers", true);
		}

		if(!configuration.contains("behaviour.global-list.max-servers-rows")) {
			needCommentsFix = true;
			configuration.set("behaviour.global-list.max-servers-rows", 20);
		}

		if(!configuration.contains("behaviour.global-list.min-player-count-to-display-server")) {
			needCommentsFix = true;
			configuration.set("behaviour.global-list.min-player-count-to-display-server", 3);
		}

		if(!configuration.contains("behaviour.server-list.players-per-page")) {
			needCommentsFix = true;
			configuration.set("behaviour.server-list.players-per-page", 16);
		}

		if(!configuration.contains("behaviour.blacklisted-servers")) {
			needCommentsFix = true;
			configuration.set("behaviour.blacklisted-servers", Collections.singletonList("Login"));
		}

		if(!configuration.contains("messages.server-players")) {
			needCommentsFix = true;
			configuration.set("messages.server-players", "{SERVER_NAME} players:");
		}

		if(!configuration.contains("messages.cannot-found-server")) {
			needCommentsFix = true;
			configuration.set("messages.cannot-found-server", "&cThe specified server name {NAME} &ccannot be found in the Network.");
		}

		if(!configuration.contains("messages.previous-page-hover-message")) {
			needCommentsFix = true;
			configuration.set("messages.previous-page-hover-message", "&eClick here to go to the page &f#{PAGE_NUMBER}");
		}

		if(!configuration.contains("messages.next-page-hover-message")) {
			needCommentsFix = true;
			configuration.set("messages.next-page-hover-message", "&eClick here to go to the page &f#{PAGE_NUMBER}");
		}

		if(!configuration.contains("messages.previous-page")) {
			needCommentsFix = true;
			configuration.set("messages.previous-page", "Previous Page");
		}

		if(!configuration.contains("messages.next-page")) {
			needCommentsFix = true;
			configuration.set("messages.next-page", "Next Page");
		}

		if(!configuration.contains("messages.no-previous-page")) {
			needCommentsFix = true;
			configuration.set("messages.no-previous-page", "&cNo previous page available.");
		}

		if(!configuration.contains("messages.no-next-page")) {
			needCommentsFix = true;
			configuration.set("messages.no-next-page", "&cNo next page available.");
		}

		if(!configuration.contains("behaviour.global-list-uppercase-server-names")) {
			needCommentsFix = true;
			configuration.set("behaviour.global-list-uppercase-server-names", false);
		}

		if(!configuration.contains("behaviour.server-list-uppercase-server-name")) {
			needCommentsFix = true;
			configuration.set("behaviour.server-list-uppercase-server-name", false);
		}

		plugin.saveConfig();

		if(needCommentsFix) {
			fixComments(plugin);
		}
	}

	private static void fixComments(EnhancedBCL plugin) {
		//TODO
	}
}
