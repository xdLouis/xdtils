package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class KillCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.kill")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        // /kill → sich selbst töten (wie Vanilla)
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }
            player.setHealth(0);
            player.sendMessage(MessageUtil.killSelf());
            return true;
        }

        String selector = args[0];

        // Entity-Selector (@a, @e, @p, @r, @s) — wie Vanilla
        if (selector.startsWith("@")) {
            Collection<Entity> targets;
            try {
                targets = Bukkit.getServer().selectEntities(sender, selector);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(MessageUtil.playerNotFound(selector));
                return true;
            }

            if (targets.isEmpty()) {
                sender.sendMessage(MessageUtil.playerNotFound(selector));
                return true;
            }

            int count = 0;
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity living) {
                    living.setHealth(0);
                    if (living instanceof Player p) {
                        if (p.getName().equalsIgnoreCase(sender.getName())) {
                            p.sendMessage(MessageUtil.killSelf());
                        } else {
                            p.sendMessage(MessageUtil.killByOther(sender.getName()));
                        }
                    }
                } else {
                    entity.remove();
                }
                count++;
            }
            sender.sendMessage(MessageUtil.killAll(count));
            return true;
        }

        // /kill <spieler> — konkreter Spielername
        Player target = Bukkit.getPlayerExact(selector);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(selector));
            return true;
        }

        target.setHealth(0);
        if (target.getName().equalsIgnoreCase(sender.getName())) {
            target.sendMessage(MessageUtil.killSelf());
        } else {
            sender.sendMessage(MessageUtil.killOther(target.getName()));
            target.sendMessage(MessageUtil.killByOther(sender.getName()));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (String sel : List.of("@a", "@e", "@p", "@r", "@s")) {
                if (sel.startsWith(input)) list.add(sel);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}
