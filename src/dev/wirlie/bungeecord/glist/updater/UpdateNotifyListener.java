package dev.wirlie.bungeecord.glist.updater;

import dev.wirlie.bungeecord.glist.DefaultValues;
import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.util.TextUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
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
            Configuration config = plugin.getConfig();
            if(config.getBoolean("updates.notify.enable", DefaultValues.getDefaultBoolean("updates.notify.enable"))) {
                String permission = config.getString("updates.notify.permission", DefaultValues.getDefaultString("updates.notify.permission"));
                if(pp.hasPermission(permission)) {
                    int delay = config.getInt("updates.notify.delay-ms", DefaultValues.getDefaultInt("updates.notify.delay-ms"));

                    if(delay < 1) {
                        delay = 1;
                    }

                    BungeeCord.getInstance().getScheduler().schedule(plugin, () -> {
                        //notify
                        List<String> rawMessages = config.getStringList("updates.notify.message");
                        if(rawMessages.isEmpty()) {
                            rawMessages = DefaultValues.getDefaultStringList("updates.notify.message");
                        }

                        for(String line : rawMessages) {
                            pp.sendMessage(TextUtil.fromLegacy(line));
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

}
