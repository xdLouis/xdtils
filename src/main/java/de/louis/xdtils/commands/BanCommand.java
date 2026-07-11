package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class BanCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public BanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /ban <spieler> [grund]
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.banUsage());
            return true;
        }

        String targetName = args[0];
        String reason = args.length >= 2
            ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
            : "Kein Grund angegeben";

        String actorName = sender instanceof Player p ? p.getName() : "Konsole";

        if (banManager.isBanned(targetName)) {
            sender.sendMessage(MessageUtil.prefixed(
                "<gray>" + MessageUtil.player(targetName) + "<gray> ist bereits gebannt."));
            return true;
        }

        banManager.ban(targetName, reason, actorName);

        Player target = Bukkit.getPlayerExact(targetName);
        if (target != null) {
            target.kick(MessageUtil.banScreen(reason, actorName));
        }

        Bukkit.getServer().sendMessage(MessageUtil.banBroadcast(targetName, actorName, reason));
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
            return List.of("Cheating", "Griefing", "Beleidigung", "Spam", "Regel-Verstoß", "Dauerhafte-Störung")
                .stream().filter(r -> r.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        return List.of();
    }
}
