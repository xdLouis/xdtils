package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TempBanCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public TempBanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("xdtils.tempban")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.tempBanUsage());
            return true;
        }

        String targetName = args[0];
        String durationStr = args[1];
        long durationMs = BanManager.parseDuration(durationStr);

        if (durationMs <= 0) {
            sender.sendMessage(MessageUtil.tempBanInvalidDuration(durationStr));
            return true;
        }

        String reason = args.length >= 3
            ? String.join(" ", Arrays.copyOfRange(args, 2, args.length))
            : "Kein Grund angegeben";

        String actorName = sender instanceof Player p ? p.getName() : "Konsole";

        if (banManager.isBanned(targetName)) {
            sender.sendMessage(MessageUtil.prefixed(
                "<gray>" + MessageUtil.player(targetName) + "<gray> ist bereits gebannt."));
            return true;
        }

        banManager.tempBan(targetName, reason, actorName, durationMs);

        // Kick falls online
        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            target.kick(MessageUtil.tempBanScreen(reason, actorName, BanManager.formatDuration(durationMs)));
        }

        // Broadcast nur an Staff (xdtils.staff) + Konsole
        String durationFormatted = BanManager.formatDuration(durationMs);
        Bukkit.broadcast(MessageUtil.tempBanBroadcast(targetName, actorName, reason, durationFormatted), "xdtils.staff");
        // Zusätzlich an alle Spieler (ohne Permission-Filter) falls gewünscht:
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> !p.hasPermission("xdtils.staff"))
            .forEach(p -> p.sendMessage(MessageUtil.tempBanBroadcast(targetName, actorName, reason, durationFormatted)));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("10m", "30m", "1h", "6h", "12h", "1d", "3d", "7d", "14d", "30d")
                .stream().filter(t -> t.startsWith(args[1])).collect(Collectors.toList());
        }
        if (args.length == 3) {
            return new ArrayList<>(List.of("Regel-Verstoß", "Cheating", "Griefing", "Spam", "Beleidigung"));
        }
        return List.of();
    }
}
