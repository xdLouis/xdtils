package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NearCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final int DEFAULT_RADIUS = 100;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.near")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        int radius = DEFAULT_RADIUS;
        if (args.length > 0) {
            try {
                radius = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtil.prefixed("<gray>Ungültiger Radius.</gray>"));
                return true;
            }
        }

        List<Map.Entry<String, Integer>> nearby = new ArrayList<>();
        for (Player other : player.getWorld().getPlayers()) {
            if (other.equals(player)) continue;
            int dist = (int) player.getLocation().distance(other.getLocation());
            if (dist <= radius) nearby.add(Map.entry(other.getName(), dist));
        }

        nearby.sort(Comparator.comparingInt(Map.Entry::getValue));

        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        player.sendMessage(MM.deserialize("  <#4DA3FF><b>Spieler in der Nähe</b></#4DA3FF>"
                + "  <dark_gray>(Radius: " + radius + ")</dark_gray>"));
        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (nearby.isEmpty()) {
            player.sendMessage(MM.deserialize("  <gray>Niemand in der Nähe.</gray>"));
        } else {
            for (var entry : nearby) {
                player.sendMessage(MM.deserialize("  <dark_gray>»</dark_gray> <#4DA3FF>"
                        + entry.getKey() + "</#4DA3FF>  <dark_gray>|</dark_gray>  <gray>"
                        + entry.getValue() + " Blöcke</gray>"));
            }
        }

        player.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }
}