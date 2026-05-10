package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.BanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PardonCommand implements CommandExecutor, TabCompleter {

    private final boolean ipPardon;

    public PardonCommand(boolean ipPardon) {
        this.ipPardon = ipPardon;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        String perm = ipPardon ? "xdtils.pardon.ip" : "xdtils.pardon";
        if (!sender.hasPermission(perm)) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command(label) + "<gray> <spieler/ip></gray>"));
            return true;
        }

        String target = args[0];
        BanList.Type type = ipPardon ? BanList.Type.IP : BanList.Type.NAME;

        if (!Bukkit.getBanList(type).isBanned(target)) {
            sender.sendMessage(MessageUtil.pardonNotBanned(target));
            return true;
        }

        Bukkit.getBanList(type).pardon(target);
        sender.sendMessage(MessageUtil.pardonSuccess(target));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            BanList.Type type = ipPardon ? BanList.Type.IP : BanList.Type.NAME;
            for (var entry : Bukkit.getBanList(type).getBanEntries()) {
                if (entry.getTarget().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(entry.getTarget());
                }
            }
        }
        return list;
    }
}