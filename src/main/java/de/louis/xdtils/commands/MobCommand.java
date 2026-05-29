package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MobCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.mobUsage());
            return true;
        }

        EntityType type = parseEntityType(args[0]);
        if (type == null || !type.isSpawnable() || !type.isAlive()) {
            player.sendMessage(MessageUtil.mobUnknown(args[0]));
            return true;
        }

        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                player.sendMessage(MessageUtil.mobInvalidAmount());
                return true;
            }

            if (amount < 1 || amount > 100) {
                player.sendMessage(MessageUtil.mobInvalidAmount());
                return true;
            }
        }

        Location spawnLocation = player.getLocation();

        for (int i = 0; i < amount; i++) {
            player.getWorld().spawnEntity(spawnLocation, type);
        }

        player.sendMessage(MessageUtil.mobSpawned(formatEntityName(type), amount));
        return true;
    }

    private EntityType parseEntityType(String input) {
        String normalized = input.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");
        try {
            return EntityType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private String formatEntityName(EntityType type) {
        String raw = type.name().toLowerCase(Locale.ROOT).replace("_", " ");
        String[] parts = raw.split(" ");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }

        return builder.toString();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return Arrays.stream(EntityType.values())
                    .filter(EntityType::isSpawnable)
                    .filter(EntityType::isAlive)
                    .map(type -> type.name().toLowerCase(Locale.ROOT))
                    .filter(name -> name.startsWith(input))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            return List.of("1", "2", "5", "10");
        }

        return Collections.emptyList();
    }
}