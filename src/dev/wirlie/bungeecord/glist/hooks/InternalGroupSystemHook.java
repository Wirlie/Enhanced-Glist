package dev.wirlie.bungeecord.glist.hooks;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.groups.Group;
import dev.wirlie.bungeecord.glist.groups.GroupManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InternalGroupSystemHook extends GroupHook {

    private final GroupManager manager;

    public InternalGroupSystemHook(EnhancedBCL plugin) {
        super(Config.BEHAVIOUR__GROUPS_PREFIX__PRIORITY__INTERNAL_GROUP_SYSTEM.get());
        this.manager = new GroupManager(plugin);
        manager.loadGroups();
    }

    @Override
    public @Nullable String getPrefix(ProxiedPlayer player) {
        return manager.getGroup(player).map(Group::getPrefix).orElse(null);
    }

    @Override
    public void reload() {
        manager.loadGroups();
    }

}
