package dev.wirlie.bungeecord.glist;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.config.ConfigEntry;
import dev.wirlie.bungeecord.glist.executor.GlistCommand;
import dev.wirlie.bungeecord.glist.executor.EBLCommand;
import dev.wirlie.bungeecord.glist.groups.GroupManager;
import dev.wirlie.bungeecord.glist.updater.UpdateNotifyListener;
import dev.wirlie.bungeecord.glist.updater.UpdateChecker;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class EnhancedBCL extends Plugin {

	public static final List<ConfigEntry<?>> CONFIGURATIONS_REGISTRY = new ArrayList<>();

	private Configuration config = null;
	private ConfigurationProvider yamlProvider = null;
	private File configFile = null;
	private GlistCommand commandExecutor = null;
	private GroupManager groupManager;

	public void onEnable() {
		//declaration of commons variables
		Logger logger = getLogger();
		PluginManager pm = BungeeCord.getInstance().getPluginManager();
        yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

		logger.info("Enabling plugin ...");
		configFile = new File(this.getDataFolder() + File.separator + "Config.yml");

		logger.info("Preparing and validating configuration ...");
		try {
			prepareConfig();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		logger.info("Registering /ebl command ...");
		pm.registerCommand(this, new EBLCommand(this));

		registerListExecutor(true);
		groupManager = new GroupManager(this);

		if(Config.UPDATES__CHECK_UPDATES.get()) {
			UpdateChecker updateChecker = new UpdateChecker(this);
			updateChecker.getSpigotVersion(v -> {}, Throwable::printStackTrace);

			pm.registerListener(this, new UpdateNotifyListener(this));
		}
	}

	public ConfigurationProvider getYamlProvider() {
		return yamlProvider;
	}

	private void prepareConfig() throws IOException {
		if(!configFile.exists()) {
			Files.copy(getClass().getResourceAsStream("/Config.yml"), configFile.toPath());
		}

		config = yamlProvider.load(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));

		Configuration defaultConfiguration = yamlProvider.load(new InputStreamReader(getClass().getResourceAsStream("/Config.yml"), StandardCharsets.UTF_8));

		boolean shouldSave = false;
		for(ConfigEntry<?> pendingResolution : CONFIGURATIONS_REGISTRY) {
			if(!config.contains(pendingResolution.getKey()) || config.get(pendingResolution.getKey()) == null) {
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
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public void reloadConfig() throws IOException {
		prepareConfig();

		registerListExecutor(false);
		getGroupManager().loadGroups();
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
		commandExecutor = new GlistCommand(label, permission, aliases);

		if (firstRegister && label.equalsIgnoreCase("glist")) {
			//TODO: Probably this can be removed if we declare the cmd_glist plugin as soft dependency, so cmd_glist should be loaded before EnhancedBungeeList...
			//we need to wait some seconds so the original /glist can be replaced.
			BungeeCord.getInstance().getScheduler().schedule(this, () -> {
				BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor);
				getLogger().info("Command /glist registered...");
			}, 3, TimeUnit.SECONDS);
		} else {
			//otherwise, custom commands should no clash to other commands (except if the user defines a command of other plugin)
			BungeeCord.getInstance().getPluginManager().registerCommand(this, commandExecutor);
			getLogger().info("Command /" + label + " registered...");
		}
	}

}
