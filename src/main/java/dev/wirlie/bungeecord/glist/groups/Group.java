package dev.wirlie.bungeecord.glist.groups;

import java.util.Objects;

public class Group implements Comparable<Group> {

	private String id;
	private int weight = 0;
	private String prefix = null;
	private String prefixPermission = null;
	private boolean visibleToUsers = true;
	private String nameColor = "&7";
	private boolean defaultGroup = false;

	public Group(String id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPrefixPermission(String prefixPermission) {
		this.prefixPermission = prefixPermission;
	}

	public void setVisibleToUsers(boolean visibleToUsers) {
		this.visibleToUsers = visibleToUsers;
	}

	public void setNameColor(String nameColor) {
		this.nameColor = nameColor;
	}

	public void setDefaultGroup(boolean defaultGroup) {
		this.defaultGroup = defaultGroup;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPrefixPermission() {
		return prefixPermission;
	}

	public boolean isVisibleToUsers() {
		return visibleToUsers;
	}

	public String getNameColor() {
		return nameColor;
	}

	public boolean isDefaultGroup() {
		return defaultGroup;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Group) {
			Group o = (Group) obj;
			return o.getId().equals(id);
		}

		return super.equals(obj);
	}

	@Override
	public int compareTo(Group o) {
		return Integer.compare(weight, o.getWeight());
	}
}
