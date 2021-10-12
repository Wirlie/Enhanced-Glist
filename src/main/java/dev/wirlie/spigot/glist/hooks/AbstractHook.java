package dev.wirlie.spigot.glist.hooks;

import dev.wirlie.spigot.glist.EnhancedBCLBridge;
import org.bukkit.entity.Player;

public interface AbstractHook {

    void sendStateToBridge(Player player, StateNotificationType type, boolean newValue);

    void sendAllPlayersStateToBridge();

    void sendPlayerToBridge(Player player);

    void registerListeners(EnhancedBCLBridge bridge);

}
