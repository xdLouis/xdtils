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

public class BottomCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        Location current = player.getLocation();
        World world = player.getWorld();

        int x = current.getBlockX();
        int y = current.getBlockY();
        int z = current.getBlockZ();

        for (int checkY = y - 1; checkY >= world.getMinHeight(); checkY--) {
            Block ground = world.getBlockAt(x, checkY, z);
            Block feet = world.getBlockAt(x, checkY + 1, z);
            Block head = world.getBlockAt(x, checkY + 2, z);

            if (isSolid(ground) && isPassable(feet) && isPassable(head)) {
                Location target = new Location(world, x + 0.5, checkY + 1, z + 0.5, current.getYaw(), current.getPitch());
                player.teleport(target);
                player.sendMessage(MessageUtil.bottomTeleported());
                return true;
            }
        }

        player.sendMessage(MessageUtil.bottomNotSafe());
        return true;
    }

    private boolean isSolid(Block block) {
        return block.getType().isSolid();
    }

    private boolean isPassable(Block block) {
        Material type = block.getType();
        return type.isAir() || block.isPassable();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}