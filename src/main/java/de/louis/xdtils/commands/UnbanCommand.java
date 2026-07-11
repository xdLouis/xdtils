package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class UnbanCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public UnbanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("unban") + "<gray> <spieler>"));
            return true;
        }

        String targetName = args[0];
        boolean wasUnbanned = banManager.unban(targetName);

        if (wasUnbanned) {
            sender.sendMessage(MessageUtil.unbanSuccess(targetName));
        } else {
            sender.sendMessage(MessageUtil.notBanned(targetName));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Kein Tab-Complete für Offline-Spieler aus Sicherheitsgründen
        return List.of();
    }
}
