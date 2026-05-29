package de.louis.xdtils.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GlowManager {

    private static final String TEAM_PREFIX = "xdg_";

    private final JavaPlugin plugin;
    private final Map<UUID, String> glowColors = new HashMap<>();
    private final Set<UUID> glowingPlayers = new HashSet<>();

    public GlowManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        glowColors.clear();
        glowingPlayers.clear();

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("glow.players");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                boolean enabled = config.getBoolean("glow.players." + key + ".enabled", false);
                String color = normalizeColor(config.getString("glow.players." + key + ".color", "white"));

                if (enabled) {
                    glowingPlayers.add(uuid);
                }

                glowColors.put(uuid, color);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        plugin.getConfig().set("glow.players", null);

        Set<UUID> all = new HashSet<>();
        all.addAll(glowingPlayers);
        all.addAll(glowColors.keySet());

        for (UUID uuid : all) {
            String base = "glow.players." + uuid;
            plugin.getConfig().set(base + ".enabled", glowingPlayers.contains(uuid));
            plugin.getConfig().set(base + ".color", getColor(uuid));
        }

        plugin.saveConfig();
    }

    public boolean isEnabledInConfig() {
        return plugin.getConfig().getBoolean("glow.enabled", true);
    }

    public boolean areColorsEnabled() {
        return plugin.getConfig().getBoolean("glow.colors-enabled", true);
    }

    public boolean isGlowing(UUID uuid) {
        return glowingPlayers.contains(uuid);
    }

    public String getColor(UUID uuid) {
        return glowColors.getOrDefault(uuid, normalizeColor(plugin.getConfig().getString("glow.default-color", "white")));
    }

    public void setColor(UUID uuid, String color) {
        glowColors.put(uuid, normalizeColor(color));
        save();
        refreshPlayer(uuid);
    }

    public void setGlowing(UUID uuid, boolean glowing) {
        if (glowing) {
            glowingPlayers.add(uuid);
        } else {
            glowingPlayers.remove(uuid);
        }

        save();
        refreshPlayer(uuid);
    }

    public void toggleGlowing(UUID uuid) {
        setGlowing(uuid, !isGlowing(uuid));
    }

    public void clear(UUID uuid) {
        glowingPlayers.remove(uuid);
        glowColors.remove(uuid);
        save();
        refreshPlayer(uuid);
    }

    public void handleJoin(Player player) {
        refreshPlayer(player.getUniqueId());
    }

    public void handleQuit(Player player) {
        removeFromGlowTeams(player.getName());
        player.setGlowing(false);
    }

    public void refreshPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }

        applyGlow(player);
    }

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyGlow(player);
        }
    }

    public void applyGlow(Player player) {
        removeFromGlowTeams(player.getName());

        if (!isEnabledInConfig() || !isGlowing(player.getUniqueId())) {
            player.setGlowing(false);
            return;
        }

        player.setGlowing(true);

        if (!areColorsEnabled()) {
            return;
        }

        ChatColor color = parseColor(getColor(player.getUniqueId()));
        if (color == null) {
            color = ChatColor.WHITE;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = getOrCreateTeam(scoreboard, color);
        team.addEntry(player.getName());
    }

    private Team getOrCreateTeam(Scoreboard scoreboard, ChatColor color) {
        String teamName = TEAM_PREFIX + color.name().toLowerCase(Locale.ROOT);
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        try {
            team.setColor(color);
        } catch (Throwable ignored) {
        }

        return team;
    }

    private void removeFromGlowTeams(String entry) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Team team : scoreboard.getTeams()) {
            if (team.getName().startsWith(TEAM_PREFIX) && team.hasEntry(entry)) {
                team.removeEntry(entry);
            }
        }
    }

    public List<String> getAvailableColors() {
        List<String> colors = new ArrayList<>();
        for (ChatColor color : ChatColor.values()) {
            if (isUsableGlowColor(color)) {
                colors.add(color.name().toLowerCase(Locale.ROOT));
            }
        }
        return colors;
    }

    public boolean isValidColor(String input) {
        return parseColor(input) != null;
    }

    private ChatColor parseColor(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        try {
            ChatColor color = ChatColor.valueOf(input.trim().toUpperCase(Locale.ROOT));
            return isUsableGlowColor(color) ? color : null;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private boolean isUsableGlowColor(ChatColor color) {
        return color != null && color.isColor() && color != ChatColor.MAGIC;
    }

    private String normalizeColor(String color) {
        ChatColor parsed = parseColor(color);
        return parsed == null ? "white" : parsed.name().toLowerCase(Locale.ROOT);
    }
}