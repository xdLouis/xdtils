package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.item")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.itemUsage());
            return true;
        }

        // Parse Material
        String materialInput = args[0].toUpperCase(Locale.ROOT).replace("-", "_");
        Material material = Material.matchMaterial(materialInput);

        if (material == null || !material.isItem()) {
            player.sendMessage(MessageUtil.itemUnknown(args[0]));
            return true;
        }


        // Parse optionale Menge
        int amount = 64;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtil.itemInvalidAmount());
                return true;
            }

            if (amount < 1 || amount > 64) {
                player.sendMessage(MessageUtil.itemInvalidAmount());
                return true;
            }
        }

        ItemStack item = new ItemStack(material, amount);
        player.getInventory().addItem(item).forEach((index, leftover) ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover)
        );

        String displayName = formatMaterialName(material);
        player.sendMessage(MessageUtil.itemGiven(displayName, amount));
        return true;
    }

    private String formatMaterialName(Material material) {
        String[] parts = material.name().toLowerCase(Locale.ROOT).split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        return sb.toString();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT).replace("-", "_");
            for (Material mat : Material.values()) {
                if (!mat.isItem() || mat.isAir()) continue;
                String key = mat.name().toLowerCase(Locale.ROOT);
                if (key.startsWith(input)) {
                    list.add(key);
                    if (list.size() >= 100) break; // Performance-Limit
                }
            }
            return list;
        }

        if (args.length == 2) {
            for (int i = 1; i <= 64; i++) {
                String val = String.valueOf(i);
                if (val.startsWith(args[1])) list.add(val);
            }
        }

        return list;
    }
}