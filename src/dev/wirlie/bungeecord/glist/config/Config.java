package dev.wirlie.bungeecord.glist.config;

import java.util.List;
import java.util.Map;

public class Config {

    public static final ConfigEntry<String> COMMAND__GLIST__LABEL = new ConfigEntry<>("command.global-list.label");
    public static final ConfigEntry<String> COMMAND__GLIST__PERMISSION = new ConfigEntry<>("command.global-list.permission");
    public static final ConfigEntry<List<String>> COMMAND__GLIST__ALIASES = new ConfigEntry<>("command.global-list.aliases");

    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__SERVER_ROW_FORMAT = new ConfigEntry<>("formats.global-list.server-row-format");
    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__GRAPHIC_BACKGROUND_COLOR = new ConfigEntry<>("formats.global-list.graphic-background-color");
    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__GRAPHIC_BAR_COLOR = new ConfigEntry<>("formats.global-list.graphic-bar-color");
    public static final ConfigEntry<List<String>> FORMATS__GLOBAL_LIST__FULL_MESSAGE_FORMAT = new ConfigEntry<>("formats.global-list.full-message-format");
    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__NO_SERVERS_FORMAT = new ConfigEntry<>("formats.global-list.no-servers-format");
    public static final ConfigEntry<List<String>> FORMATS__SERVER_LIST__FULL_MESSAGE_FORMAT = new ConfigEntry<>("formats.server-list.full-message-format");
    public static final ConfigEntry<List<String>> FORMATS__SERVER_LIST__NO_PAGE_DATA_MESSAGE = new ConfigEntry<>("formats.server-list.no-page-data-message");
    public static final ConfigEntry<List<String>> FORMATS__SERVER_LIST__NO_PLAYERS_MESSAGE = new ConfigEntry<>("formats.server-list.no-players-message");
    public static final ConfigEntry<String> FORMATS__SERVER_LIST__PLAYER_ROW_FORMAT = new ConfigEntry<>("formats.server-list.player-row-format");
    public static final ConfigEntry<Integer> FORMATS__SERVER_LIST__PLAYERS_PER_ROW = new ConfigEntry<>("formats.server-list.players-per-row");
    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__MAIN_FORMAT = new ConfigEntry<>("formats.global-list.server-sp-option.main-format");
    public static final ConfigEntry<String> FORMATS__GLOBAL_LIST__SERVER_SP_OPTION__PLAYERS_FORMAT = new ConfigEntry<>("formats.global-list.server-sp-option.players-format");

    public static final ConfigEntry<Integer> BEHAVIOUR__CACHE_TIME__PLAYER_LIST_PAGES = new ConfigEntry<>("behaviour.cache-time.player-list-pages");
    public static final ConfigEntry<Boolean> BEHAVIOUR__GLOBAL_LIST__HIDE_EMPTY_SERVERS = new ConfigEntry<>("behaviour.global-list.hide-empty-servers");
    public static final ConfigEntry<Integer> BEHAVIOUR__GLOBAL_LIST__MAX_SERVERS_ROWS = new ConfigEntry<>("behaviour.global-list.max-servers-rows");
    public static final ConfigEntry<Integer> BEHAVIOUR__GLOBAL_LIST__MIN_PLAYER_COUNT_TO_DISPLAY_SERVER = new ConfigEntry<>("behaviour.global-list.min-player-count-to-display-server");
    public static final ConfigEntry<Integer> BEHAVIOUR__SERVER_LIST__PLAYERS_PER_PAGE = new ConfigEntry<>("behaviour.server-list.players-per-page");
    public static final ConfigEntry<List<String>> BEHAVIOUR__SERVER_LIST__BLACKLISTED_SERVERS = new ConfigEntry<>("behaviour.blacklisted-servers");
    public static final ConfigEntry<Boolean> BEHAVIOUR__GLOBAL_LIST__UPPER_CASE_NAMES = new ConfigEntry<>("behaviour.global-list-uppercase-server-names");
    public static final ConfigEntry<Boolean> BEHAVIOUR__SERVER_LIST__UPPER_CASE_NAME = new ConfigEntry<>("behaviour.server-list-uppercase-server-name");
    public static final ConfigEntry<Boolean> BEHAVIOUR__GROUPS_PREFIX__ENABLE = new ConfigEntry<>("behaviour.groups-prefix.enable");
    public static final ConfigEntry<Boolean> BEHAVIOUR__GROUPS_PREFIX__USE__LUCKPERMS = new ConfigEntry<>("behaviour.groups-prefix.use.luckperms");
    public static final ConfigEntry<Boolean> BEHAVIOUR__GROUPS_PREFIX__USE__INTERNAL_GROUP_SYSTEM = new ConfigEntry<>("behaviour.groups-prefix.use.internal-group-system");
    public static final ConfigEntry<Integer> BEHAVIOUR__GROUPS_PREFIX__PRIORITY__LUCKPERMS = new ConfigEntry<>("behaviour.groups-prefix.priority.luckperms");
    public static final ConfigEntry<Integer> BEHAVIOUR__GROUPS_PREFIX__PRIORITY__INTERNAL_GROUP_SYSTEM = new ConfigEntry<>("behaviour.groups-prefix.priority.internal-group-system");
    public static final ConfigEntry<Boolean> BEHAVIOUR__PLAYER_STATUS__VANISH__HIDE_VANISHED_USERS = new ConfigEntry<>("behaviour.player-status.vanish.hide-vanished-users");
    public static final ConfigEntry<String> BEHAVIOUR__PLAYER_STATUS__VANISH__BYPASS_PERMISSION = new ConfigEntry<>("behaviour.player-status.vanish.bypass-permission");
    public static final ConfigEntry<String> BEHAVIOUR__PLAYER_STATUS__VANISH__VANISH_PREFIX = new ConfigEntry<>("behaviour.player-status.vanish.vanish-prefix");
    public static final ConfigEntry<Boolean> BEHAVIOUR__PLAYER_STATUS__AFK__SHOW_AFK_STATE = new ConfigEntry<>("behaviour.player-status.afk.show-afk-state");
    public static final ConfigEntry<String> BEHAVIOUR__PLAYER_STATUS__AFK__AFK_PREFIX = new ConfigEntry<>("behaviour.player-status.afk.afk-prefix:");

    public static final ConfigEntry<String> MESSAGES__CANNOT_FOUND_SERVER = new ConfigEntry<>("messages.cannot-found-server");
    public static final ConfigEntry<String> MESSAGES__PREVIOUS_PAGE_HOVER_MESSAGE = new ConfigEntry<>("messages.previous-page-hover-message");
    public static final ConfigEntry<String> MESSAGES__NEXT_PAGE_HOVER_MESSAGE = new ConfigEntry<>("messages.next-page-hover-message");
    public static final ConfigEntry<String> MESSAGES__PREVIOUS_PAGE = new ConfigEntry<>("messages.previous-page");
    public static final ConfigEntry<String> MESSAGES__NEXT_PAGE = new ConfigEntry<>("messages.next-page");
    public static final ConfigEntry<String> MESSAGES__NO_PREVIOUS_PAGE = new ConfigEntry<>("messages.no-previous-page");
    public static final ConfigEntry<String> MESSAGES__NO_NEXT_PAGE = new ConfigEntry<>("messages.no-next-page");
    public static final ConfigEntry<String> MESSAGES__ALL_PAGES = new ConfigEntry<>("messages.all-pages");
    public static final ConfigEntry<String> MESSAGES__CLICK_TO_SHOW_PLAYERS = new ConfigEntry<>("messages.click-to-show-players");

    public static final ConfigEntry<Boolean> UPDATES__CHECK_UPDATES = new ConfigEntry<>("updates.check-updates");
    public static final ConfigEntry<Boolean> UPDATES__NOTIFY__ENABLE = new ConfigEntry<>("updates.notify.enable");
    public static final ConfigEntry<String> UPDATES__NOTIFY__PERMISSION = new ConfigEntry<>("updates.notify.permission");
    public static final ConfigEntry<List<String>> UPDATES__NOTIFY__MESSAGE = new ConfigEntry<>("updates.notify.message");
    public static final ConfigEntry<Integer> UPDATES__NOTIFY__DELAY_MS = new ConfigEntry<>("updates.notify.delay-ms");

    public static final ConfigEntry<List<Map<String, Object>>> SERVERS__GROUPS = new ConfigEntry<>("servers.groups");

}
