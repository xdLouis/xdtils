package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RenameCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.rename")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Halte ein Item in der Hand.</gray>"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("rename") + "<gray> <name></gray>"));
            return true;
        }

        String name = String.join(" ", args);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MM.deserialize(name.replace("&", "§")));
        item.setItemMeta(meta);

        player.sendMessage(MessageUtil.prefixed("<gray>Item umbenannt zu: ").append(MM.deserialize(name)));
        return true;
    }
}