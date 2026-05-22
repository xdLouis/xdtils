package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaffCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.staff")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        List<Player> staff = (List<Player>) Bukkit.getOnlinePlayers().stream()
                .filter(Player::isOp)
                .toList();

        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        sender.sendMessage(MM.deserialize(
                "  <#4DA3FF><b>Online Staff</b></#4DA3FF> <dark_gray>(" + staff.size() + ")</dark_gray>"));
        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));

        if (staff.isEmpty()) {
            sender.sendMessage(MM.deserialize("  <gray>Kein Staff online.</gray>"));
        } else {
            for (Player p : staff) {
                sender.sendMessage(MM.deserialize(
                        "  <dark_gray>»</dark_gray> <#86EFAC>" + p.getName() + "</#86EFAC>"
                                + " <dark_gray>| " + p.getWorld().getName() + "</dark_gray>"));
            }
        }

        sender.sendMessage(MM.deserialize(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</dark_gray>"));
        return true;
    }
}