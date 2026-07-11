package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class DifficultyCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.difficulty")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.difficultyUsage());
            return true;
        }

        Difficulty difficulty = switch (args[0].toLowerCase(Locale.ROOT)) {
            case "peaceful", "p", "0" -> Difficulty.PEACEFUL;
            case "easy",     "e", "1" -> Difficulty.EASY;
            case "normal",   "n", "2" -> Difficulty.NORMAL;
            case "hard",     "h", "3" -> Difficulty.HARD;
            default -> null;
        };

        if (difficulty == null) {
            sender.sendMessage(MessageUtil.difficultyInvalid(args[0]));
            return true;
        }

        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(difficulty);
        }

        sender.sendMessage(MessageUtil.difficultySet(capitalize(difficulty.name().toLowerCase(Locale.ROOT))));
        return true;
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("peaceful", "easy", "normal", "hard");
        return List.of();
    }
}