package dev.wirlie.bungeecord.glist.updater

import dev.wirlie.bungeecord.glist.EnhancedBCL
import dev.wirlie.bungeecord.glist.util.Pair
import net.md_5.bungee.BungeeCord
import java.net.URL
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import kotlin.Boolean
import kotlin.Exception
import kotlin.Throwable
import kotlin.check

class UpdateChecker(private val plugin: EnhancedBCL) {
    
    fun checkForUpdates(
        firstExecution: Boolean,
        versionConsumer: Consumer<Pair<String, Boolean>>,
        exceptionConsumer: Consumer<Throwable?>
    ) {
        plugin.logger.info("Checking for updates...")
        
        BungeeCord.getInstance().scheduler.runAsync(plugin) {
            try {
                URL("https://api.spigotmc.org/legacy/update.php?resource=53295").openStream().use { inputStream ->
                    Scanner(inputStream).use { scanner ->
                        if (scanner.hasNext()) {
                            val version = scanner.next()
                            val hasUpdate = determineIfUpdateAvailable(version)
                            versionConsumer.accept(Pair(version, hasUpdate))
                            
                            if (hasUpdate) {
                                if (!firstExecution) {
                                    plugin.logger.info("-------------------------------------------")
                                }
                                plugin.logger.warning("New update found!! Download the latest update from: ")
                                plugin.logger.warning("https://www.spigotmc.org/resources/enhancedbungeelist.53295/")
                            } else {
                                if (firstExecution) {
                                    plugin.logger.info("Plugin is up to date.")
                                }
                            }
                            
                            if (firstExecution || hasUpdate) {
                                plugin.logger.info("-------------------------------------------")
                            }
                        }
                    }
                }
            } catch (exception: Throwable) {
                exceptionConsumer.accept(exception)
            }
        }
    }

    private fun determineIfUpdateAvailable(remoteVersion: String): Boolean {
        return try {
            val currentVersion = plugin.description.version

            if (currentVersion == remoteVersion) {
                return false
            }

            val currentVersionParts: MutableList<String> =
                ArrayList(listOf(*currentVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()))
            val remoteVersionParts: MutableList<String> =
                ArrayList(listOf(*remoteVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()))
            val maxParts = currentVersionParts.size.coerceAtLeast(remoteVersionParts.size)

            for (i in currentVersionParts.size until maxParts) {
                currentVersionParts.add("0")
            }

            for (i in remoteVersionParts.size until maxParts) {
                remoteVersionParts.add("0")
            }

            check(currentVersionParts.size == remoteVersionParts.size) {
                "Unexpected size mismatch, remote parts: [${remoteVersionParts.joinToString(".")}] vs plugin parts: [${currentVersionParts.joinToString(".")}]"
            }

            for (i in 0 until maxParts) {
                val remotePart = remoteVersionParts[i]
                val currentPart = currentVersionParts[i]
                val remotePartNumber = remotePart.toInt()
                val currentPartNumber = currentPart.toInt()

                if (remotePartNumber > currentPartNumber) {
                    return true
                }

                if (remotePartNumber < currentPartNumber) {
                    return false
                }
            }

            false
        } catch (ex: Exception) {
            plugin.logger.log(
                Level.SEVERE,
                "Failed to determine if there is a new update! Remote[" + remoteVersion + "], Plugin[" + plugin.description.version + "]",
                ex
            )

            false
        }
    }
}
