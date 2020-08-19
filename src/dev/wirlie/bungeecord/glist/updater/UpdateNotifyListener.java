package dev.wirlie.bungeecord.glist.updater;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.util.TextUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateNotifyListener implements Listener {

    private final EnhancedBCL plugin;

    public UpdateNotifyListener(EnhancedBCL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void event(PostLoginEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        if(pp.isConnected()) {
            if(Config.UPDATES__NOTIFY__ENABLE.get()) {
                if(pp.hasPermission(Config.UPDATES__NOTIFY__PERMISSION.get())) {
                    int delay = Config.UPDATES__NOTIFY__DELAY_MS.get();

                    if(delay < 1) {
                        delay = 1;
                    }

                    BungeeCord.getInstance().getScheduler().schedule(plugin, () -> {
                        //notify
                        List<String> rawMessages = Config.UPDATES__NOTIFY__MESSAGE.get();

                        for(String line : rawMessages) {
                            pp.sendMessage(TextUtil.fromLegacy(line));
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

}
