package de.louis.xdtils.commands;

import de.louis.xdtils.manager.GlowManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GlowCommand implements CommandExecutor, TabCompleter {

    private final GlowManager glowManager;

    public GlowCommand(GlowManager glowManager) {
        this.glowManager = glowManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!glowManager.isEnabledInConfig()) {
            sender.sendMessage(MessageUtil.glowDisabled());
            return true;
        }

        if (!sender.hasPermission("xdtils.glow")) {
            sender.sendMessage(MessageUtil.noPermission("glow"));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageUtil.onlyPlayers());
                return true;
            }

            glowManager.toggleGlowing(player.getUniqueId());
            sender.sendMessage(MessageUtil.glowSelf(glowManager.isGlowing(player.getUniqueId())));
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                if (!sender.hasPermission("xdtils.glow.others")) {
                    sender.sendMessage(MessageUtil.noPermission("glow"));
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                    return true;
                }

                glowManager.toggleGlowing(target.getUniqueId());
                boolean enabled = glowManager.isGlowing(target.getUniqueId());
                sender.sendMessage(MessageUtil.glowOther(target.getName(), enabled));
                target.sendMessage(MessageUtil.glowByOther(sender.getName(), enabled));
                return true;
            }

            String lower = args[0].toLowerCase(Locale.ROOT);

            if (lower.equals("off") || lower.equals("disable")) {
                glowManager.setGlowing(player.getUniqueId(), false);
                sender.sendMessage(MessageUtil.glowSelf(false));
                return true;
            }

            if (lower.equals("on") || lower.equals("enable")) {
                glowManager.setGlowing(player.getUniqueId(), true);
                sender.sendMessage(MessageUtil.glowSelf(true));
                return true;
            }

            if (glowManager.areColorsEnabled() && glowManager.isValidColor(lower)) {
                glowManager.setColor(player.getUniqueId(), lower);
                if (!glowManager.isGlowing(player.getUniqueId())) {
                    glowManager.setGlowing(player.getUniqueId(), true);
                }
                sender.sendMessage(MessageUtil.glowColorSelf(lower));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            if (!sender.hasPermission("xdtils.glow.others")) {
                sender.sendMessage(MessageUtil.noPermission("glow"));
                return true;
            }

            glowManager.toggleGlowing(target.getUniqueId());
            boolean enabled = glowManager.isGlowing(target.getUniqueId());
            sender.sendMessage(MessageUtil.glowOther(target.getName(), enabled));
            target.sendMessage(MessageUtil.glowByOther(sender.getName(), enabled));
            return true;
        }

        if (args.length == 2) {
            if (!sender.hasPermission("xdtils.glow.others")) {
                sender.sendMessage(MessageUtil.noPermission("glow"));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.playerNotFound(args[0]));
                return true;
            }

            String action = args[1].toLowerCase(Locale.ROOT);

            if (action.equals("off") || action.equals("disable")) {
                glowManager.setGlowing(target.getUniqueId(), false);
                sender.sendMessage(MessageUtil.glowOther(target.getName(), false));
                target.sendMessage(MessageUtil.glowByOther(sender.getName(), false));
                return true;
            }

            if (action.equals("on") || action.equals("enable")) {
                glowManager.setGlowing(target.getUniqueId(), true);
                sender.sendMessage(MessageUtil.glowOther(target.getName(), true));
                target.sendMessage(MessageUtil.glowByOther(sender.getName(), true));
                return true;
            }

            if (!glowManager.areColorsEnabled()) {
                sender.sendMessage(MessageUtil.glowColorsDisabled());
                return true;
            }

            if (!glowManager.isValidColor(action)) {
                sender.sendMessage(MessageUtil.glowInvalidColor(action));
                return true;
            }

            glowManager.setColor(target.getUniqueId(), action);
            if (!glowManager.isGlowing(target.getUniqueId())) {
                glowManager.setGlowing(target.getUniqueId(), true);
            }

            sender.sendMessage(MessageUtil.glowColorOther(target.getName(), action));
            target.sendMessage(MessageUtil.glowColorByOther(sender.getName(), action));
            return true;
        }

        sender.sendMessage(MessageUtil.glowUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!glowManager.isEnabledInConfig()) {
            return Collections.emptyList();
        }

        if (!sender.hasPermission("xdtils.glow")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();

            if (sender instanceof Player) {
                suggestions.add("on");
                suggestions.add("off");

                if (glowManager.areColorsEnabled()) {
                    suggestions.addAll(glowManager.getAvailableColors());
                }
            }

            if (sender.hasPermission("xdtils.glow.others")) {
                suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()));
            }

            return filter(suggestions, args[0]);
        }

        if (args.length == 2) {
            if (!sender.hasPermission("xdtils.glow.others")) {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            suggestions.add("on");
            suggestions.add("off");

            if (glowManager.areColorsEnabled()) {
                suggestions.addAll(glowManager.getAvailableColors());
            }

            return filter(suggestions, args[1]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> input, String current) {
        String lower = current.toLowerCase(Locale.ROOT);
        return input.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower))
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}