package dev.wirlie.bungeecord.glist.servers;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerGroup implements ServerInfoProvider {

    private final String id;
    private List<ServerInfo> servers = new ArrayList<>();

    public ServerGroup(String id) {
        this.id = id;
    }

    public void setServers(List<ServerInfo> servers) {
        this.servers = servers;
    }

    public List<ServerInfo> getServers() {
        return servers;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getPlayerCount() {
        return servers.stream().mapToInt(s -> s.getPlayers().size()).sum();
    }

    @Override
    public List<ProxiedPlayer> getPlayers() {
        return servers.stream().flatMap(s -> s.getPlayers().stream()).collect(Collectors.toList());
    }

    @Override
    public String getDisplayName() {
        return id + " (" + servers.stream().map(ServerInfo::getName).collect(Collectors.joining(", ")) + ")";
    }
}
