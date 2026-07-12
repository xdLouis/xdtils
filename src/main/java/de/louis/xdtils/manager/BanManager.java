package de.louis.xdtils.manager;

import de.louis.xdtils.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * BanManager — verwaltet permanente Bans, TempBans, Mutes, TempMutes und die History.
 * Daten werden in /plugins/xdtils/bans.yml persistiert.
 */
public final class BanManager {

    private final Main plugin;
    private File file;
    private FileConfiguration data;

    private final Map<String, BanEntry> activeBans = new HashMap<>();
    private final Map<String, List<HistoryEntry>> history = new HashMap<>();

    public BanManager(Main plugin) {
        this.plugin = plugin;
        load();
        scheduleTempBanChecker();
    }

    // ── Laden & Speichern ─────────────────────────────────────────────

    public void load() {
        file = new File(plugin.getDataFolder(), "bans.yml");
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { plugin.getLogger().severe("Konnte bans.yml nicht erstellen: " + e.getMessage()); }
        }
        data = YamlConfiguration.loadConfiguration(file);

        activeBans.clear();
        history.clear();

        if (data.isConfigurationSection("bans")) {
            for (String key : data.getConfigurationSection("bans").getKeys(false)) {
                String path = "bans." + key + ".";
                String name = data.getString(path + "name", key);
                String reason = data.getString(path + "reason", "Kein Grund");
                String bannedBy = data.getString(path + "bannedBy", "Konsole");
                long bannedAt = data.getLong(path + "bannedAt", 0);
                long expiresAt = data.getLong(path + "expiresAt", -1);
                activeBans.put(key.toLowerCase(), new BanEntry(name, reason, bannedBy, bannedAt, expiresAt));
            }
        }

        if (data.isConfigurationSection("history")) {
            for (String key : data.getConfigurationSection("history").getKeys(false)) {
                List<Map<?, ?>> entries = data.getMapList("history." + key);
                List<HistoryEntry> list = new ArrayList<>();
                for (Map<?, ?> m : entries) {
                    String type = String.valueOf(m.get("type"));
                    String reason = String.valueOf(m.get("reason"));
                    String by = String.valueOf(m.get("by"));
                    long at = m.get("at") instanceof Number ? ((Number) m.get("at")).longValue() : 0L;
                    long expires = m.get("expires") instanceof Number ? ((Number) m.get("expires")).longValue() : -1L;
                    list.add(new HistoryEntry(type, reason, by, at, expires));
                }
                history.put(key.toLowerCase(), list);
            }
        }
    }

    public void save() {
        data.set("bans", null);
        for (Map.Entry<String, BanEntry> e : activeBans.entrySet()) {
            String path = "bans." + e.getKey() + ".";
            BanEntry b = e.getValue();
            data.set(path + "name", b.name());
            data.set(path + "reason", b.reason());
            data.set(path + "bannedBy", b.bannedBy());
            data.set(path + "bannedAt", b.bannedAt());
            data.set(path + "expiresAt", b.expiresAt());
        }

        data.set("history", null);
        for (Map.Entry<String, List<HistoryEntry>> e : history.entrySet()) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (HistoryEntry h : e.getValue()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("type", h.type());
                m.put("reason", h.reason());
                m.put("by", h.by());
                m.put("at", h.at());
                m.put("expires", h.expires());
                list.add(m);
            }
            data.set("history." + e.getKey(), list);
        }

        try { data.save(file); } catch (IOException ex) { plugin.getLogger().severe("Konnte bans.yml nicht speichern: " + ex.getMessage()); }
    }

    // ── Ban-Operationen ───────────────────────────────────────────────

    public void ban(String playerName, String reason, String bannedBy) {
        long now = System.currentTimeMillis();
        activeBans.put(playerName.toLowerCase(), new BanEntry(playerName, reason, bannedBy, now, -1));
        addHistory(playerName, "BAN", reason, bannedBy, now, -1);
        save();
    }

    public void tempBan(String playerName, String reason, String bannedBy, long durationMs) {
        long now = System.currentTimeMillis();
        long expires = now + durationMs;
        activeBans.put(playerName.toLowerCase(), new BanEntry(playerName, reason, bannedBy, now, expires));
        addHistory(playerName, "TEMPBAN", reason, bannedBy, now, expires);
        save();
    }

    public boolean unban(String playerName) {
        BanEntry removed = activeBans.remove(playerName.toLowerCase());
        if (removed != null) {
            addHistory(playerName, "UNBAN", "-", "-", System.currentTimeMillis(), -1);
            save();
            return true;
        }
        return false;
    }

    public void logKick(String playerName, String reason, String kickedBy) {
        addHistory(playerName, "KICK", reason, kickedBy, System.currentTimeMillis(), -1);
        save();
    }

    /** Permanenter Mute (nur History-Eintrag, kein Ban) */
    public void logMute(String playerName, String reason, String mutedBy) {
        addHistory(playerName, "MUTE", reason, mutedBy, System.currentTimeMillis(), -1);
        save();
    }

    /** TempMute History-Eintrag */
    public void logMute(String playerName, String reason, String mutedBy, long durationMs) {
        long expires = System.currentTimeMillis() + durationMs;
        addHistory(playerName, "TEMPMUTE", reason, mutedBy, System.currentTimeMillis(), expires);
        save();
    }

    /** Unmute History-Eintrag */
    public void logUnmute(String playerName, String unmutedBy) {
        addHistory(playerName, "UNMUTE", "-", unmutedBy, System.currentTimeMillis(), -1);
        save();
    }

    public boolean isBanned(String playerName) {
        BanEntry entry = activeBans.get(playerName.toLowerCase());
        if (entry == null) return false;
        if (entry.expiresAt() != -1 && System.currentTimeMillis() >= entry.expiresAt()) {
            activeBans.remove(playerName.toLowerCase());
            save();
            return false;
        }
        return true;
    }

    public BanEntry getBan(String playerName) {
        if (!isBanned(playerName)) return null;
        return activeBans.get(playerName.toLowerCase());
    }

    public List<HistoryEntry> getHistory(String playerName) {
        return history.getOrDefault(playerName.toLowerCase(), Collections.emptyList());
    }

    private void addHistory(String playerName, String type, String reason, String by, long at, long expires) {
        history.computeIfAbsent(playerName.toLowerCase(), k -> new ArrayList<>())
               .add(new HistoryEntry(type, reason, by, at, expires));
    }

    // ── TempBan-Cleanup-Task ──────────────────────────────────────────

    private void scheduleTempBanChecker() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            boolean dirty = false;
            Iterator<Map.Entry<String, BanEntry>> it = activeBans.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, BanEntry> e = it.next();
                if (e.getValue().expiresAt() != -1 && System.currentTimeMillis() >= e.getValue().expiresAt()) {
                    it.remove();
                    dirty = true;
                }
            }
            if (dirty) save();
        }, 20L * 30, 20L * 30);
    }

    // ── Hilfsmethoden ─────────────────────────────────────────────────

    public static long parseDuration(String input) {
        if (input == null || input.isBlank()) return -1;
        input = input.trim().toLowerCase();
        try {
            char unit = input.charAt(input.length() - 1);
            long amount = Long.parseLong(input.substring(0, input.length() - 1));
            return switch (unit) {
                case 's' -> amount * 1000L;
                case 'm' -> amount * 60_000L;
                case 'h' -> amount * 3_600_000L;
                case 'd' -> amount * 86_400_000L;
                case 'w' -> amount * 604_800_000L;
                default  -> -1;
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String formatDuration(long ms) {
        if (ms <= 0) return "Permanent";
        long remaining = ms;
        long weeks   = remaining / 604_800_000L; remaining %= 604_800_000L;
        long days    = remaining / 86_400_000L;  remaining %= 86_400_000L;
        long hours   = remaining / 3_600_000L;   remaining %= 3_600_000L;
        long minutes = remaining / 60_000L;      remaining %= 60_000L;
        long seconds = remaining / 1000L;
        StringBuilder sb = new StringBuilder();
        if (weeks   > 0) sb.append(weeks).append("w ");
        if (days    > 0) sb.append(days).append("d ");
        if (hours   > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    public static String formatTimestamp(long epochMs) {
        java.time.Instant instant = java.time.Instant.ofEpochMilli(epochMs);
        java.time.ZonedDateTime zdt = instant.atZone(java.time.ZoneId.of("Europe/Berlin"));
        return java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(zdt);
    }

    // ── Records ───────────────────────────────────────────────────────

    public record BanEntry(String name, String reason, String bannedBy, long bannedAt, long expiresAt) {
        public boolean isTemp() { return expiresAt != -1; }
        public long remainingMs() { return isTemp() ? Math.max(0, expiresAt - System.currentTimeMillis()) : -1; }
    }

    public record HistoryEntry(String type, String reason, String by, long at, long expires) {}
}
