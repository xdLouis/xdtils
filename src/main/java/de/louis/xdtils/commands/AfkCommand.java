package de.louis.xdtils.commands;

import de.louis.xdtils.manager.AfkManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AfkCommand implements CommandExecutor {

    private final AfkManager afkManager;

    public AfkCommand(AfkManager afkManager) {
        this.afkManager = afkManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.afk")) {

            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        afkManager.toggle(player);
        return true;
    }
}