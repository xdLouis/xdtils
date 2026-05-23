package de.louis.xdtils.manager;

import java.util.*;

public class HistoryManager {

    public record HistoryEntry(String type, String by, String reason, long timestamp) {}

    // UUID → Einträge
    private final Map<UUID, List<HistoryEntry>> history = new HashMap<>();
    // Name → UUID Cache
    private final Map<String, UUID> nameCache = new HashMap<>();

    public void addEntry(UUID uuid, String name, String type, String by, String reason) {
        nameCache.put(name.toLowerCase(), uuid);
        history.computeIfAbsent(uuid, k -> new ArrayList<>())
                .add(new HistoryEntry(type, by, reason, System.currentTimeMillis()));
    }

    public List<HistoryEntry> getHistory(UUID uuid) {
        return history.getOrDefault(uuid, List.of());
    }

    public UUID getUUID(String name) {
        // Online-Check zuerst
        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) return p.getUniqueId();
        }
        return nameCache.get(name.toLowerCase());
    }

    public void cachePlayer(org.bukkit.entity.Player player) {
        nameCache.put(player.getName().toLowerCase(), player.getUniqueId());
    }
}