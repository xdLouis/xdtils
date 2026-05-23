package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MuteManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MuteCommand implements CommandExecutor, TabCompleter {

    private final MuteManager muteManager;

    public MuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.mute")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("mute")
                    + "<gray> <spieler> [grund]</gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (muteManager.isMuted(target.getUniqueId())) {
            sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                    + "<gray> ist bereits stummgeschaltet.</gray>"));
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "Kein Grund angegeben";

        muteManager.mute(target.getUniqueId(), sender.getName(), reason);

        target.sendMessage(MessageUtil.prefixed("<gray>Du wurdest <#F87171>stummgeschaltet</#F87171><gray>.</gray>\n"
                + "<gray>Grund: <#F87171>" + reason + "</#F87171></gray>"));
        sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                + "<gray> wurde <#F87171>stummgeschaltet</#F87171><gray>.</gray>"));
        Bukkit.broadcast(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                        + "<gray> wurde stummgeschaltet. <dark_gray>(" + reason + ")</dark_gray>"),
                "xdtils.staff");
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