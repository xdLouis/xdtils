package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemDbCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.itemdb")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Halte ein Item in der Hand.</gray>"));
            return true;
        }

        var meta = item.getItemMeta();
        String displayName = meta != null && meta.hasDisplayName()
                ? net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacySection().serialize(meta.displayName())
                : "<gray>Kein Name</gray>";

        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        player.sendMessage(MM.deserialize("  <#4DA3FF><b>Item Info</b></#4DA3FF>"));
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Material:</gray> <#67E8F9>" + item.getType().getKey() + "</#67E8F9>"));
        player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Menge:</gray> <#67E8F9>" + item.getAmount() + "</#67E8F9>"));
        player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Name:</gray> <#67E8F9>" + displayName + "</#67E8F9>"));
        player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>MaxStack:</gray> <#67E8F9>" + item.getType().getMaxStackSize() + "</#67E8F9>"));
        if (meta != null && !item.getEnchantments().isEmpty()) {
            item.getEnchantments().forEach((ench, lvl) ->
                    player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <gray>Enchant:</gray> <#C084FC>"
                            + ench.getKey().getKey() + " " + lvl + "</#C084FC>")));
        }
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }
}