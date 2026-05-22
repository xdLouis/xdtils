package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MsgManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MsgCommand implements CommandExecutor, TabCompleter {

    private final MsgManager msgManager;

    public MsgCommand(MsgManager msgManager) {
        this.msgManager = msgManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.msg")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("msg") + "<gray> <spieler> <nachricht></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Du kannst dir nicht selbst schreiben.</gray>"));
            return true;
        }

        // Ignore-Check
        if (sender instanceof Player p && msgManager.isIgnored(target.getUniqueId(), p.getUniqueId())) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Dieser Spieler ignoriert dich.</gray>"));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String senderName = sender instanceof Player p ? p.getName() : "Konsole";

        sender.sendMessage(MessageUtil.msgSent(target.getName(), message));
        target.sendMessage(MessageUtil.msgReceived(senderName, message));

        msgManager.setLastReply(target.getUniqueId(), senderName);
        if (sender instanceof Player p) msgManager.setLastReply(p.getUniqueId(), target.getName());

        // SocialSpy
        msgManager.broadcastSocialSpy(senderName, target.getName(), message);
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