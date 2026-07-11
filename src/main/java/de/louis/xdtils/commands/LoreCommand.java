package de.louis.xdtils.commands;

import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LoreCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.onlyPlayers());
            return true;
        }

        if (!player.hasPermission("xdtils.lore")) {
            player.sendMessage(MessageUtil.noPermission(label));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(MessageUtil.prefixed("<gray>Halte ein Item in der Hand.</gray>"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: "
                    + MessageUtil.command("lore") + "<gray> <set|add|clear> [text]</gray>"));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        List<net.kyori.adventure.text.Component> lore = meta.hasLore()
                ? new ArrayList<>(meta.lore()) : new ArrayList<>();

        switch (args[0].toLowerCase()) {
            case "set" -> {
                if (args.length < 3) { player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /lore set <zeile> <text></gray>")); return true; }
                int line;
                try { line = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException e) { player.sendMessage(MessageUtil.prefixed("<gray>Ungültige Zeilennummer.</gray>")); return true; }
                String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                while (lore.size() <= line) lore.add(net.kyori.adventure.text.Component.empty());
                lore.set(line, MM.deserialize(text.replace("&", "§")));
                player.sendMessage(MessageUtil.prefixed("<gray>Zeile " + (line + 1) + " gesetzt.</gray>"));
            }
            case "add" -> {
                if (args.length < 2) { player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /lore add <text></gray>")); return true; }
                String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                lore.add(MM.deserialize(text.replace("&", "§")));
                player.sendMessage(MessageUtil.prefixed("<gray>Lore-Zeile hinzugefügt.</gray>"));
            }
            case "clear" -> {
                lore.clear();
                player.sendMessage(MessageUtil.prefixed("<gray>Lore wurde geleert.</gray>"));
            }
            default -> { player.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /lore <set|add|clear></gray>")); return true; }
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("set", "add", "clear");
        return List.of();
    }
}