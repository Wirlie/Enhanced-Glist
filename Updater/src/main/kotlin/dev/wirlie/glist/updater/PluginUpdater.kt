/*
 * Enhanced Glist - Plugin that enhances /glist command
 * Copyright (C) 2024 Josue Acevedo and the Enhanced Glist contributors
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
import java.net.URI

class PluginUpdater(
    private val updaterScheduler: UpdaterScheduler,
    private val checkInterval: Int,
    private val consoleNotificationInterval: Int,
    val logger: SimpleLogger,
    private val pluginVersion: String,
    private val consoleNotification: Boolean,
    private val checkForUpdates: Boolean
) {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private var client = OkHttpClient()
    private var firstCheck = true

    var updateAvailable = false
    var updateDownloadURL = "https://www.spigotmc.org/resources/enhanced-glist-bungeecord-velocity.53295/"

    init {
        setup()
    }

    private fun setup() {
        if(checkForUpdates) {
            scheduleCheck()
        } else {
            logger.info("[Updater] Updater is disabled from config (check-for-updates = false).")
        }
    }

    private fun getSpigotReleases(): Array<SpigotReleaseModel> {
        val requestURI = URI.create("https://api.spiget.org/v2/resources/53295/versions?sort=-releaseDate")
        val request = Request.Builder()
            .url(requestURI.toURL())
            .method("GET", null)
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body.string(), object: TypeToken<Array<SpigotReleaseModel>>(){}.type)
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

            val ourRelease = releases.firstOrNull { release -> release.name.trim().equals(versionExpected, true) }

            if (ourRelease == null) {
                val latestRelease = releases.maxByOrNull { it.releaseDate }!!
                if(firstCheck) {
                    firstCheck = false
                    logger.warning("[Updater] ----------------------------------------------------------------------------------")
                    logger.warning("[Updater] Updater is unable to find the current version of the plugin from SpigotMC website")
                    logger.warning("[Updater]             Plugin Version: ${pluginVersion.replace("-SNAPSHOT", "")}")
                    logger.warning("[Updater]    Latest SpigotMC Version: ${latestRelease.name}")
                    logger.warning("[Updater] Maybe are you using a preview build of Enhanced Glist?")
                    logger.warning("[Updater] If you think that this is an error please fill a report at our")
                    logger.warning("[Updater] GitHub repository: https://github.com/Wirlie/Enhanced-Glist")
                    logger.warning("[Updater] ----------------------------------------------------------------------------------")
                }

                return@scheduleUpdaterCheckTask
            }

            var hasUpdate = false
            val latestRelease = releases.maxByOrNull { it.releaseDate }!!

            if(!ourRelease.name.equals(latestRelease.name, true)) {
                // If our release does not match the latest release published at SpigotMC then an update is available...
                hasUpdate = true
            }

            if(hasUpdate) {
                firstCheck = false
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
