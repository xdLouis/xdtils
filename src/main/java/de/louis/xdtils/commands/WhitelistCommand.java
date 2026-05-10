package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

public class WhitelistCommand implements CommandExecutor, TabCompleter {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.whitelist")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.whitelistUsage());
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "on" -> {
                Bukkit.setWhitelist(true);
                sender.sendMessage(MessageUtil.whitelistEnabled());
            }
            case "off" -> {
                Bukkit.setWhitelist(false);
                sender.sendMessage(MessageUtil.whitelistDisabled());
            }
            case "add" -> {
                if (args.length < 2) { sender.sendMessage(MessageUtil.whitelistUsage()); return true; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                target.setWhitelisted(true);
                sender.sendMessage(MessageUtil.whitelistAdded(args[1]));
            }
            case "remove" -> {
                if (args.length < 2) { sender.sendMessage(MessageUtil.whitelistUsage()); return true; }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                target.setWhitelisted(false);
                sender.sendMessage(MessageUtil.whitelistRemoved(args[1]));
            }
            case "list" -> {
                var entries = Bukkit.getWhitelistedPlayers();
                sender.sendMessage(MessageUtil.whitelistList(
                        entries.stream().map(OfflinePlayer::getName).toList(), entries.size()));
            }
            case "reload" -> {
                Bukkit.reloadWhitelist();
                sender.sendMessage(MessageUtil.whitelistReloaded());
            }
            default -> sender.sendMessage(MessageUtil.whitelistUsage());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("on", "off", "add", "remove", "list", "reload");
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> list = new ArrayList<>();
            String input = args[1].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
            return list;
        }
        return List.of();
    }
}