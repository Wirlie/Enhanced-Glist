package dev.wirlie.bungeecord.glist.hooks;

import dev.wirlie.bungeecord.glist.config.Config;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

public class LuckPermsHook extends GroupHook {

    public LuckPermsHook() {
        super(Config.BEHAVIOUR__GROUPS_PREFIX__PRIORITY__LUCKPERMS.get());
    }

    @Override
    public @Nullable String getPrefix(ProxiedPlayer player) {
        String prefix = null;

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());

        if (user != null) {
            prefix = user
                    .getCachedData()
                    .getMetaData(QueryOptions.defaultContextualOptions())
                    .getPrefix();
        }

        if(prefix == null || prefix.equalsIgnoreCase("null")) {
            prefix = "";
        }

        return prefix;
    }

    @Override
    public void reload() {}

}
