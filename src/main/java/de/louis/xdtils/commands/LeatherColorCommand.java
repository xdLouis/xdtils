package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LeatherColorCommand implements CommandExecutor, TabCompleter {

    private static final List<String> NAMED_COLORS = Arrays.asList(
            "red", "green", "blue", "yellow", "orange", "purple",
            "cyan", "white", "black", "pink", "lime", "magenta"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.leathercolor")) {
            player.sendMessage(MessageUtil.noPermission("leathercolor"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.leatherColorUsage());
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (!isLeatherArmor(held.getType())) {
            player.sendMessage(MessageUtil.leatherColorNoLeather());
            return true;
        }

        String input = args[0].toLowerCase(Locale.ROOT);
        Color color = resolveColor(input);

        if (color == null) {
            player.sendMessage(MessageUtil.leatherColorInvalid(args[0]));
            return true;
        }

        LeatherArmorMeta meta = (LeatherArmorMeta) held.getItemMeta();
        meta.setColor(color);
        held.setItemMeta(meta);

        String hexDisplay = String.format("#%06X", color.asRGB());
        player.sendMessage(MessageUtil.leatherColorSet(hexDisplay));
        return true;
    }

    private Color resolveColor(String input) {
        // HEX: #RRGGBB or RRGGBB
        String hex = input.startsWith("#") ? input.substring(1) : input;
        if (hex.matches("[0-9a-fA-F]{6}")) {
            try {
                int rgb = Integer.parseInt(hex, 16);
                return Color.fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        // Named colors
        return switch (input) {
            case "red"     -> Color.RED;
            case "green"   -> Color.fromRGB(0, 128, 0);
            case "blue"    -> Color.BLUE;
            case "yellow"  -> Color.YELLOW;
            case "orange"  -> Color.ORANGE;
            case "purple"  -> Color.PURPLE;
            case "cyan"    -> Color.AQUA;
            case "white"   -> Color.WHITE;
            case "black"   -> Color.BLACK;
            case "pink"    -> Color.fromRGB(255, 105, 180);
            case "lime"    -> Color.LIME;
            case "magenta" -> Color.FUCHSIA;
            default        -> null;
        };
    }

    private boolean isLeatherArmor(Material mat) {
        return mat == Material.LEATHER_HELMET
                || mat == Material.LEATHER_CHESTPLATE
                || mat == Material.LEATHER_LEGGINGS
                || mat == Material.LEATHER_BOOTS
                || mat == Material.LEATHER_HORSE_ARMOR;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("xdtils.leathercolor")) return Collections.emptyList();

        if (args.length == 1) {
            String partial = args[0].toLowerCase(Locale.ROOT);
            List<String> suggestions = new java.util.ArrayList<>(NAMED_COLORS);
            suggestions.add("#FF5500");
            return suggestions.stream()
                    .filter(s -> s.startsWith(partial))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
