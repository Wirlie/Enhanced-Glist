/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2022 Josue Acevedo and the Enhanced Glist contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: wirlie.dev@gmail.com
 */

package dev.wirlie.glist.updater

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URI

class PluginUpdater(
    private val updaterScheduler: UpdaterScheduler,
    private val checkInterval: Int,
    private val consoleNotificationInterval: Int,
    val logger: SimpleLogger,
    val pluginFolder: File,
    val pluginVersion: String,
    private val consoleNotification: Boolean
) {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private var client = OkHttpClient()
    private var firstCheck = true

    var updateAvailable = false
    var updateDownloadURL = "https://www.spigotmc.org/resources/enhanced-glist-bungeecord-velocity.53295/"

    fun setup() {
        scheduleCheck()
    }

    private fun getSpigotReleases(): Array<SpigotReleaseModel> {
        val requestURI = URI.create("https://api.spiget.org/v2/resources/53295/versions?sort=-releaseDate")
        val request = Request.Builder()
            .url(requestURI.toURL())
            .method("GET", null)
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), object: TypeToken<Array<SpigotReleaseModel>>(){}.type)
    }

    fun stop() {
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdownNow()
        updaterScheduler.stopUpdaterCheckTask()
        updaterScheduler.stopConsoleNotificationTask()
    }

    private fun scheduleCheck() {
        updaterScheduler.scheduleUpdaterCheckTask({
            val releases = getSpigotReleases()
            val versionExpected = pluginVersion.replace("-SNAPSHOT", "")

            var ourRelease: SpigotReleaseModel? = null

            // Try to get our release from published releases at SpigotMC
            for(release in releases) {
                if(release.name.trim().equals(versionExpected, true)) {
                    // Release found!! At this point we know our release timestamp
                    ourRelease = release
                    break
                }
            }

            var hasUpdate = false
            val latestRelease = releases.maxByOrNull { it.releaseDate }!!

            if(ourRelease != null) {
                logger.info("[Updater] Version found from spigot: ${ourRelease.name}")
                if(ourRelease.name != latestRelease.name) {
                    // If our release does not match the latest release published at SpigotMC then an update is available...
                    hasUpdate = true
                }
            } else {
                logger.info("[Updater] Failed to retrieve current version from spigot (Not found: ${pluginVersion.replace("-SNAPSHOT", "")}), assuming that this version is out of date...")
                // We cannot find our release, so probably this version is a really outdated version or is an unpublished version
                hasUpdate = true
            }

            if(hasUpdate) {
                updateAvailable = true
                updaterScheduler.stopUpdaterCheckTask()
                printUpdateMessage(latestRelease)
                scheduleConsoleNotificationTask(latestRelease)
            } else {
                if(firstCheck) {
                    firstCheck = false
                    printUpToDateMessage(latestRelease)
                }
            }
        }, checkInterval)
    }

    private fun printUpdateMessage(latestRelease: SpigotReleaseModel) {
        logger.warning("======================= UPDATE AVAILABLE =======================")
        logger.warning("[Updater] New update available: ${latestRelease.name}")
        logger.warning("[Updater] Current version: ${pluginVersion.replace("-SNAPSHOT", "")}")
        logger.warning("[Updater] Download the latest update from:")
        logger.warning("[Updater] $updateDownloadURL")
        logger.warning("=================================================================")
    }

    private fun printUpToDateMessage(latestRelease: SpigotReleaseModel) {
        logger.info("[Updater] No updates available, current version: ${pluginVersion.replace("-SNAPSHOT", "")}, remote version: ${latestRelease.name}")
    }

    private fun scheduleConsoleNotificationTask(latestRelease: SpigotReleaseModel) {
        if (consoleNotification) {
            // Only notify to console periodically if enabled
            updaterScheduler.scheduleConsoleNotificationTask({
                printUpdateMessage(latestRelease)
            }, consoleNotificationInterval)
        }
    }

}
