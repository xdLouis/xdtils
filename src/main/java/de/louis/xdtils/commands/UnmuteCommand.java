package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MuteManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UnmuteCommand implements CommandExecutor, TabCompleter {

    private final MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
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
                    + MessageUtil.command("unmute") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        if (!muteManager.isMuted(target.getUniqueId())) {
            sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                    + "<gray> ist nicht stummgeschaltet.</gray>"));
            return true;
        }

        muteManager.unmute(target.getUniqueId());
        target.sendMessage(MessageUtil.prefixed("<gray>Du wurdest <#86EFAC>entstummt</#86EFAC><gray>.</gray>"));
        sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                + "<gray> wurde <#86EFAC>entstummt</#86EFAC><gray>.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (muteManager.isMuted(p.getUniqueId())
                        && p.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    list.add(p.getName());
                }
            }
        }
        return list;
    }
}