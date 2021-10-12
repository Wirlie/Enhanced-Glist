package dev.wirlie.bungeecord.glist;

import dev.wirlie.bungeecord.glist.activity.ActivityType;
import dev.wirlie.bungeecord.glist.config.Config;
import dev.wirlie.bungeecord.glist.util.PlayerGlistEntry;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemporalPaginator {
	private List<PlayerGlistEntry> data;
	private final int pageSize;
	private final long createdAt = System.currentTimeMillis();

	public TemporalPaginator(Collection<PlayerGlistEntry> data, int pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		}

		this.data = new ArrayList<>(data);
		this.pageSize = pageSize;
	}

	public List<PlayerGlistEntry> getFullPage(int page) {
		int totalPages = resolveNumOfPages(data);
		if (page >= 1 && page <= totalPages) {
			int index = page - 1;
			return this.data.stream().skip((long) index * this.pageSize).limit(this.pageSize).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public List<PlayerGlistEntry> getVisiblePage(int page, ProxiedPlayer player) {
		List<PlayerGlistEntry> data = getFullDataVisible(player);
		int totalPages = resolveNumOfPages(data);
		if (page >= 1 && page <= totalPages) {
			int index = page - 1;
			return data.stream().skip((long) index * this.pageSize).limit(this.pageSize).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public List<PlayerGlistEntry> getFullData() {
		return this.data;
	}

	public List<PlayerGlistEntry> getFullDataVisible(ProxiedPlayer player) {
		if(!Config.BEHAVIOUR__PLAYER_STATUS__VANISH__HIDE_VANISHED_USERS.get() || player.hasPermission(Config.BEHAVIOUR__PLAYER_STATUS__VANISH__BYPASS_PERMISSION.get())) {
			return this.data;
		}

		return this.data.stream().filter(d -> !d.getActivities().contains(ActivityType.VANISH)).collect(Collectors.toList());
	}

	public int resolveNumOfPages(List<PlayerGlistEntry> data) {
		return (int)Math.ceil((double)data.size() / (double)pageSize);
	}

	private long getCreatedAt() {
		return this.createdAt;
	}

	public boolean shouldUpdate(long millisToKeep) {
		return System.currentTimeMillis() - this.getCreatedAt() >= millisToKeep;
	}

	public void update(Collection<PlayerGlistEntry> data) {
		this.data = new ArrayList<>(data);
	}

	public int dataSize() {
		return this.data.size();
	}
}
