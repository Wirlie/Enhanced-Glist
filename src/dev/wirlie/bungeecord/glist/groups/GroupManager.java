package dev.wirlie.bungeecord.glist.groups;

import dev.wirlie.bungeecord.glist.EnhancedBCL;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class GroupManager {

	private final List<Group> groups = new ArrayList<>();

	private final File groupFile;
	private final EnhancedBCL plugin;

	public GroupManager(EnhancedBCL plugin) {
		this.groupFile = new File(plugin.getDataFolder(), "GroupsPrefix.yml");
		this.plugin = plugin;
	}

	public void loadGroups() {
		if(!groupFile.exists()) {
			plugin.getLogger().info("File not found: GroupsPrefix.yml | Generating file...");
			File parentFolder = groupFile.getParentFile();
			if(!parentFolder.exists()) {
				parentFolder.mkdirs();
			}
			try {
				Files.copy(getClass().getResourceAsStream("/GroupsPrefix.yml"), groupFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(!groupFile.exists()) {
			plugin.getLogger().warning("File: GroupsPrefix.yml not found, check folder permissions, plugin cannot create default file...");
			return;
		}

		plugin.getLogger().info("GroupsPrefix.yml found, reading configuration ...");

		try {
			groups.clear();

			Configuration config = plugin.getYamlProvider().load(new InputStreamReader(new FileInputStream(groupFile), StandardCharsets.UTF_8));
			String staffGroupPermission = config.getString("staff-group.permission", null);

			if(staffGroupPermission == null) {
				plugin.getLogger().warning("No permission defined for staff members. Using default permission: 'ebl.groups.staff'");
				staffGroupPermission = "ebl.groups.staff";
			}

			if(!config.contains("groups")) {
				plugin.getLogger().warning("No groups defined. Adding default group for users.");
				Group group = new Group("default");
				group.setDefaultGroup(true);
				group.setNameColor("&7");
				groups.add(group);
				return;
			}

			try {
				@SuppressWarnings("unchecked")
				List<Map<?, ?>> allData = (List<Map<?, ?>>) config.get("groups");
				for(Map<?,?> groupData : allData) {
					try {
						String id = null;
						int weight = 0;
						String prefix = null;
						String prefixPermission = null;
						boolean prefixVisibleToUsers = true;
						String nameColor = "";
						boolean defaultGroup = false;

						for (Map.Entry<?, ?> entry : groupData.entrySet()) {
							String key = entry.getKey().toString();
							String value = entry.getValue().toString();

							if (key.equalsIgnoreCase("id")) {
								id = value;
							} else if (key.equalsIgnoreCase("weight")) {
								try {
									weight = Integer.parseInt(value);
								} catch (NumberFormatException e) {
									plugin.getLogger().warning("Unexpected weight value: " + value);
								}
							} else if (key.equalsIgnoreCase("prefix")) {
								prefix = value;
							} else if (key.equalsIgnoreCase("prefix-permission")) {
								prefixPermission = value;
							} else if(key.equalsIgnoreCase("prefix-visible-to-users")) {
								prefixVisibleToUsers = Boolean.parseBoolean(value);
							} else if(key.equalsIgnoreCase("name-color")) {
								nameColor = value;
							} else if(key.equalsIgnoreCase("default-group")) {
								try {
									defaultGroup = Boolean.parseBoolean(value);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

						if(id == null) {
							plugin.getLogger().warning("Group not loaded: id must be defined");
						} else {
							Group group = new Group(id);
							group.setDefaultGroup(defaultGroup);
							group.setWeight(weight);
							group.setPrefix(prefix);
							group.setPrefixPermission(prefixPermission);
							group.setVisibleToUsers(prefixVisibleToUsers);
							group.setNameColor(nameColor);

							if(groups.contains(group)) {
								plugin.getLogger().warning("Group '" + group.getId() + "' already loaded! Fixing id to: '" + group.getId() + "~1'");
								group.setId(id + "~1");
							}

							int fixedId = 1;
							while(groups.contains(group)) {
								fixedId++;
								plugin.getLogger().warning("Group '" + group.getId() + "' already loaded! Fixing id to: '" + id + "~" + fixedId + "'");
								group.setId(id + "~" + fixedId);
							}

							plugin.getLogger().info("Group '" + group.getId() + "' loaded. Weight: " + group.getWeight() + ", permission: " + group.getPrefixPermission());

							groups.add(group);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ClassCastException e) {
				plugin.getLogger().log(Level.SEVERE, "Cannot read GroupsPrefix.yml (ClassCastException)", e);
			}

			groups.sort(Comparator.naturalOrder());
			plugin.getLogger().info(groups.size() + " groups loaded.");
			plugin.getLogger().info(groups.stream().map(Group::getId).collect(Collectors.joining(", ", "[", "]")));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Cannot read GroupsPrefix.yml (IOException)", e);
		}
	}

	public Optional<Group> getGroup(ProxiedPlayer player) {
		Group defaultGroup = null;

		for(Group group : groups) {
			if(group.isDefaultGroup()) {
				defaultGroup = group;
				continue;
			}

			if(group.getPrefixPermission() == null || group.getPrefixPermission().isEmpty() || player.hasPermission(group.getPrefixPermission())) {
				return Optional.of(group);
			}
		}

		return Optional.ofNullable(defaultGroup);
	}
}
