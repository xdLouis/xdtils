package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EnderChestCommand implements CommandExecutor, TabCompleter {

    private static final String SELF_PERMISSION = "xdtils.enderchest";
    private static final String OTHERS_PERMISSION = "xdtils.enderchest.others";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            if (!sender.hasPermission(SELF_PERMISSION)) {
                sender.sendMessage(MessageUtil.noPermission(command.getName()));
                return true;
            }

            player.openInventory(player.getEnderChest());
            player.sendMessage(MessageUtil.enderchestOpenedSelf());
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission(OTHERS_PERMISSION)) {
                sender.sendMessage(MessageUtil.noPermission(command.getName()));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            if (!(sender instanceof Player viewer)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            viewer.openInventory(target.getEnderChest());
            viewer.sendMessage(MessageUtil.enderchestOpenedOther(target.getName()));
            return true;
        }

        sender.sendMessage(MessageUtil.enderchestUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (!sender.hasPermission(OTHERS_PERMISSION)) {
                return Collections.emptyList();
            }

            String input = args[0].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}