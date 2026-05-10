package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpCommand implements CommandExecutor, TabCompleter {

    private final boolean deop;

    public OpCommand(boolean deop) {
        this.deop = deop;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String perm = deop ? "xdtils.deop" : "xdtils.op";
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command(label) + "<gray> <spieler></gray>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (deop) {
            if (!target.isOp()) {
                sender.sendMessage(MessageUtil.prefixed("<gray><#F87171>"
                        + args[0] + "</#F87171><gray> ist kein Operator.</gray>"));
                return true;
            }
            target.setOp(false);
            sender.sendMessage(MessageUtil.deopSuccess(args[0]));
            Player online = Bukkit.getPlayerExact(args[0]);
            if (online != null) online.sendMessage(MessageUtil.deopNotify());
        } else {
            if (target.isOp()) {
                sender.sendMessage(MessageUtil.prefixed("<gray><#F87171>"
                        + args[0] + "</#F87171><gray> ist bereits Operator.</gray>"));
                return true;
            }
            target.setOp(true);
            sender.sendMessage(MessageUtil.opSuccess(args[0]));
            Player online = Bukkit.getPlayerExact(args[0]);
            if (online != null) online.sendMessage(MessageUtil.opNotify());
        }

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