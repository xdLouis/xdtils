package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class WeatherCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.weather")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.weatherUsage());
            return true;
        }

        World world = Bukkit.getWorlds().get(0);
        String type = args[0].toLowerCase(Locale.ROOT);

        switch (type) {
            case "clear" -> {
                world.setStorm(false);
                world.setThundering(false);
                sender.sendMessage(MessageUtil.weatherSet("Sonnig ☀"));
            }
            case "rain" -> {
                world.setStorm(true);
                world.setThundering(false);
                sender.sendMessage(MessageUtil.weatherSet("Regen 🌧"));
            }
            case "thunder" -> {
                world.setStorm(true);
                world.setThundering(true);
                sender.sendMessage(MessageUtil.weatherSet("Gewitter ⛈"));
            }
            default -> sender.sendMessage(MessageUtil.weatherInvalid(args[0]));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("clear", "rain", "thunder");
        }
        return List.of();
    }
}