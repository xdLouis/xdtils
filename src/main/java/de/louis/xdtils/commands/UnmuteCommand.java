package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
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
    private final BanManager banManager;

    public UnmuteCommand(MuteManager muteManager, BanManager banManager) {
        this.muteManager = muteManager;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("xdtils.unmute")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                + MessageUtil.command("unmute") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        String targetName = target != null ? target.getName() : args[0];
        UUID uuid = target != null ? target.getUniqueId() : null;

        if (uuid == null || !muteManager.isMuted(uuid)) {
            sender.sendMessage(MessageUtil.prefixed(
                "<gray>" + MessageUtil.player(targetName) + "<gray> ist nicht stummgeschaltet.</gray>"));
            return true;
        }

        String actorName = sender instanceof Player p ? p.getName() : "Konsole";
        muteManager.unmute(uuid);
        banManager.logUnmute(targetName, actorName);

        sender.sendMessage(MessageUtil.prefixed(
            "<gray>" + MessageUtil.player(targetName) + "<gray> wurde <#86EFAC>entstummt</#86EFAC><gray>.</gray>"));
        if (target != null) {
            target.sendMessage(MessageUtil.prefixed(
                "<gray>Du wurdest <#86EFAC>entstummt</#86EFAC><gray>.</gray>"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(input))
                .collect(java.util.stream.Collectors.toList());
        }
        return List.of();
    }
}
