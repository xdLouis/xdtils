package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BanCommand implements CommandExecutor, TabCompleter {

    private final boolean ipBan;

    public BanCommand(boolean ipBan) {
        this.ipBan = ipBan;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String perm = ipBan ? "xdtils.ban.ip" : "xdtils.ban";
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command(label) + "<gray> <spieler/ip> [grund]</gray>"));
            return true;
        }

        String target = args[0];
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "Du wurdest gebannt.";

        if (ipBan) {
            Bukkit.getBanList(BanList.Type.IP).addBan(target, reason, null, sender.getName());
            Player online = Bukkit.getPlayerExact(target);
            if (online != null) online.kick(MessageUtil.banScreen(reason, sender.getName()));
            sender.sendMessage(MessageUtil.banIpSuccess(target, sender.getName(), reason));
        } else {
            Bukkit.getBanList(BanList.Type.NAME).addBan(target, reason, null, sender.getName());
            Player online = Bukkit.getPlayerExact(target);
            if (online != null) {
                online.kick(MessageUtil.banScreen(reason, sender.getName()));
            }
            Bukkit.broadcast(MessageUtil.banBroadcast(target, sender.getName(), reason));
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