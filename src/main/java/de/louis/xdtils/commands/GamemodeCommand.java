package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.gamemode")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.gamemodeUsage());
            return true;
        }

        GameMode mode = parseMode(args[0]);
        if (mode == null) {
            sender.sendMessage(MessageUtil.invalidGamemode(args[0]));
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            player.setGameMode(mode);
            player.sendMessage(MessageUtil.gamemodeSelf(formatMode(mode)));
            return true;
        }

        if (args[1].equalsIgnoreCase("@a")) {
            Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            for (Player target : onlinePlayers) {
                target.setGameMode(mode);

                if (!target.getUniqueId().equals(getSenderUuid(sender))) {
                    target.sendMessage(MessageUtil.gamemodeChangedBy(sender.getName(), formatMode(mode)));
                } else {
                    target.sendMessage(MessageUtil.gamemodeSelf(formatMode(mode)));
                }
            }

            sender.sendMessage(MessageUtil.gamemodeAll(formatMode(mode), onlinePlayers.size()));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[1]));
            return true;
        }

        target.setGameMode(mode);
        sender.sendMessage(MessageUtil.gamemodeOther(target.getName(), formatMode(mode)));

        if (!target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.gamemodeChangedBy(sender.getName(), formatMode(mode)));
        }

        return true;
    }

    private @Nullable GameMode parseMode(String input) {
        return switch (input.toLowerCase(Locale.ROOT)) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spec", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    private String formatMode(GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> "Survival";
            case CREATIVE -> "Creative";
            case ADVENTURE -> "Adventure";
            case SPECTATOR -> "Spectator";
        };
    }

    private java.util.UUID getSenderUuid(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }
        return new java.util.UUID(0L, 0L);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            List<String> modes = List.of("survival", "creative", "adventure", "spectator", "s", "c", "a", "sp");
            String input = args[0].toLowerCase(Locale.ROOT);

            for (String mode : modes) {
                if (mode.startsWith(input)) {
                    list.add(mode);
                }
            }
            return list;
        }

        if (args.length == 2) {
            String input = args[1].toLowerCase(Locale.ROOT);

            if ("@a".startsWith(input)) {
                list.add("@a");
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(player.getName());
                }
            }
        }

        return list;
    }
}