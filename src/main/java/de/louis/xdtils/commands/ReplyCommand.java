package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MsgManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ReplyCommand implements CommandExecutor {

    private final MsgManager msgManager;

    public ReplyCommand(MsgManager msgManager) {
        this.msgManager = msgManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.msg")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        String lastReply = msgManager.getLastReply(player.getUniqueId());
        if (lastReply == null) {
            player.sendMessage(MessageUtil.prefixed("<gray>Niemand zum Antworten.</gray>"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("r") + "<gray> <nachricht></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(lastReply);
        if (target == null) {
            player.sendMessage(MessageUtil.prefixed("<gray><#F87171>" + lastReply + "</#F87171><gray> ist nicht online.</gray>"));
            return true;
        }

        String message = String.join(" ", args);
        player.sendMessage(MessageUtil.msgSent(target.getName(), message));
        target.sendMessage(MessageUtil.msgReceived(player.getName(), message));
        msgManager.setLastReply(target.getUniqueId(), player.getName());
        msgManager.broadcastSocialSpy(player.getName(), target.getName(), message);
        return true;
    }
}