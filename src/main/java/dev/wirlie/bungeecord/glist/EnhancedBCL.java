package dev.wirlie.bungeecord.glist;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dev.wirlie.bungeecord.glist.activity.ActivityManager;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.config.ConfigEntry;
import dev.wirlie.bungeecord.glist.executor.GlistCommand;
import dev.wirlie.bungeecord.glist.executor.EBLCommand;
import dev.wirlie.bungeecord.glist.hooks.GroupHook;
import dev.wirlie.bungeecord.glist.hooks.InternalGroupSystemHook;
import dev.wirlie.bungeecord.glist.hooks.LuckPermsHook;
import dev.wirlie.bungeecord.glist.servers.ServerGroup;
import dev.wirlie.bungeecord.glist.updater.UpdateNotifyListener;
import dev.wirlie.bungeecord.glist.updater.UpdateChecker;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnhancedBCL extends Plugin {

	public static final List<ConfigEntry<?>> CONFIGURATIONS_REGISTRY = new ArrayList<>();

	private Configuration config = null;
	private ConfigurationProvider yamlProvider = null;
	private File configFile = null;
	private GlistCommand commandExecutor = null;

	private final List<GroupHook> groupHooks = new ArrayList<>();
	private final List<ServerGroup> serverGroups = new ArrayList<>();

	private final ActivityManager activityManager = new ActivityManager();

	public boolean isPremiumVanishHooked = false;

	private ScheduledTask registerGlistCommandTask;
	private final Object registerGlistCommandTaskSyncObject = new Object();

	//TODO: Implement update check every X minutes
	private UpdateChecker updateChecker;

	private BungeeAudiences adventure;

	public final static LegacyComponentSerializer defaultLegacyDeserializer = LegacyComponentSerializer
	.builder()
	.hexCharacter('#')
	.character('&')
	.extractUrls()
	.hexColors()
	.build();

	public @NotNull BungeeAudiences adventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
		}

		return this.adventure;
	}

	public void onEnable() {
		this.adventure = BungeeAudiences.create(this);

		//declaration of commons variables
		Logger logger = getLogger();
		PluginManager pm = BungeeCord.getInstance().getPluginManager();
        yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

		logger.info("Enabling plugin ...");
		configFile = new File(this.getDataFolder() + File.separator + "Config.yml");

		logger.info("Preparing and validating configuration ...");
		try {
			//make initialization of static variables...
			//noinspection InstantiationOfUtilityClass
			new Config();

			prepareConfig();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		logger.info("Registering /ebl command ...");

		logger.info("Registering /ebl command ...");
		pm.registerCommand(this, new EBLCommand(this));

		registerListExecutor(true);

		if(Config.UPDATES__CHECK_UPDATES.get()) {
			updateChecker = new UpdateChecker(this);
			updateChecker.checkForUpdates(
				true,
				version -> {
					getLogger().info("-------------------------------------------");
					getLogger().info("Remote version (SpigotMC): " + version);
					getLogger().info("Current version (Plugin): " + getDescription().getVersion());
					getLogger().info("-------------------------------------------");

					pm.registerListener(this, new UpdateNotifyListener(this));
				},
				ex -> {
					getLogger().warning("-------------------------------------------");
					getLogger().log(Level.WARNING, "Cannot check for updates due an internal error:", ex);
					getLogger().warning("-------------------------------------------");
				}
			);
		}

		BungeeCord.getInstance().registerChannel("ebcl:bridge");
		pm.registerListener(this, new BridgeListener(this));

		if(pm.getPlugin("PremiumVanish") != null) {
			isPremiumVanishHooked = true;
			getLogger().info("Hooked into PremiumVanish!");
			PremiumVanishListener listener = new PremiumVanishListener(this);
			pm.registerListener(this, listener);
			listener.initialHandle();
		}

		//request states from all servers...
		for(ServerInfo server : BungeeCord.getInstance().getServers().values()) {
			try {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(bout);
				out.writeUTF("send_all_players_to_bungeecord");
				out.close();
				bout.close();
				server.sendData("ebcl:bridge", bout.toByteArray(), false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onDisable() {
		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
	}

	public List<GroupHook> getGroupHooks() {
		return groupHooks;
	}

	public ConfigurationProvider getYamlProvider() {
		return yamlProvider;
	}

	private void prepareConfig() throws IOException {
		getLogger().info("Starting verification of " + CONFIGURATIONS_REGISTRY.size() + " configurations...");

		if(!configFile.exists()) {
			File parent = configFile.getParentFile();

			if(!parent.exists()) {
				parent.mkdirs();
			}

			Files.copy(getClass().getResourceAsStream("/Config.yml"), configFile.toPath());
		}

		config = yamlProvider.load(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));

		Configuration defaultConfiguration = yamlProvider.load(new InputStreamReader(getClass().getResourceAsStream("/Config.yml"), StandardCharsets.UTF_8));

		boolean shouldSave = false;
		for(ConfigEntry<?> pendingResolution : CONFIGURATIONS_REGISTRY) {
			if(!config.contains(pendingResolution.getKey()) || config.get(pendingResolution.getKey()) == null) {
				getLogger().warning("Missing configuration [" + pendingResolution.getKey() + "], trying to get fallback value...");

				//get fallback value
				Object fallbackValue = defaultConfiguration.get(pendingResolution.getKey());

				if(fallbackValue == null) {
					throw new IOException("Cannot get default value of [" + pendingResolution.getKey() + "] from default Configuration...");
				}

				config.set(pendingResolution.getKey(), fallbackValue);
				pendingResolution.setValue(fallbackValue);
				shouldSave = true;
			} else {
				pendingResolution.setValue(config.get(pendingResolution.getKey()));
			}
		}

		if(shouldSave) {
			saveConfig();
		}

		//hooks
		groupHooks.clear();

		if(Config.BEHAVIOUR__GROUPS_PREFIX__ENABLE.get()) {
			if (Config.BEHAVIOUR__GROUPS_PREFIX__USE__LUCKPERMS.get() && BungeeCord.getInstance().getPluginManager().getPlugin("LuckPerms") != null) {
				getLogger().info("LuckPerms Hooked!");
				groupHooks.add(new LuckPermsHook());
			}

			if (Config.BEHAVIOUR__GROUPS_PREFIX__USE__INTERNAL_GROUP_SYSTEM.get()) {
				getLogger().info("Enabling internal Group System");
				groupHooks.add(new InternalGroupSystemHook(this));
			}

			//sort group hooks by priority
			groupHooks.sort((v1, v2) -> Integer.compare(v2.getPriority(), v1.getPriority()));

			if (groupHooks.isEmpty()) {
				getLogger().warning("Player prefixes are enabled but LuckPerms and the internal Group System are disabled, so... player prefixes will be disabled.");
			}
		}

		//server groups
		serverGroups.clear();

		for(Map<String, Object> configuration : Config.SERVERS__GROUPS.get()) {
			String id = null;
			List<String> serversIds = null;

			for(Map.Entry<String, Object> entry : configuration.entrySet()) {
				if(entry.getKey().equalsIgnoreCase("group-id")) {
					id = (String) entry.getValue();
				} else if(entry.getKey().equalsIgnoreCase("servers")) {
					//noinspection unchecked
					serversIds = (List<String>) entry.getValue();
				}
			}

			if(id == null || serversIds == null) {
				getLogger().warning("Not valid server group found! Missing id or server list...");
				getLogger().warning("Full map: {" + (configuration.entrySet().stream().map((e) -> "[k=" + e.getKey() + ",v=" + e.getValue() + "]").collect(Collectors.joining(","))) + "}");
				continue;
			}

			BungeeCord bc = BungeeCord.getInstance();
			List<ServerInfo> matchedServers = new ArrayList<>();

			for(String sid : serversIds) {
				ServerInfo server = bc.getServerInfo(sid);

				if(server == null) {
					continue;
				}

				matchedServers.add(server);
			}

			if(matchedServers.isEmpty()) {
				continue;
			}

			String finalId = id;
			if(serverGroups.stream().anyMatch(s -> s.getId().equalsIgnoreCase(finalId))) {
				getLogger().warning("Duplicated server group [id=" + finalId + "], skipping duplicated definition...");
				continue;
			}

			getLogger().info("New server group found, id=" + id + " with " + matchedServers.size() + " servers");
			ServerGroup group = new ServerGroup(id);
			group.setServers(matchedServers);
			serverGroups.add(group);
		}
	}

	public void reloadConfig() throws IOException {
		prepareConfig();
		groupHooks.forEach(GroupHook::reload);
		registerListExecutor(false);
		commandExecutor.reload();
	}

	private void saveConfig() {
		if (config != null) {
			try {
				yamlProvider.save(config, new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void registerListExecutor(boolean firstRegister) {
		if (commandExecutor != null) {
			BungeeCord.getInstance().getPluginManager().unregisterCommand(commandExecutor);
		}

		//read configuration
		String label = Config.COMMAND__GLIST__LABEL.get();
		getLogger().info("Loading /" + label + " command ...");
		String permission = Config.COMMAND__GLIST__PERMISSION.get();
		List<String> aliasesList = Config.COMMAND__GLIST__ALIASES.get();
		String[] aliases = aliasesList.toArray(new String[0]);

		//prepare enhanced list executor
		commandExecutor = new GlistCommand(this, label, permission, aliases);

		if (firstRegister && label.equalsIgnoreCase("glist")) {
			//we need to wait some time so the original /glist can be replaced.
			//if proxy have players we can consider that EBL was loaded using a plugin manager, so no waiting required to register the /glist command...
			if(!ProxyServer.getInstance().getPlayers().isEmpty()) {
				//immediately register
				BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor);
			} else {
				//wait for the next player connection, or 30s if no player was connected
				synchronized (registerGlistCommandTaskSyncObject) {
					registerGlistCommandTask = BungeeCord.getInstance().getScheduler().schedule(this, () -> {
						synchronized (registerGlistCommandTaskSyncObject) {
							registerGlistCommandTask = null;
						}
						BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor);
						getLogger().info("Command /glist registered...");
					}, 30, TimeUnit.SECONDS);
				}

				ProxyServer.getInstance().getPluginManager().registerListener(this, new RegisterGlistListener());
			}
		} else {
			//otherwise, custom commands should no clash to other commands (except if the user defines a command of other plugin)
			BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor);
			getLogger().info("Command /" + label + " registered...");
		}
	}

	@Nullable
	public String getPrefix(ProxiedPlayer player) {
		for(GroupHook hook : groupHooks) {
			String tryPrefix = hook.getPrefix(player);

			if(tryPrefix != null) {
				String sanitizedPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', tryPrefix));

				if(sanitizedPrefix.endsWith(" ")) {
					int lastSpace = tryPrefix.lastIndexOf(' ');
					if(lastSpace == tryPrefix.length() - 1) {
						tryPrefix = tryPrefix.substring(0, tryPrefix.length() - 1);
					} else {
						tryPrefix = tryPrefix.substring(0, lastSpace) + tryPrefix.substring(lastSpace + 1, tryPrefix.length());
					}
				} else if(tryPrefix.isEmpty()) {
					continue;
				}

				return ChatColor.translateAlternateColorCodes('&', tryPrefix);
			}
		}

		return null;
	}

	public List<ServerGroup> getServerGroups() {
		return serverGroups;
	}

	public boolean isInGroup(ServerInfo server) {
		return serverGroups.stream().flatMap(sg -> sg.getServers().stream()).anyMatch(s -> s.getName().equalsIgnoreCase(server.getName()));
	}

	public ActivityManager getActivityManager() {
		return activityManager;
	}

	private class RegisterGlistListener implements Listener {

		@EventHandler
		public void event(PostLoginEvent e) {
			synchronized (registerGlistCommandTaskSyncObject) {
				if (registerGlistCommandTask != null) {
					registerGlistCommandTask.cancel();
					registerGlistCommandTask = null;

					BungeeCord.getInstance().getPluginManager().registerCommand(EnhancedBCL.this, commandExecutor);
					getLogger().info("Command /glist registered...");
				}
			}

			ProxyServer.getInstance().getPluginManager().unregisterListener(this);
		}

	}

}
