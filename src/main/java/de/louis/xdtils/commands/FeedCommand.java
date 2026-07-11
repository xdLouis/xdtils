package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FeedCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.feed")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            feed(player);
            player.sendMessage(MessageUtil.feedSelf());
            return true;
        }

        if (args[0].equalsIgnoreCase("@a")) {
            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                feed(p);
                p.sendMessage(MessageUtil.feedByOther(sender.getName()));
                count++;
            }
            sender.sendMessage(MessageUtil.feedAll(count));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        feed(target);
        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.feedSelf());
        } else {
            sender.sendMessage(MessageUtil.feedOther(target.getName()));
            target.sendMessage(MessageUtil.feedByOther(sender.getName()));
        }
        return true;
    }

    private void feed(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setExhaustion(0f);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            if ("@a".startsWith(input)) list.add("@a");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}