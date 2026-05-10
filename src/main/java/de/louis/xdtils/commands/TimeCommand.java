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
import java.util.Map;

public class TimeCommand implements CommandExecutor, TabCompleter {

    private static final Map<String, Long> PRESETS = Map.of(
            "day",       1000L,
            "noon",      6000L,
            "sunset",   12000L,
            "night",    13000L,
            "midnight", 18000L,
            "sunrise",  23000L
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.time")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.timeUsage());
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        // /time query
        if (sub.equals("query")) {
            World world = Bukkit.getWorlds().get(0);
            sender.sendMessage(MessageUtil.timeQuery(world.getTime()));
            return true;
        }

        // /time set <wert>
        if (sub.equals("set") || sub.equals("add")) {
            if (args.length < 2) {
                sender.sendMessage(MessageUtil.timeUsage());
                return true;
            }

            String val = args[1].toLowerCase(Locale.ROOT);
            long ticks;

            if (PRESETS.containsKey(val)) {
                ticks = PRESETS.get(val);
            } else {
                try {
                    ticks = Long.parseLong(val);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.timeInvalid(args[1]));
                    return true;
                }
            }

            for (World world : Bukkit.getWorlds()) {
                if (world.getEnvironment() != World.Environment.NORMAL) continue;
                if (sub.equals("add")) {
                    world.setTime(world.getTime() + ticks);
                } else {
                    world.setTime(ticks);
                }
            }

            sender.sendMessage(sub.equals("add")
                    ? MessageUtil.timeAdded(ticks)
                    : MessageUtil.timeSet(val));
            return true;
        }

        sender.sendMessage(MessageUtil.timeUsage());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("set", "add", "query");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            return List.of("day", "noon", "sunset", "night", "midnight", "sunrise");
        }
        return List.of();
    }
}