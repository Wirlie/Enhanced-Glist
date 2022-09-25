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

import com.google.gson.JsonParser
import okhttp3.*
import okio.IOException
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import java.net.URI
import java.nio.file.Files
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory


class PluginUpdater(
    private val updaterScheduler: UpdaterScheduler,
    private val checkInterval: Int,
    private val consoleNotificationInterval: Int,
    val logger: SimpleLogger,
    val pluginFolder: File,
    private val consoleNotification: Boolean
) {

    private var client = OkHttpClient()
    private val fileName = "metadata.conf"
    private lateinit var metaDataProperties: MetaDataProperties

    var updateAvailable = false
    var updateDownloadURL = ""

    fun setup() {
        val inputFile = this::class.java.getResourceAsStream("/$fileName")
        if(inputFile == null) {
            logger.severe("Cannot get $fileName from plugin .jar!! This file is required, Updater will not be enabled.")
            logger.severe("Please download the latest version from our CI Server.")
            return
        }

        val file = File(pluginFolder, fileName)
        if(file.exists()) {
            Files.delete(file.toPath())
        }
        Files.copy(inputFile, file.toPath())

        // Load metadata
        logger.info("[Updater] Reading $fileName file to know the current git version...")
        val loader = HoconConfigurationLoader.builder()
            .prettyPrinting(true)
            .emitComments(true)
            .path(file.toPath())
            .build()
            .load()

        metaDataProperties = loader.get(MetaDataProperties::class.java) ?: MetaDataProperties()

        logger.info("==================================")
        logger.info("       Version: ${metaDataProperties.build.version}")
        logger.info("    Git Branch: ${metaDataProperties.build.branch}")
        logger.info("       Build #: ${metaDataProperties.build.number}")
        logger.info("==================================")

        if(metaDataProperties.build.branch != "master") {
            logger.warning("======================== BETA BUILD ========================")
            logger.warning("This is a Beta build and may be unstable or contains performance")
            logger.warning("problems, please make a Backup before testing a Beta Build.")
            logger.warning("Make a bug report if you have found a bug related to this build.")
            logger.warning("============================================================")
        }

        if(
            metaDataProperties.build.version.equals("unknown", true) ||
            metaDataProperties.build.project.equals("unknown", true) ||
            metaDataProperties.build.number.equals("unknown", true) ||
            metaDataProperties.build.branch.equals("unknown", true) ||
            metaDataProperties.build.fullHash.equals("unknown", true) ||
            metaDataProperties.build.targetRelease.equals("unknown", true) ||
            metaDataProperties.build.timestamp.equals("unknown", true)
        ) {
            logger.severe("[Updater] MetaData is corrupted or invalid, updater disabled.")
            logger.severe("[Updater] Please download the latest version from our CI Server or from SpigotMC.")
            return
        }

        scheduleCheck()
    }

    private fun getLatestBuildFromJenkins(
        onBuildNumberResolved: (Int) -> Unit
    ) {
        val project = metaDataProperties.build.project.split('/')
        val rootJobName = project[0]
        val branch = metaDataProperties.build.branch

        // Make HTTP Client and HTTP Request using Java11+

        val requestURI = URI.create("https://ci.wirlie.net/job/$rootJobName/job/$branch/api/xml?pretty=true")
        val request = Request.Builder()
            .url(requestURI.toURL())
            .method("GET", null)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                logger.severe("[Updater] Something went wrong while fetching information from CI Server (HTTP Client Exception).")
                e.printStackTrace()
                onBuildNumberResolved(-1)
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code != 200) {
                    logger.severe("[Updater] Something went wrong while fetching information from CI Server: Not HTTP 200 code, HTTP ${response.code} code received instead.")
                    onBuildNumberResolved(-1)
                    return
                }

                val body = response.body

                if(body == null) {
                    logger.severe("[Updater] Something went wrong while fetching information from CI Server: HTTP code ${response.code}, no body is provided.")
                    onBuildNumberResolved(-1)
                    return
                }

                try {
                    // Read XML from Jenkins
                    val dbf = DocumentBuilderFactory.newInstance()
                    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                    val db = dbf.newDocumentBuilder()
                    val doc = db.parse(InputSource(StringReader(body.string())))
                    doc.documentElement.normalize()

                    // Get latest successful build
                    val list = doc.getElementsByTagName("lastSuccessfulBuild")
                    var latestBuildNumberFromCI = -1

                    for (i in 0 until list.length) {
                        val node = list.item(i)

                        if (node.nodeType == Node.ELEMENT_NODE) {
                            val element = node as Element
                            val numberNodeList = element.getElementsByTagName("number")

                            if (numberNodeList.length > 0) {
                                val numberNode = numberNodeList.item(0)
                                val tryNumber = numberNode.textContent.toIntOrNull()
                                if (tryNumber == null) {
                                    logger.warning("[Updater] Unexpected build number from CI: '${numberNode.textContent}' is not an integer.")
                                    break
                                }

                                latestBuildNumberFromCI = tryNumber
                                break
                            }
                        }
                    }

                    if (latestBuildNumberFromCI == -1) {
                        logger.warning("[Updater] Cannot fetch latest build from our CI Server: Unresolved build number (resolves as -1).")
                        onBuildNumberResolved(-1)
                        return
                    }

                    onBuildNumberResolved(latestBuildNumberFromCI)
                } catch (ex: Throwable) {
                    logger.severe("[Updater] Something went wrong while fetching information from CI Server (Java Exception).")
                    ex.printStackTrace()
                    onBuildNumberResolved(-1)
                }
            }

        })
    }

    private fun checkIfPublishedAtSpigot() {
        val requestURI = URI.create("https://api.spiget.org/v2/resources/53295/versions?sort=-releaseDate")
        val request = Request.Builder()
            .url(requestURI.toURL())
            .method("GET", null)
            .build()

        val response = client.newCall(request).execute()

        // Deprecated, but Spigot 1.8 uses this...
        @Suppress("DEPRECATION")
        val json = JsonParser().parse(response.body!!.string())
        val jsonArray = json.asJsonArray

        for(element in jsonArray) {
            val jsonObject = element.asJsonObject
            if(jsonObject.has("name")) {
                if(jsonObject.get("name").asString.equals(metaDataProperties.build.targetRelease, true)) {
                    updateAvailable = true
                    // Redirect to Spigot to download the release instead of the beta build
                    updateDownloadURL = "https://www.spigotmc.org/resources/enhanced-glist-bungeecord-velocity.53295/"
                    break
                }
            }
        }
    }

    fun stop() {
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdownNow()
        updaterScheduler.stopUpdaterCheckTask()
        updaterScheduler.stopConsoleNotificationTask()
    }

    private fun scheduleCheck() {
        updaterScheduler.scheduleUpdaterCheckTask({
            if(metaDataProperties.build.branch != "master") {
                // Check if target release is not published at SpigotMC
                checkIfPublishedAtSpigot()
                // Release is published, stop
                if (updateAvailable) {
                    updaterScheduler.stopUpdaterCheckTask()
                    printUpdateMessage()
                    scheduleConsoleNotificationTask()
                    return@scheduleUpdaterCheckTask
                }
            }

            // Check for updates
            getLatestBuildFromJenkins {latestBuildNumber ->
                if (latestBuildNumber == -1) return@getLatestBuildFromJenkins

                if(metaDataProperties.build.number.toInt() < latestBuildNumber) {
                    updaterScheduler.stopUpdaterCheckTask()
                    updateAvailable = true

                    updateDownloadURL = if(metaDataProperties.build.branch == "master") {
                        // Redirect to SpigotMC, because this build is a Release Build
                        "https://www.spigotmc.org/resources/enhanced-glist-bungeecord-velocity.53295/"
                    } else {
                        val project = metaDataProperties.build.project.split('/')
                        val rootJobName = project[0]
                        // Redirect to CI Server, because this build is a Beta Build
                        "https://ci.wirlie.net/job/$rootJobName/job/${metaDataProperties.build.branch}/lastSuccessfulBuild/"
                    }

                    // Update found
                    printUpdateMessage()
                    scheduleConsoleNotificationTask()
                }
            }
        }, checkInterval)
    }

    private fun printUpdateMessage() {
        logger.warning("======================= UPDATE AVAILABLE =======================")
        logger.warning("[Updater] A new update is available.")
        logger.warning("[Updater] Download the latest update from:")
        logger.warning("[Updater] $updateDownloadURL")
        logger.warning("=================================================================")
    }

    private fun scheduleConsoleNotificationTask() {
        if (consoleNotification) {
            // Only notify to console periodically if enabled
            updaterScheduler.scheduleConsoleNotificationTask({
                printUpdateMessage()
            }, consoleNotificationInterval)
        }
    }

}
