package de.louis.xdtils.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean isCommandEnabled(String commandName) {
        return config.getBoolean("commands." + commandName + ".enabled", true);
    }

    public String getCommandPermission(String commandName, String defaultPermission) {
        return config.getString("commands." + commandName + ".permission", defaultPermission);
    }
}