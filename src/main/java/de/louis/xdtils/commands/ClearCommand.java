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
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ClearCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.clear")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /clear → eigenes Inventar leeren
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            player.getInventory().clear();
            player.sendMessage(MessageUtil.clearSelf());
            return true;
        }

        // /clear @a → alle Spieler leeren
        if (args[0].equalsIgnoreCase("@a")) {
            Collection<? extends Player> online = Bukkit.getOnlinePlayers();
            for (Player p : online) {
                p.getInventory().clear();
                p.sendMessage(MessageUtil.clearByOther(sender.getName()));
            }
            sender.sendMessage(MessageUtil.clearAll(online.size()));
            return true;
        }

        // /clear <spieler>
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        target.getInventory().clear();

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.clearSelf());
        } else {
            sender.sendMessage(MessageUtil.clearOther(target.getName()));
            target.sendMessage(MessageUtil.clearByOther(sender.getName()));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);

            if ("@a".startsWith(input)) list.add("@a");

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }
        }

        return list;
    }
}