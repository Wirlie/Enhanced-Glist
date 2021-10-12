package dev.wirlie.bungeecord.glist.updater;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import dev.wirlie.bungeecord.glist.util.Pair;
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

    public UpdateChecker(EnhancedBCL plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates(Boolean firstExecution, Consumer<Pair<String, Boolean>> versionConsumer, Consumer<Throwable> exceptionConsumer) {
        plugin.getLogger().info("Checking for updates...");

        BungeeCord.getInstance().getScheduler().runAsync(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=53295").openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String version = scanner.next();

                    boolean hasUpdate = determineIfUpdateAvailable(version);

                    versionConsumer.accept(new Pair<>(version, hasUpdate));

                    if (hasUpdate) {
                        if (!firstExecution) {
                            plugin.getLogger().info("-------------------------------------------");
                        }
                        plugin.getLogger().warning("New update found!! Download the latest update from: ");
                        plugin.getLogger().warning("https://www.spigotmc.org/resources/enhancedbungeelist.53295/");
                    } else {
                        if (firstExecution) {
                            plugin.getLogger().info("Plugin is up to date.");
                        }
                    }

                    if (firstExecution || hasUpdate) {
                        plugin.getLogger().info("-------------------------------------------");
                    }
                }
            } catch (Throwable exception) {
                exceptionConsumer.accept(exception);
            }
        });
    }

    private boolean determineIfUpdateAvailable(String remoteVersion) {
        try {
            String currentVersion = plugin.getDescription().getVersion();

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
            plugin.getLogger().log(Level.SEVERE, "Failed to determine if there is a new update! Remote[" + remoteVersion + "], Plugin[" + plugin.getDescription().getVersion() + "]", ex);
            return false;
        }
    }

}
