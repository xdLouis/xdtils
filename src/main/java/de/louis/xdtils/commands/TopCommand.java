package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class TopCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();
        World world = player.getWorld();

        int highestY = world.getHighestBlockYAt(x, z);
        Block highestBlock = world.getBlockAt(x, highestY - 1, z);

        if (highestY <= world.getMinHeight() || highestBlock.getType() == Material.AIR) {
            player.sendMessage(MessageUtil.topNotSafe());
            return true;
        }

        Location target = new Location(world, x + 0.5, highestY, z + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());
        player.teleport(target);
        player.sendMessage(MessageUtil.topTeleported());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}