package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.more")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Halte ein Item in der Hand.</gray>"));
            return true;
        }

        item.setAmount(item.getType().getMaxStackSize());
        player.sendMessage(MessageUtil.prefixed("<gray>Stack auf <#67E8F9>"
                + item.getType().getMaxStackSize() + "</#67E8F9><gray> aufgefüllt.</gray>"));
        return true;
    }
}