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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class KickCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public KickCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("kick") + "<gray> <spieler> [grund]"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        String reason = args.length >= 2
            ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
            : "Kein Grund angegeben";
        String actorName = sender instanceof Player p ? p.getName() : "Konsole";

        banManager.logKick(target.getName(), reason, actorName);
        target.kick(MessageUtil.kickScreen(reason, actorName));
        Bukkit.getServer().sendMessage(MessageUtil.kickBroadcast(target.getName(), actorName, reason));
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
            return List.of("Regel-Verstoß", "Cheating", "Spam", "Beleidigung", "Griefing")
                .stream().filter(r -> r.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        return List.of();
    }
}
