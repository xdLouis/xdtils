package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;

public class SeenCommand implements CommandExecutor, TabCompleter {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.seen")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("seen") + "<gray> <spieler></gray>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (target.isOnline()) {
            sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(args[0])
                    + "<gray> ist gerade <#86EFAC>online</#86EFAC><gray>.</gray>"));
        } else {
            String last = FMT.format(new Date(target.getLastSeen()));
            sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(args[0])
                    + "<gray> war zuletzt online: <#67E8F9>" + last + "</#67E8F9><gray>.</gray>"));
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