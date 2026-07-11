package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkullCommand implements CommandExecutor, TabCompleter {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.skull")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        String targetName = args.length > 0 ? args[0] : player.getName();
        Player target = Bukkit.getPlayerExact(targetName);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (target != null) {
            meta.setOwningPlayer(target);
        } else {
            meta.setOwner(targetName);
        }

        skull.setItemMeta(meta);
        player.getInventory().addItem(skull);
        player.sendMessage(MessageUtil.prefixed("<gray>Kopf von <#4DA3FF>"
                + targetName + "</#4DA3FF><gray> erhalten.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}