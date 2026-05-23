package de.louis.xdtils.commands;

import de.louis.xdtils.manager.EconomyManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.balance")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("balance <spieler>") + "</gray>"));
                return true;
            }

            double balance = economyManager.getBalance(player);
            sender.sendMessage(MessageUtil.prefixed("<gray>Dein Kontostand: <#86EFAC>"
                    + economyManager.format(balance) + "</#86EFAC><gray>.</gray>"));
            return true;
        }

        if (!sender.hasPermission("xdtils.balance.others")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        var target = economyManager.findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        double balance = economyManager.getBalance(target);
        String name = target.getName() != null ? target.getName() : args[0];

        sender.sendMessage(MessageUtil.prefixed("<gray>Kontostand von "
                + MessageUtil.player(name)
                + "<gray>: <#86EFAC>" + economyManager.format(balance) + "</#86EFAC><gray>.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length != 1) return List.of();

        String input = args[0].toLowerCase(Locale.ROOT);
        return sender.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}