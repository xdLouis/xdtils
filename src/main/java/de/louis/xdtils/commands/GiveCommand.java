package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
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

public class GiveCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.give")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.giveUsage());
            return true;
        }

        // /give @a <item> [menge] oder /give <spieler> <item> [menge]
        boolean allPlayers = args[0].equalsIgnoreCase("@a");

        String materialInput = args[1].toUpperCase(Locale.ROOT).replace("-", "_");
        Material material = Material.matchMaterial(materialInput);

        if (material == null || !material.isItem()) {
            sender.sendMessage(MessageUtil.itemUnknown(args[1]));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.itemInvalidAmount());
                return true;
            }

            if (amount < 1 || amount > 64) {
                sender.sendMessage(MessageUtil.itemInvalidAmount());
                return true;
            }
        }

        String displayName = formatMaterialName(material);
        ItemStack item = new ItemStack(material, amount);

        if (allPlayers) {
            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getInventory().addItem(item.clone()).forEach((index, leftover) ->
                        p.getWorld().dropItemNaturally(p.getLocation(), leftover)
                );
                p.sendMessage(MessageUtil.giveReceivedAll(sender.getName(), displayName, amount));
                count++;
            }
            sender.sendMessage(MessageUtil.giveAllPlayers(displayName, amount, count));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        target.getInventory().addItem(item).forEach((index, leftover) ->
                target.getWorld().dropItemNaturally(target.getLocation(), leftover)
        );

        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.giveSelf(displayName, amount));
        } else {
            sender.sendMessage(MessageUtil.giveOther(target.getName(), displayName, amount));
            target.sendMessage(MessageUtil.giveReceived(sender.getName(), displayName, amount));
        }

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
            String input = args[0].toLowerCase(Locale.ROOT);
            if ("@a".startsWith(input)) list.add("@a");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }
            return list;
        }

        if (args.length == 2) {
            String input = args[1].toLowerCase(Locale.ROOT).replace("-", "_");
            for (Material mat : Material.values()) {
                if (!mat.isItem() || mat.isAir()) continue;
                String key = mat.name().toLowerCase(Locale.ROOT);
                if (key.startsWith(input)) {
                    list.add(key);
                    if (list.size() >= 100) break;
                }
            }
            return list;
        }

        if (args.length == 3) {
            for (int i = 1; i <= 64; i++) {
                String val = String.valueOf(i);
                if (val.startsWith(args[2])) list.add(val);
            }
        }

        return list;
    }
}