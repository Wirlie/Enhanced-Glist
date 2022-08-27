package dev.wirlie.bungeecord.glist

import dev.wirlie.bungeecord.glist.activity.ActivityManager
import dev.wirlie.bungeecord.glist.config.Config
import dev.wirlie.bungeecord.glist.config.ConfigEntry
import dev.wirlie.bungeecord.glist.executor.EBLCommand
import dev.wirlie.bungeecord.glist.executor.GlistCommand
import dev.wirlie.bungeecord.glist.hooks.GroupHook
import dev.wirlie.bungeecord.glist.hooks.InternalGroupSystemHook
import dev.wirlie.bungeecord.glist.hooks.LuckPermsHook
import dev.wirlie.bungeecord.glist.servers.ServerGroup
import dev.wirlie.bungeecord.glist.updater.UpdateChecker
import dev.wirlie.bungeecord.glist.updater.UpdateNotifyListener
import dev.wirlie.bungeecord.glist.util.Pair
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.ScheduledTask
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.logging.Level
import java.util.stream.Collectors

class EnhancedBCL : Plugin() {
    private lateinit var config: Configuration
    var yamlProvider: ConfigurationProvider? = null
        private set

    private lateinit var configFile: File
    private val groupHooks: MutableList<GroupHook> = ArrayList()
    private val serverGroups: MutableList<ServerGroup> = ArrayList()

    val activityManager = ActivityManager()
	var isPremiumVanishHooked = false

    //TODO: Implement update check every X minutes
    private var updateChecker: UpdateChecker? = null
    private var adventure: BungeeAudiences? = null

    fun adventure(): BungeeAudiences {
        checkNotNull(adventure) { "Cannot retrieve audience provider when plugin is not enabled" }
        return adventure!!
    }

    override fun onEnable() {
        INSTANCE = this
        adventure = BungeeAudiences.create(this)

        //declaration of commons variables
        val logger = logger
        val pm = BungeeCord.getInstance().getPluginManager()

        yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
        logger.info("Enabling plugin ...")

        configFile = File(this.dataFolder.toString() + File.separator + "Config.yml")
        logger.info("Preparing and validating configuration ...")

        try {
            //make initialization of static variables...
            prepareConfig()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        logger.info("Registering /ebl command ...")
        logger.info("Registering /ebl command ...")
        pm.registerCommand(this, EBLCommand(this))
        registerListExecutor(true)

        if (Config.UPDATES__CHECK_UPDATES.get()) {
            updateChecker = UpdateChecker(this)
            updateChecker!!.checkForUpdates(
                true,
                { result: Pair<String, Boolean> ->
                    getLogger().info("-------------------------------------------")
                    getLogger().info("Remote version (SpigotMC): " + result.a)
                    getLogger().info("Current version (Plugin): " + description.version)
                    getLogger().info("-------------------------------------------")
                    if (result.b) {
                        pm.registerListener(this, UpdateNotifyListener(this))
                    }
                }
            ) { ex: Throwable? ->
                getLogger().warning("-------------------------------------------")
                getLogger().log(Level.WARNING, "Cannot check for updates due an internal error:", ex)
                getLogger().warning("-------------------------------------------")
            }
        }

        BungeeCord.getInstance().registerChannel("ebcl:bridge")
        pm.registerListener(this, BridgeListener(this))

        if (pm.getPlugin("PremiumVanish") != null) {
            isPremiumVanishHooked = true
            getLogger().info("Hooked into PremiumVanish!")
            val listener = PremiumVanishListener(this)
            pm.registerListener(this, listener)
            listener.initialHandle()
        }

        //request states from all servers...
        for (server in BungeeCord.getInstance().servers.values) {
            try {
                val bout = ByteArrayOutputStream()
                val out = DataOutputStream(bout)
                out.writeUTF("send_all_players_to_bungeecord")
                out.close()
                bout.close()
                server.sendData("ebcl:bridge", bout.toByteArray(), false)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onDisable() {
        if (adventure != null) {
            adventure!!.close()
            adventure = null
        }
    }

    fun getGroupHooks(): List<GroupHook> {
        return groupHooks
    }

    @Throws(IOException::class)
    private fun prepareConfig() {
        logger.info("Starting verification of " + CONFIGURATIONS_REGISTRY.size + " configurations...")

        if (!configFile.exists()) {
            val parent = configFile.parentFile
            if (!parent.exists()) {
                parent.mkdirs()
            }
            Files.copy(javaClass.getResourceAsStream("/Config.yml")!!, configFile.toPath())
        }

        config = yamlProvider!!.load(InputStreamReader(FileInputStream(configFile), StandardCharsets.UTF_8))
        val defaultConfiguration =
            yamlProvider!!.load(InputStreamReader(javaClass.getResourceAsStream("/Config.yml")!!, StandardCharsets.UTF_8))

        var shouldSave = false
        for (pendingResolution in CONFIGURATIONS_REGISTRY) {
            if (!config.contains(pendingResolution.key) || config.get(pendingResolution.key) == null) {
                logger.warning("Missing configuration [" + pendingResolution.key + "], trying to get fallback value...")

                //get fallback value
                val fallbackValue = defaultConfiguration[pendingResolution.key]
                    ?: throw IOException("Cannot get default value of [" + pendingResolution.key + "] from default Configuration...")
                config.set(pendingResolution.key, fallbackValue)
                pendingResolution.setValue(fallbackValue)
                shouldSave = true
            } else {
                pendingResolution.setValue(config.get(pendingResolution.key))
            }
        }
        if (shouldSave) {
            saveConfig()
        }

        //hooks
        groupHooks.clear()
        if (Config.BEHAVIOUR__GROUPS_PREFIX__ENABLE.get()) {
            if (Config.BEHAVIOUR__GROUPS_PREFIX__USE__LUCKPERMS.get() && BungeeCord.getInstance().getPluginManager()
                    .getPlugin("LuckPerms") != null
            ) {
                logger.info("LuckPerms Hooked!")
                groupHooks.add(LuckPermsHook())
            }

            if (Config.BEHAVIOUR__GROUPS_PREFIX__USE__INTERNAL_GROUP_SYSTEM.get()) {
                logger.info("Enabling internal Group System")
                groupHooks.add(InternalGroupSystemHook(this))
            }

            //sort group hooks by priority
            // TODO: Validar que este sort si sea correcto, no sé si es ascendente o descendente...
            groupHooks.sortBy { it.priority }

            if (groupHooks.isEmpty()) {
                logger.warning("Player prefixes are enabled but LuckPerms and the internal Group System are disabled, so... player prefixes will be disabled.")
            }
        }

        //server groups
        serverGroups.clear()
        for (configuration in Config.SERVERS__GROUPS.get()) {
            var id: String? = null
            var serversIds: List<String?>? = null

            for ((key, value) in configuration) {
                if (key.equals("group-id", ignoreCase = true)) {
                    id = value as String
                } else if (key.equals("servers", ignoreCase = true)) {
                    @Suppress("UNCHECKED_CAST")
                    serversIds = value as List<String?>
                }
            }

            if (id == null || serversIds == null) {
                logger.warning("Not valid server group found! Missing id or server list...")
                logger.warning(
                    "Full map: {" + configuration.entries.stream()
                        .map { (key, value): Map.Entry<String, Any> -> "[k=$key,v=$value]" }
                        .collect(Collectors.joining(",")) + "}")
                continue
            }

            val bc = BungeeCord.getInstance()
            val matchedServers: MutableList<ServerInfo> = ArrayList()

            for (sid in serversIds) {
                val server = bc.getServerInfo(sid) ?: continue
                matchedServers.add(server)
            }

            if (matchedServers.isEmpty()) {
                continue
            }

            val finalId: String = id
            if (serverGroups.stream().anyMatch { s: ServerGroup -> s.id.equals(finalId, ignoreCase = true) }) {
                logger.warning("Duplicated server group [id=$finalId], skipping duplicated definition...")
                continue
            }

            logger.info("New server group found, id=" + id + " with " + matchedServers.size + " servers")
            val group = ServerGroup(id)
            group.servers = matchedServers
            serverGroups.add(group)
        }
    }

    @Throws(IOException::class)
    fun reloadConfig() {
        prepareConfig()
        groupHooks.forEach(Consumer { obj: GroupHook -> obj.reload() })
        registerListExecutor(false)
        commandExecutor!!.reload()
    }

    private fun saveConfig() {
        if (this::config.isInitialized) {
            try {
                yamlProvider!!.save(config, OutputStreamWriter(FileOutputStream(configFile), StandardCharsets.UTF_8))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun registerListExecutor(firstRegister: Boolean) {
        if (commandExecutor != null) {
            BungeeCord.getInstance().getPluginManager().unregisterCommand(commandExecutor)
        }

        //read configuration
        val label = Config.COMMAND__GLIST__LABEL.get()
        logger.info("Loading /$label command ...")
        val permission = Config.COMMAND__GLIST__PERMISSION.get()
        val aliasesList = Config.COMMAND__GLIST__ALIASES.get()
        val aliases = aliasesList.toTypedArray()

        //prepare enhanced list executor
        commandExecutor = GlistCommand(this, label, permission, *aliases)
        if (firstRegister && label.equals("glist", ignoreCase = true)) {
            //we need to wait some time so the original /glist can be replaced.
            //if proxy have players we can consider that EBL was loaded using a plugin manager, so no waiting required to register the /glist command...
            if (!ProxyServer.getInstance().players.isEmpty()) {
                //immediately register
                BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor)
            } else {
                //wait for the next player connection, or 30s if no player was connected
                synchronized(registerGlistCommandTaskSyncObject) {
                    registerGlistCommandTask = BungeeCord.getInstance().scheduler.schedule(this, {
                        synchronized(registerGlistCommandTaskSyncObject) { registerGlistCommandTask = null }
                        BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor)
                        logger.info("Command /glist registered...")
                    }, 30, TimeUnit.SECONDS)
                }
                ProxyServer.getInstance().pluginManager.registerListener(this, RegisterGlistListener())
            }
        } else {
            //otherwise, custom commands should no clash to other commands (except if the user defines a command of other plugin)
            BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor)
            logger.info("Command /$label registered...")
        }
    }

    fun getPrefix(player: ProxiedPlayer): String? {
        for (hook in groupHooks) {
            var tryPrefix = hook.getPrefix(player)

            if (tryPrefix != null) {
                val sanitizedPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tryPrefix))

                if (sanitizedPrefix.endsWith(" ")) {
                    val lastSpace = tryPrefix.lastIndexOf(' ')
                    tryPrefix = if (lastSpace == tryPrefix.length - 1) {
                        tryPrefix.substring(0, tryPrefix.length - 1)
                    } else {
                        tryPrefix.substring(0, lastSpace) + tryPrefix.substring(lastSpace + 1, tryPrefix.length)
                    }
                } else if (tryPrefix.isEmpty()) {
                    continue
                }

                return ChatColor.translateAlternateColorCodes('&', tryPrefix)
            }
        }

        return null
    }

    fun getServerGroups(): List<ServerGroup> {
        return serverGroups
    }

    fun isInGroup(server: ServerInfo): Boolean {
        return serverGroups.stream().flatMap { sg: ServerGroup -> sg.servers.stream() }
            .anyMatch { s: ServerInfo -> s.name.equals(server.name, ignoreCase = true) }
    }

    companion object {
        lateinit var INSTANCE: EnhancedBCL
		val CONFIGURATIONS_REGISTRY: MutableList<ConfigEntry<*>> = ArrayList()
        var commandExecutor: GlistCommand? = null
        var registerGlistCommandTask: ScheduledTask? = null
        val registerGlistCommandTaskSyncObject = Any()
		val defaultLegacyDeserializer = LegacyComponentSerializer
            .builder()
            .hexCharacter('#')
            .character('&')
            .extractUrls()
            .hexColors()
            .build()
    }
}
