package dev.wirlie.bungeecord.glist;

import net.md_5.bungee.BungeeCord;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public class UpdateChecker {

    private final EnhancedBCL plugin;
    private String lastKnowVersion = null;
    private boolean hasUpdate = false;

    public UpdateChecker(EnhancedBCL plugin) {
        this.plugin = plugin;
    }

    public void hasUpdate(Consumer<Boolean> updateConsumer, Consumer<Throwable> exceptionConsumer) {
        if(lastKnowVersion != null) {
            updateConsumer.accept(hasUpdate);
        } else {
            //obtener
            getSpigotVersion(version -> updateConsumer.accept(hasUpdate), ex -> {
                if(ex != null) {
                    exceptionConsumer.accept(ex);
                }
            });
        }
    }

    public void getLastKnowVersion(Consumer<String> versionConsumer, Consumer<Throwable> exceptionConsumer) {
        if(lastKnowVersion != null) {
            versionConsumer.accept(lastKnowVersion);
        } else {
            //obtener
            getSpigotVersion(versionConsumer, ex -> {
                if(ex != null) {
                    exceptionConsumer.accept(ex);
                }
            });
        }
    }

    public void getSpigotVersion(Consumer<String> versionConsumer, Consumer<Throwable> exceptionConsumer) {
        plugin.getLogger().info("Checking for updates...");
        BungeeCord.getInstance().getScheduler().runAsync(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=53295").openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String version = scanner.next();
                    lastKnowVersion = version;
                    hasUpdate = determineIfUpdateAvailable();

                    plugin.getLogger().info("-------------------------------------------");
                    plugin.getLogger().info("Remote version (SpigotMC): " + version);
                    plugin.getLogger().info("Current version (Plugin): " + plugin.getDescription().getVersion());
                    plugin.getLogger().info("-------------------------------------------");

                    if(hasUpdate) {
                        plugin.getLogger().warning("New update found!! Download the latest update from: ");
                        plugin.getLogger().warning("https://www.spigotmc.org/resources/enhancedbungeelist.53295/");
                    } else {
                        plugin.getLogger().info("Plugin is up to date.");
                    }

                    plugin.getLogger().info("-------------------------------------------");

                    versionConsumer.accept(version);
                }
            } catch (Throwable exception) {
                exceptionConsumer.accept(exception);
            }
        });
    }

    private boolean determineIfUpdateAvailable() {
        try {
            String currentVersion = plugin.getDescription().getVersion();
            String remoteVersion = lastKnowVersion;

            if(currentVersion.equals(remoteVersion)) {
                return false;
            }

            List<String> currentVersionParts = new ArrayList<>(Arrays.asList(currentVersion.split("\\.")));
            List<String> remoteVersionParts = new ArrayList<>(Arrays.asList(remoteVersion.split("\\.")));

            int maxParts = Math.max(currentVersionParts.size(), remoteVersionParts.size());

            for(int i = currentVersionParts.size(); i < maxParts; i++) {
                currentVersionParts.add("0");
            }

            for(int i = remoteVersionParts.size(); i < maxParts; i++) {
                remoteVersionParts.add("0");
            }

            if(currentVersionParts.size() != remoteVersionParts.size()) {
                throw new IllegalStateException("Unexpected size mismatch, remote parts: [" + String.join(".", remoteVersionParts) + "] vs plugin parts: [" + String.join(".", currentVersionParts) +  "]");
            }

            for(int i = 0; i < maxParts; i++) {
                String remotePart = remoteVersionParts.get(i);
                String currentPart = currentVersionParts.get(i);

                int remotePartNumber = Integer.parseInt(remotePart);
                int currentPartNumber = Integer.parseInt(currentPart);

                if(remotePartNumber > currentPartNumber) {
                    return true;
                }

                if(remotePartNumber < currentPartNumber) {
                    return false;
                }
            }

            return false;
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to determine if there is a new update! Remote[" + lastKnowVersion + "], Plugin[" + plugin.getDescription().getVersion() + "]", ex);
            return false;
        }
    }

}
