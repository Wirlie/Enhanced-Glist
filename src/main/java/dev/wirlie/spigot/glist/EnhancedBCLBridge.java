package dev.wirlie.spigot.glist;

import dev.wirlie.spigot.glist.hooks.AbstractHook;
import dev.wirlie.spigot.glist.hooks.EssentialsHook;
import dev.wirlie.spigot.glist.hooks.SuperVanishHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EnhancedBCLBridge extends JavaPlugin {

    private final List<AbstractHook> hooks = new ArrayList<>();

    @Override
    public void onEnable() {
        Logger logger = getLogger();

        logger.info("---------------- EnhancedBungeeList - (Bridge) ----------------");
        logger.info("This plugin will send player state changes to BungeeCord like");
        logger.info("afk and vanish state.");
        logger.info("---------------------------------------------------------------");
        tryEssentialsHook();
        trySuperVanishHook();

        if(hooks.isEmpty()) {
            setEnabled(false);
            logger.warning("Cannot find one of these plugins in this server: Essentials, SuperVanish or PremiumVanish");
            logger.warning("Plugin not enabled.");
            return;
        }

        logger.info("Initializing communication to BungeeCord...");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "ebcl:bridge");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "ebcl:bridge", new BridgeListener(this));

        //send all player state
        for(AbstractHook hook : hooks) {
            hook.registerListeners(this);
            hook.sendAllPlayersStateToBridge();
        }

        logger.info("Plugin enabled and ready to work!");

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
    }

    public List<AbstractHook> getHooks() {
        return hooks;
    }

    private void tryEssentialsHook() {
        Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");

        if(essentialsPlugin != null) {
            getLogger().info("Hooked into Essentials!");
            hooks.add(new EssentialsHook(this, essentialsPlugin));
        }
    }

    private void trySuperVanishHook() {
        Plugin superVanishPlugin = Bukkit.getPluginManager().getPlugin("SuperVanish");

        if(superVanishPlugin != null) {
            getLogger().info("Hooked into SuperVanish!");
            hooks.add(new SuperVanishHook(this));
        } else {
            //try with PremiumVanish
            superVanishPlugin = Bukkit.getPluginManager().getPlugin("PremiumVanish");

            if(superVanishPlugin != null) {
                getLogger().info("Hooked into PremiumVanish!");
                hooks.add(new SuperVanishHook(this));
            }
        }
    }

}
