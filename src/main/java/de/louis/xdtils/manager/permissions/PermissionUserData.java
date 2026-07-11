package de.louis.xdtils.manager.permissions;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PermissionUserData {

    private final UUID uuid;
    private final Set<String> permissions = new LinkedHashSet<>();
    private final Set<String> groups = new LinkedHashSet<>();

    public PermissionUserData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void addPermission(String permission) {
        permissions.add(normalize(permission));
    }

    public void removePermission(String permission) {
        permissions.remove(normalize(permission));
    }

    public void addGroup(String group) {
        groups.add(group.toLowerCase(Locale.ROOT));
    }

    public void removeGroup(String group) {
        groups.remove(group.toLowerCase(Locale.ROOT));
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(normalize(permission));
    }

    private String normalize(String permission) {
        return permission == null ? "" : permission.trim().toLowerCase(Locale.ROOT);
    }
}