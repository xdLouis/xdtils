package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    private static final int CLEAR_LINES = 100;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.clearchat")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // 100 leere Zeilen an alle senden
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < CLEAR_LINES; i++) {
                player.sendMessage(Component.empty());
            }
            player.sendMessage(MessageUtil.chatCleared(sender.getName()));
        }

        return true;
    }
}