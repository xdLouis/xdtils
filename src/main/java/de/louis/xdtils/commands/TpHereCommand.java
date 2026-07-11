package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class TpHereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player op)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!op.hasPermission("xdtils.tphere")) {
            op.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            op.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("tphere") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            op.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        target.teleport(op.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        op.sendMessage(MessageUtil.tpHereSuccess(target.getName()));
        target.sendMessage(MessageUtil.tpHereNotify(op.getName()));
        return true;
    }
}