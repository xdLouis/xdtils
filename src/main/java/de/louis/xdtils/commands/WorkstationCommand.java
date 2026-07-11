package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
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
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        switch (type.toLowerCase()) {
            case "workbench" -> {
                player.openWorkbench(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Werkbank"));
            }
            case "anvil" -> {
                player.openAnvil(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Amboss"));
            }
            case "grindstone" -> {
                player.openGrindstone(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Schleifstein"));
            }
            case "cartography" -> {
                player.openCartographyTable(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Kartografietisch"));
            }
            case "loom" -> {
                player.openLoom(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Webstuhl"));
            }
            case "stonecutter" -> {
                player.openStonecutter(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Steinschneider"));
            }
            case "smithing" -> {
                player.openSmithingTable(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Schmiedetisch"));
            }
            case "enchanting" -> {
                player.openEnchanting(null, true);
                player.sendMessage(MessageUtil.workstationOpened("Zaubertisch"));
            }
            default -> player.sendMessage(MessageUtil.unknownWorkstation(type));
        }

        return true;
    }
}
