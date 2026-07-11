package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlaytimeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.playtime")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) { sender.sendMessage(MessageUtil.onlyPlayers()); return true; }
            target = p;
        } else {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) { sender.sendMessage(MessageUtil.playerNotFound(args[0])); return true; }
        }

        long ticks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20, minutes = seconds / 60, hours = minutes / 60, days = hours / 24;
        String formatted = days + "d " + (hours % 24) + "h " + (minutes % 60) + "m";

        sender.sendMessage(MessageUtil.prefixed("<gray>Spielzeit von "
                + MessageUtil.player(target.getName())
                + "<gray>: <#67E8F9>" + formatted + "</#67E8F9><gray>.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}