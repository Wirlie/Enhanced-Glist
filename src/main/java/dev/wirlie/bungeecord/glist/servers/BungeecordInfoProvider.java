package dev.wirlie.bungeecord.glist.servers;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class BungeecordInfoProvider implements ServerInfoProvider {

    private final ServerInfo info;

    public BungeecordInfoProvider(ServerInfo info) {
        this.info = info;
    }

    @Override
    public String getId() {
        return info.getName();
    }

    @Override
    public int getPlayerCount() {
        return info.getPlayers().size();
    }

    @Override
    public List<ProxiedPlayer> getPlayers() {
        return new ArrayList<>(info.getPlayers());
    }

    @Override
    public String getDisplayName() {
        return info.getName();
    }
}
