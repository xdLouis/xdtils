package de.louis.xdtils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;

public class WorkstationCommand implements CommandExecutor {

    private final String type;

    public WorkstationCommand(String type) {
        this.type = type;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7cNur Spieler k\u00f6nnen diesen Befehl verwenden!");
            return true;
        }

        switch (type) {
            case "workbench" -> {
                player.openWorkbench(null, true);
                player.sendMessage("\u00a7aWerkbank ge\u00f6ffnet!");
            }
            case "anvil" -> {
                player.openAnvil(null, true);
                player.sendMessage("\u00a7aAmboss ge\u00f6ffnet!");
            }
            case "grindstone" -> {
                player.openGrindstone(null, true);
                player.sendMessage("\u00a7aSchleifstein ge\u00f6ffnet!");
            }
            case "cartography" -> {
                player.openCartographyTable(null, true);
                player.sendMessage("\u00a7aKartografietisch ge\u00f6ffnet!");
            }
            case "loom" -> {
                player.openLoom(null, true);
                player.sendMessage("\u00a7aWebstuhl ge\u00f6ffnet!");
            }
            case "stonecutter" -> {
                player.openStonecutter(null, true);
                player.sendMessage("\u00a7aSteinschneider ge\u00f6ffnet!");
            }
            case "smithing" -> {
                player.openSmithingTable(null, true);
                player.sendMessage("\u00a7aSchmiedetisch ge\u00f6ffnet!");
            }
            case "enchanting" -> {
                player.openEnchanting(null, true);
                player.sendMessage("\u00a7aZaubertisch ge\u00f6ffnet!");
            }
            default -> player.sendMessage("\u00a7cUnbekannte Workstation: " + type);
        }

        return true;
    }
}
