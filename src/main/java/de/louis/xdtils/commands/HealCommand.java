package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
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

public class HealCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.heal")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            heal(player);
            player.sendMessage(MessageUtil.healSelf());
            return true;
        }

        if (args[0].equalsIgnoreCase("@a")) {
            int count = 0;
            for (Player p : Bukkit.getOnlinePlayers()) {
                heal(p);
                p.sendMessage(MessageUtil.healByOther(sender.getName()));
                count++;
            }
            sender.sendMessage(MessageUtil.healAll(count));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        heal(target);
        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.healSelf());
        } else {
            sender.sendMessage(MessageUtil.healOther(target.getName()));
            target.sendMessage(MessageUtil.healByOther(sender.getName()));
        }
        return true;
    }

    private void heal(Player player) {
        var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : 20.0;
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            if ("@a".startsWith(input)) list.add("@a");
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}