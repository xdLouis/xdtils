package de.louis.xdtils.manager.permissions;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PermissionSystemManager {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration config;

    private final Map<String, PermissionGroup> groups = new LinkedHashMap<>();
    private final Map<UUID, PermissionUserData> users = new LinkedHashMap<>();
    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    public PermissionSystemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "permissions.yml");
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte permissions.yml nicht erstellen.");
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        groups.clear();
        users.clear();

        loadGroups();
        loadUsers();
    }

    public void save() {
        config.set("groups", null);
        config.set("users", null);

        for (PermissionGroup group : groups.values()) {
            String base = "groups." + group.getName();
            config.set(base + ".displayName", group.getDisplayName());
            config.set(base + ".priority", group.getPriority());
            config.set(base + ".permissions", new ArrayList<>(group.getPermissions()));
            config.set(base + ".inherits", new ArrayList<>(group.getInheritedGroups()));
        }

        for (PermissionUserData user : users.values()) {
            String base = "users." + user.getUuid();
            config.set(base + ".permissions", new ArrayList<>(user.getPermissions()));
            config.set(base + ".groups", new ArrayList<>(user.getGroups()));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte permissions.yml nicht speichern.");
        }
    }

    private void loadGroups() {
        ConfigurationSection section = config.getConfigurationSection("groups");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String normalizedKey = key.toLowerCase(Locale.ROOT);

            PermissionGroup group = new PermissionGroup(normalizedKey);
            group.setDisplayName(config.getString("groups." + key + ".displayName", key));
            group.setPriority(config.getInt("groups." + key + ".priority", 0));

            for (String perm : config.getStringList("groups." + key + ".permissions")) {
                group.addPermission(normalizeNodeInput(perm));
            }

            for (String inherit : config.getStringList("groups." + key + ".inherits")) {
                group.addInheritedGroup(inherit.toLowerCase(Locale.ROOT));
            }

            groups.put(normalizedKey, group);
        }
    }

    private void loadUsers() {
        ConfigurationSection section = config.getConfigurationSection("users");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PermissionUserData data = new PermissionUserData(uuid);

                for (String perm : config.getStringList("users." + key + ".permissions")) {
                    data.addPermission(normalizeNodeInput(perm));
                }

                for (String group : config.getStringList("users." + key + ".groups")) {
                    data.addGroup(group.toLowerCase(Locale.ROOT));
                }

                users.put(uuid, data);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("permissions-system.enabled", true);
    }

    public Map<String, PermissionGroup> getGroups() {
        return groups;
    }

    public Collection<PermissionUserData> getUsers() {
        return users.values();
    }

    public PermissionGroup getGroup(String name) {
        return groups.get(name.toLowerCase(Locale.ROOT));
    }

    public PermissionUserData getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, PermissionUserData::new);
    }

    public PermissionUserData getUserData(UUID uuid) {
        return getUser(uuid);
    }

    public void createGroup(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        groups.putIfAbsent(normalized, new PermissionGroup(normalized));
        save();
    }

    public void deleteGroup(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        groups.remove(lower);

        for (PermissionUserData user : users.values()) {
            user.removeGroup(lower);
        }

        for (PermissionGroup group : groups.values()) {
            group.removeInheritedGroup(lower);
        }

        save();
        refreshAllOnlinePlayers();
    }

    public void addUserPermission(UUID uuid, String permission) {
        getUser(uuid).addPermission(normalizeNodeInput(permission));
        save();
        refreshPlayer(uuid);
    }

    public void removeUserPermission(UUID uuid, String permission) {
        getUser(uuid).removePermission(normalizeNodeInput(permission));
        save();
        refreshPlayer(uuid);
    }

    public void addUserGroup(UUID uuid, String group) {
        String normalized = group.toLowerCase(Locale.ROOT);
        createGroup(normalized);
        getUser(uuid).addGroup(normalized);
        save();
        refreshPlayer(uuid);
    }

    public void removeUserGroup(UUID uuid, String group) {
        getUser(uuid).removeGroup(group.toLowerCase(Locale.ROOT));
        save();
        refreshPlayer(uuid);
    }

    public void addGroupPermission(String group, String permission) {
        String normalizedGroup = group.toLowerCase(Locale.ROOT);
        createGroup(normalizedGroup);
        getGroup(normalizedGroup).addPermission(normalizeNodeInput(permission));
        save();
        refreshAllOnlinePlayers();
    }

    public void removeGroupPermission(String group, String permission) {
        PermissionGroup g = getGroup(group);
        if (g != null) {
            g.removePermission(normalizeNodeInput(permission));
            save();
            refreshAllOnlinePlayers();
        }
    }

    public void addGroupInheritance(String group, String inheritedGroup) {
        String normalizedGroup = group.toLowerCase(Locale.ROOT);
        String normalizedInherited = inheritedGroup.toLowerCase(Locale.ROOT);

        createGroup(normalizedGroup);
        createGroup(normalizedInherited);
        getGroup(normalizedGroup).addInheritedGroup(normalizedInherited);
        save();
        refreshAllOnlinePlayers();
    }

    public void removeGroupInheritance(String group, String inheritedGroup) {
        PermissionGroup g = getGroup(group);
        if (g != null) {
            g.removeInheritedGroup(inheritedGroup.toLowerCase(Locale.ROOT));
            save();
            refreshAllOnlinePlayers();
        }
    }

    public boolean hasPermission(OfflinePlayer player, String permission) {
        if (player == null) return false;
        return hasPermission(player.getUniqueId(), permission);
    }

    public boolean hasPermission(UUID uuid, String permission) {
        String normalized = normalize(permission);
        Map<String, Boolean> effective = buildEffectivePermissionMap(uuid);

        if (effective.containsKey(normalized)) {
            return effective.get(normalized);
        }

        for (Map.Entry<String, Boolean> entry : effective.entrySet()) {
            String node = entry.getKey();
            boolean value = entry.getValue();

            if (node.equals("*")) {
                return value;
            }

            if (node.endsWith("*")) {
                String prefix = node.substring(0, node.length() - 1);
                if (normalized.startsWith(prefix)) {
                    return value;
                }
            }
        }

        return false;
    }

    public List<PermissionGroup> getSortedGroups() {
        List<PermissionGroup> list = new ArrayList<>(groups.values());
        list.sort(Comparator.comparingInt(PermissionGroup::getPriority).reversed()
                .thenComparing(PermissionGroup::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public List<PermissionGroup> getUserSortedGroups(UUID uuid) {
        PermissionUserData user = getUser(uuid);
        List<PermissionGroup> list = new ArrayList<>();

        for (String groupName : user.getGroups()) {
            PermissionGroup group = getGroup(groupName);
            if (group != null) {
                list.add(group);
            }
        }

        list.sort(Comparator.comparingInt(PermissionGroup::getPriority).reversed()
                .thenComparing(PermissionGroup::getName, String.CASE_INSENSITIVE_ORDER));
        return list;
    }

    public Set<String> getEffectivePermissions(UUID uuid) {
        Map<String, Boolean> effective = buildEffectivePermissionMap(uuid);
        LinkedHashSet<String> result = new LinkedHashSet<>();

        for (Map.Entry<String, Boolean> entry : effective.entrySet()) {
            result.add((entry.getValue() ? "" : "-") + entry.getKey());
        }

        return result;
    }

    private Map<String, Boolean> buildEffectivePermissionMap(UUID uuid) {
        LinkedHashMap<String, Boolean> effective = new LinkedHashMap<>();
        PermissionUserData user = getUser(uuid);

        for (PermissionGroup group : getUserSortedGroups(uuid)) {
            collectGroupPermissions(group.getName(), effective, new HashSet<>());
        }

        for (String permission : user.getPermissions()) {
            applyPermissionNode(effective, permission);
        }

        return effective;
    }

    private void collectGroupPermissions(String groupName, Map<String, Boolean> target, Set<String> visited) {
        String lower = groupName.toLowerCase(Locale.ROOT);
        if (!visited.add(lower)) return;

        PermissionGroup group = groups.get(lower);
        if (group == null) return;

        for (String inherited : group.getInheritedGroups()) {
            collectGroupPermissions(inherited, target, visited);
        }

        for (String permission : group.getPermissions()) {
            applyPermissionNode(target, permission);
        }
    }

    private void applyPermissionNode(Map<String, Boolean> map, String node) {
        String normalized = normalizeNodeInput(node);
        if (normalized.isBlank()) return;

        boolean value = true;
        String permission = normalized;

        if (permission.startsWith("-")) {
            value = false;
            permission = permission.substring(1);
        }

        if (!permission.isBlank()) {
            map.put(permission, value);
        }
    }

    public void refreshPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        applyPermissions(player);
    }

    public void refreshAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyPermissions(player);
        }
    }

    public void applyPermissions(Player player) {
        PermissionAttachment old = attachments.remove(player.getUniqueId());
        if (old != null) {
            player.removeAttachment(old);
        }

        PermissionAttachment attachment = player.addAttachment(plugin);
        attachments.put(player.getUniqueId(), attachment);

        Map<String, Boolean> effective = buildEffectivePermissionMap(player.getUniqueId());
        Map<String, Boolean> expanded = expandPermissions(effective);

        for (Map.Entry<String, Boolean> entry : expanded.entrySet()) {
            attachment.setPermission(entry.getKey(), entry.getValue());
        }

        player.recalculatePermissions();
        player.updateCommands();
    }

    private Map<String, Boolean> expandPermissions(Map<String, Boolean> source) {
        LinkedHashMap<String, Boolean> expanded = new LinkedHashMap<>();
        Set<String> knownPermissions = getAllKnownPermissions();

        for (Map.Entry<String, Boolean> entry : source.entrySet()) {
            String node = entry.getKey();
            boolean value = entry.getValue();

            if (node.equals("*")) {
                for (String known : knownPermissions) {
                    expanded.put(known, value);
                }
                continue;
            }

            if (node.endsWith("*")) {
                String prefix = node.substring(0, node.length() - 1);
                for (String known : knownPermissions) {
                    if (known.startsWith(prefix)) {
                        expanded.put(known, value);
                    }
                }
                expanded.put(node, value);
                continue;
            }

            expanded.put(node, value);
        }

        return expanded;
    }

    public void clearAttachment(Player player) {
        PermissionAttachment old = attachments.remove(player.getUniqueId());
        if (old != null) {
            player.removeAttachment(old);
            player.recalculatePermissions();
            player.updateCommands();
        }
    }

    public void handleJoin(Player player) {
        String defaultGroup = plugin.getConfig().getString("permissions-system.default-group", "default");
        if (!defaultGroup.isBlank()) {
            String normalizedDefault = defaultGroup.toLowerCase(Locale.ROOT);
            createGroup(normalizedDefault);

            PermissionUserData user = getUser(player.getUniqueId());
            if (user.getGroups().isEmpty()) {
                user.addGroup(normalizedDefault);
                save();
            }
        }

        applyPermissions(player);
    }

    public void handleQuit(Player player) {
        clearAttachment(player);
    }

    public void saveAndReload() {
        save();
        load();
        refreshAllOnlinePlayers();
    }

    private String normalize(String permission) {
        return permission == null ? "" : permission.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeNodeInput(String permission) {
        String normalized = normalize(permission);
        if (normalized.startsWith("-")) {
            return "-" + normalize(normalized.substring(1));
        }
        return normalized;
    }

    public Set<String> getAllKnownPermissions() {
        Set<String> permissions = new HashSet<>();

        for (org.bukkit.permissions.Permission permission : Bukkit.getPluginManager().getPermissions()) {
            permissions.add(permission.getName().toLowerCase(Locale.ROOT));
        }

        return permissions;
    }
}