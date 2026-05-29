package de.louis.xdtils.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.UUID;

public class EconomyManager {

    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration config;
    private final double startingBalance;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "balances.yml");
        this.startingBalance = plugin.getConfig().getDouble("economy.starting-balance", 0.0);
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
                plugin.getLogger().severe("Konnte balances.yml nicht erstellen.");
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte balances.yml nicht speichern.");
        }
    }

    public boolean hasAccount(UUID uuid) {
        return config.contains("balances." + uuid);
    }

    public void createAccount(UUID uuid) {
        if (!hasAccount(uuid)) {
            config.set("balances." + uuid, round(startingBalance));
            save();
        }
    }

    public double getBalance(UUID uuid) {
        if (!hasAccount(uuid)) {
            createAccount(uuid);
        }
        return round(config.getDouble("balances." + uuid, startingBalance));
    }

    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    public void setBalance(UUID uuid, double amount) {
        config.set("balances." + uuid, round(Math.max(0.0, amount)));
        save();
    }

    public void setBalance(OfflinePlayer player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void addBalance(OfflinePlayer player, double amount) {
        addBalance(player.getUniqueId(), amount);
    }

    public boolean removeBalance(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current < amount) {
            return false;
        }
        setBalance(uuid, current - amount);
        return true;
    }

    public boolean removeBalance(OfflinePlayer player, double amount) {
        return removeBalance(player.getUniqueId(), amount);
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getUniqueId(), amount);
    }

    public String format(double amount) {
        String symbol = plugin.getConfig().getString("economy.currency-symbol", "$");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
        format.setRoundingMode(RoundingMode.HALF_UP);

        return format.format(round(amount)) + symbol;
    }

    public String currencyName(double amount) {
        String singular = plugin.getConfig().getString("economy.currency-name-singular", "Coin");
        String plural = plugin.getConfig().getString("economy.currency-name-plural", "Coins");
        return Math.abs(amount) == 1.0 ? singular : plural;
    }

    public OfflinePlayer findPlayer(String name) {
        var online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName() != null && player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}