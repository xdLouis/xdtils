package de.louis.xdtils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorkstationCommand implements CommandExecutor {

    private final String type;

    public WorkstationCommand(String type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
            return true;
        }

        switch (type.toLowerCase()) {
            case "workbench" -> {
                player.openWorkbench(null, true);
                player.sendMessage("§aWerkbank geöffnet!");
            }
            case "anvil" -> {
                player.openAnvil(null, true);
                player.sendMessage("§aAmboss geöffnet!");
            }
            case "grindstone" -> {
                player.openGrindstone(null, true);
                player.sendMessage("§aSchleifstein geöffnet!");
            }
            case "cartography" -> {
                player.openCartographyTable(null, true);
                player.sendMessage("§aKartografietisch geöffnet!");
            }
            case "loom" -> {
                player.openLoom(null, true);
                player.sendMessage("§aWebstuhl geöffnet!");
            }
            case "stonecutter" -> {
                player.openStonecutter(null, true);
                player.sendMessage("§aSteinschneider geöffnet!");
            }
            case "smithing" -> {
                player.openSmithingTable(null, true);
                player.sendMessage("§aSchmiedetisch geöffnet!");
            }
            case "enchanting" -> {
                player.openEnchanting(null, true);
                player.sendMessage("§aZaubertisch geöffnet!");
            }
            default -> player.sendMessage("§cUnbekannte Workstation: " + type);
        }

        return true;
    }
}