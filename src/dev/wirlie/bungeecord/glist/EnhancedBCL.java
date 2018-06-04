package dev.wirlie.bungeecord.glist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dev.wirlie.bungeecord.glist.groups.GroupManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class EnhancedBCL extends Plugin {
	private Configuration config = null;
	private ConfigurationProvider yamlProvider = null;
	private File configFile = null;
	private ListExecutor commandExecutor = null;
	private GroupManager groupManager;

	public void onEnable() {
		this.getLogger().info("Enabling plugin ...");
		this.yamlProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
		this.configFile = new File(this.getDataFolder() + File.separator + "Config.yml");

		getLogger().info("Validating Config.yml ...");
		ConfigurationValidator.validate(this);

		BungeeCord bc = BungeeCord.getInstance();

		this.getLogger().info("Registering Plugin Command ...");
		bc.getPluginManager().registerCommand(this, new PluginExecutor(this));
		this.getLogger().info("Registering Enhanced List Command ...");
		String label = this.getConfig().getString("command.global-list.label", "glist");
		String permission = this.getConfig().getString("command.global-list.permission", null);
		List<String> aliasesList = this.getConfig().getStringList("command.global-list.aliases");
		String[] aliases = aliasesList.toArray(new String[aliasesList.size()]);
		this.commandExecutor = new ListExecutor(this, label, permission, aliases);
		this.getLogger().info("Enhanced List Command loaded ...");
		this.getLogger().info("Label: /" + label + " | Aliases: " + aliasesList.toString());

		this.groupManager = new GroupManager(this);

		BungeeCord.getInstance().getScheduler().schedule(this, () -> {
			this.getLogger().info("Enhanced List Command Registered!");
			bc.getPluginManager().registerCommand(this, this.commandExecutor);
		}, 5L, TimeUnit.SECONDS);
	}

	public GroupManager getGroupManager() {
		return groupManager;
	}

	public void unloadConfig() {
		this.config = null;
	}

	public Configuration getConfig() {
		if (this.config == null) {
			this.loadConfig();
		}

		return this.config;
	}

	public void reloadConfig() {
		this.config = null;
		if (this.commandExecutor != null) {
			BungeeCord.getInstance().getPluginManager().unregisterCommand(this.commandExecutor);
		}

		String label = this.getConfig().getString("command.global-list.label", "glist");
		String permission = this.getConfig().getString("command.global-list.permission", null);
		List<String> aliasesList = this.getConfig().getStringList("command.global-list.aliases");
		String[] aliases = aliasesList.toArray(new String[aliasesList.size()]);
		this.commandExecutor = new ListExecutor(this, label, permission, aliases);
		BungeeCord.getInstance().getPluginManager().registerCommand(this, this.commandExecutor);

		getGroupManager().loadGroups();
	}

	@SuppressWarnings("unused")
	public void saveConfig() {
		if (this.config != null) {
			try {
				this.yamlProvider.save(this.config, this.configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void loadConfig() {
		if (!this.configFile.exists()) {
			if (!this.configFile.getParentFile().mkdirs()) {
				getLogger().warning("Cannot make parent dir: " + configFile.getParentFile().getAbsolutePath());
			}

			try {
				InputStream in = this.getResourceAsStream("Config.yml");

				try {
					Files.copy(in, this.configFile.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						in.close();
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			this.config = this.yamlProvider.load(new FileReader(this.configFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public File getConfigFile() {
		return configFile;
	}

	public Configuration loadConfiguration(File file) throws IOException {
		return yamlProvider.load(file);
	}

	public void saveConfiguration(Configuration configuration, File file) throws IOException {
		yamlProvider.save(configuration, file);
	}

}