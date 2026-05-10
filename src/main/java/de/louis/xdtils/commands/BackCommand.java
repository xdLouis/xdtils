package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BackManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.back")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (!BackManager.hasLastLocation(player.getUniqueId())) {
            player.sendMessage(MessageUtil.backNoLocation());
            return true;
        }

        Location last = BackManager.getLastLocation(player.getUniqueId());

        if (last.getWorld() == null) {
            player.sendMessage(MessageUtil.backNoLocation());
            return true;
        }

        // Aktuelle Position speichern (damit man wieder /back nutzen kann)
        BackManager.setLastLocation(player.getUniqueId(), player.getLocation());

        // UNKNOWN als Cause, damit BackListener diesen Teleport ignoriert
        player.teleport(last, PlayerTeleportEvent.TeleportCause.UNKNOWN);
        player.sendMessage(MessageUtil.backTeleported());

        return true;
    }
}