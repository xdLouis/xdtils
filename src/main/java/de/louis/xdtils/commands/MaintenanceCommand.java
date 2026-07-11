package de.louis.xdtils.commands;

import de.louis.xdtils.manager.MaintenanceManager;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaintenanceCommand implements CommandExecutor, TabCompleter {

    private final MaintenanceManager maintenanceManager;

    public MaintenanceCommand(MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("xdtils.maintenance")) {
            sender.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Wartungsmodus ist: "
                    + (maintenanceManager.isEnabled()
                    ? "<#86EFAC>aktiv</#86EFAC>"
                    : "<#F87171>inaktiv</#F87171>")
                    + "<gray>.</gray>"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on" -> {
                maintenanceManager.setEnabled(true);
                Bukkit.broadcast(MessageUtil.prefixed(
                        "<gray>Der Server ist jetzt im <#F87171>Wartungsmodus</#F87171><gray>.</gray>"));
                // Nicht-OPs kicken
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.isOp()) p.kick(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                            .deserialize("<red><b>Wartungsmodus</b></red>\n\n<gray>Der Server ist derzeit in Wartung.</gray>"));
                }
            }
            case "off" -> {
                maintenanceManager.setEnabled(false);
                Bukkit.broadcast(MessageUtil.prefixed(
                        "<gray>Der Wartungsmodus wurde <#86EFAC>beendet</#86EFAC><gray>.</gray>"));
            }
            default -> sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("maintenance") + "<gray> <on|off></gray>"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("on", "off");
        return List.of();
    }
}