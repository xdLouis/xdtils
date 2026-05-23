package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ListCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.list")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize("  <#4DA3FF><b>Online Spieler</b></#4DA3FF>"
                + "  <dark_gray>(" + players.size() + "/" + Bukkit.getMaxPlayers() + ")</dark_gray>"));
        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (players.isEmpty()) {
            sender.sendMessage(MM.deserialize("  <gray>Niemand online.</gray>"));
        } else {
            for (Player p : players) {
                String gmColor = switch (p.getGameMode()) {
                    case CREATIVE  -> "<#C084FC>";
                    case SPECTATOR -> "<#94A3B8>";
                    case ADVENTURE -> "<#FCD34D>";
                    default        -> "<#86EFAC>";
                };
                String gmClose = switch (p.getGameMode()) {
                    case CREATIVE  -> "</#C084FC>";
                    case SPECTATOR -> "</#94A3B8>";
                    case ADVENTURE -> "</#FCD34D>";
                    default        -> "</#86EFAC>";
                };
                String gm = gmColor + p.getGameMode().name().charAt(0) + gmClose;

                int ping = p.getPing();
                String pingColor = ping < 60 ? "<#86EFAC>" : ping < 120 ? "<#FCD34D>" : "<#F87171>";
                String pingClose = ping < 60 ? "</#86EFAC>" : ping < 120 ? "</#FCD34D>" : "</#F87171>";

                boolean op = p.isOp();

                sender.sendMessage(MM.deserialize(
                        "  <dark_gray>»</dark_gray> "
                                + (op ? "<#F59E0B>⭐</#F59E0B> " : "")
                                + "<#4DA3FF>" + p.getName() + "</#4DA3FF>"
                                + "  <dark_gray>[</dark_gray>" + gm + "<dark_gray>]</dark_gray>"
                                + "  " + pingColor + ping + "ms" + pingClose
                                + "  <dark_gray>" + p.getWorld().getName() + "</dark_gray>"));
            }
        }

        sender.sendMessage(MM.deserialize("<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }
}