package dev.wirlie.bungeecord.glist.servers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public interface ServerInfoProvider {

    String getId();

    String getDisplayName();

    int getPlayerCount();

    List<ProxiedPlayer> getPlayers();

}
