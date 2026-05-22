package de.louis.xdtils.commands;

import de.louis.xdtils.manager.FreezeManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FreezeCommand implements CommandExecutor, TabCompleter {

    private final FreezeManager freezeManager;

    public FreezeCommand(FreezeManager freezeManager) {
        this.freezeManager = freezeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.freeze")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("freeze") + "<gray> <spieler></gray>"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtil.playerNotFound(args[0]));
            return true;
        }

        boolean nowFrozen = freezeManager.toggle(target.getUniqueId());
        sender.sendMessage(MessageUtil.prefixed("<gray>" + MessageUtil.player(target.getName())
                + "<gray> wurde "
                + (nowFrozen ? "<#F87171>eingefroren</#F87171>" : "<#86EFAC>aufgetaut</#86EFAC>")
                + "<gray>.</gray>"));
        target.sendMessage(MessageUtil.prefixed("<gray>Du wurdest "
                + (nowFrozen ? "<#F87171>eingefroren</#F87171>" : "<#86EFAC>aufgetaut</#86EFAC>")
                + "<gray>.</gray>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(input)) list.add(p.getName());
            }
        }
        return list;
    }
}