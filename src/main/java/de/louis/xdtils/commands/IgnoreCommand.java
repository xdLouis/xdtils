package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MsgManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IgnoreCommand implements CommandExecutor, TabCompleter {

    private final MsgManager msgManager;

    public IgnoreCommand(MsgManager msgManager) {
        this.msgManager = msgManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.ignore")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("ignore") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(MessageUtil.prefixed("<gray>Du kannst dich nicht selbst ignorieren.</gray>"));
            return true;
        }

        boolean nowIgnored = msgManager.toggleIgnore(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                + "<gray> wird jetzt "
                + (nowIgnored ? "<#F87171>ignoriert</#F87171>" : "<#86EFAC>nicht mehr ignoriert</#86EFAC>")
                + "<gray>.</gray>"));
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