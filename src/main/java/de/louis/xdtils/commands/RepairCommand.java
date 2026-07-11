package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RepairCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.repair")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /repair all
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!player.hasPermission("xdtils.repair.all")) {
                player.sendMessage(MessageUtil.noPermission(label));
                return true;
            }
            int count = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (repairItem(item)) count++;
            }
            player.sendMessage(MessageUtil.prefixed("<gray><#67E8F9>" + count
                    + "</#67E8F9><gray> Items wurden repariert.</gray>"));
            return true;
        }

        // /repair → Hand
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Halte ein Item in der Hand.</gray>"));
            return true;
        }
        if (!repairItem(item)) {
            player.sendMessage(MessageUtil.prefixed("<gray>Dieses Item kann nicht repariert werden.</gray>"));
            return true;
        }
        player.sendMessage(MessageUtil.prefixed("<gray>Item wurde <#86EFAC>repariert</#86EFAC><gray>.</gray>"));
        return true;
    }

    private boolean repairItem(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        var meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;
        if (damageable.getDamage() == 0) return false;
        damageable.setDamage(0);
        item.setItemMeta(damageable);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("all");
        return List.of();
    }
}