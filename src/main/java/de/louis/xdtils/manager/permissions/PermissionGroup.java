package de.louis.xdtils.manager.permissions;

import java.util.*;

public class PermissionGroup {

    private final String name;
    private String displayName;
    private int priority;
    private final Set<String> permissions = new LinkedHashSet<>();
    private final Set<String> inheritedGroups = new LinkedHashSet<>();

    public PermissionGroup(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.displayName = name;
        this.priority = 0;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<String> getInheritedGroups() {
        return inheritedGroups;
    }

    public void addPermission(String permission) {
        permissions.add(normalize(permission));
    }

    public void removePermission(String permission) {
        permissions.remove(normalize(permission));
    }

    public void addInheritedGroup(String group) {
        inheritedGroups.add(group.toLowerCase(Locale.ROOT));
    }

    public void removeInheritedGroup(String group) {
        inheritedGroups.remove(group.toLowerCase(Locale.ROOT));
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(normalize(permission));
    }

    private String normalize(String permission) {
        return permission == null ? "" : permission.trim().toLowerCase(Locale.ROOT);
    }
}