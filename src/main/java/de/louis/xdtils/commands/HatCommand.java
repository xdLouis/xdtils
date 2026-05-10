package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.hat")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType().isAir()) {
            player.sendMessage(MessageUtil.hatNoItem());
            return true;
        }

        ItemStack currentHelmet = player.getInventory().getHelmet();

        // Swap: hand → head, head → hand
        player.getInventory().setHelmet(hand.clone());
        player.getInventory().setItemInMainHand(currentHelmet != null ? currentHelmet.clone() : null);

        String itemName = formatItemName(hand.getType().name());
        player.sendMessage(MessageUtil.hatEquipped(itemName));

        return true;
    }

    private String formatItemName(String rawName) {
        String[] parts = rawName.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1));
        }
        return sb.toString();
    }
}