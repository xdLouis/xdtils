package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.enchant")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MessageUtil.enchantUsage());
            return true;
        }

        // Parse enchantment
        Enchantment enchantment = resolveEnchantment(args[0]);
        if (enchantment == null) {
            player.sendMessage(MessageUtil.enchantUnknown(args[0]));
            return true;
        }

        // Parse level
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(MessageUtil.enchantInvalidLevel());
            return true;
        }

        if (level < 1) {
            player.sendMessage(MessageUtil.enchantInvalidLevel());
            return true;
        }

        boolean isOp = player.isOp();
        int maxLevel = enchantment.getMaxLevel();
        String enchantName = formatEnchantName(enchantment);

        if (level > maxLevel && !isOp) {
            player.sendMessage(MessageUtil.enchantLevelTooHigh(enchantName, maxLevel));
            return true;
        }

        // Get item in main hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.enchantNoItem());
            return true;
        }

        // Apply enchantment — unsafe allows over-level and incompatible enchants
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            // Enchanted books store enchants differently
            storageMeta.addStoredEnchant(enchantment, level, true);
            item.setItemMeta(storageMeta);
        } else {
            item.addUnsafeEnchantment(enchantment, level);
        }

        if (level > maxLevel) {
            player.sendMessage(MessageUtil.enchantAppliedOverlevel(enchantName, level));
        } else {
            player.sendMessage(MessageUtil.enchantApplied(enchantName, level));
        }

        return true;
    }

    @Nullable
    private Enchantment resolveEnchantment(String input) {
        // Try minecraft:key format or plain name
        String cleaned = input.toLowerCase(Locale.ROOT).replace("-", "_");

        // Try with minecraft: prefix
        NamespacedKey key = NamespacedKey.minecraft(cleaned);
        Enchantment found = Registry.ENCHANTMENT.get(key);
        if (found != null) return found;

        // Fallback: iterate registry and match by key
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (enchantment.getKey().getKey().equalsIgnoreCase(cleaned)) {
                return enchantment;
            }
        }

        return null;
    }

    private String formatEnchantName(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        // Convert snake_case to Title Case
        String[] parts = key.split("_");
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
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Enchantment enchantment : Registry.ENCHANTMENT) {
                String key = enchantment.getKey().getKey();
                if (key.startsWith(input)) {
                    list.add(key);
                }
            }
            return list;
        }

        if (args.length == 2) {
            Enchantment enchantment = resolveEnchantment(args[0]);
            if (enchantment != null) {
                int max = sender.isOp() ? 255 : enchantment.getMaxLevel();
                String input = args[1];
                for (int i = 1; i <= Math.min(max, 20); i++) {
                    String val = String.valueOf(i);
                    if (val.startsWith(input)) {
                        list.add(val);
                    }
                }
                // Suggest some over-level values for OPs
                if (sender.isOp() && max > enchantment.getMaxLevel()) {
                    for (int ovl : new int[]{50, 100, 255}) {
                        if (ovl > enchantment.getMaxLevel() && String.valueOf(ovl).startsWith(input)) {
                            list.add(String.valueOf(ovl));
                        }
                    }
                }
            }
        }

        return list;
    }
}