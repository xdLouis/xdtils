package de.louis.xdtils.commands;

import de.louis.xdtils.manager.BanManager;
import de.louis.xdtils.manager.BanManager.HistoryEntry;
import de.louis.xdtils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BanHistoryCommand implements CommandExecutor, TabCompleter {

    private final BanManager banManager;

    public BanHistoryCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // Konsole bekommt Text-Output
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("banhistory") + "<gray> <spieler>"));
                return true;
            }
            printHistoryToConsole(sender, args[0]);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("banhistory") + "<gray> <spieler>"));
            return true;
        }

        openHistoryGui(player, args[0]);
        return true;
    }

    // ── GUI ───────────────────────────────────────────────────────────

    public void openHistoryGui(Player player, String targetName) {
        List<HistoryEntry> entries = banManager.getHistory(targetName);
        BanManager.BanEntry activeBan = banManager.getBan(targetName);

        // Größe: 9-Slots-Reihen, min 3 Reihen, max 6 Reihen
        int rows = Math.min(6, Math.max(3, 1 + (int) Math.ceil((entries.size() + 1) / 9.0)));
        int size = rows * 9;

        String title = "<gray>[</gray><gradient:#67E8F9:#3B82F6><bold>xdTils</bold></gradient><gray>]</gray> <gray>History: <#4DA3FF>" + targetName + "</#4DA3FF>";
        Inventory gui = Bukkit.createInventory(null, size, MessageUtil.parse(title));

        // ── Spieler-Kopf oben links ───────────────────────────────────
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.displayName(MessageUtil.parse("<#4DA3FF><bold>" + targetName + "</bold>"));
        List<Component> skullLore = new ArrayList<>();
        skullLore.add(MessageUtil.parse("<gray>Moderationshistorie</gray>"));
        skullLore.add(MessageUtil.parse("<dark_gray>» <gray>" + entries.size() + " Einträge"));
        if (activeBan != null) {
            skullLore.add(Component.empty());
            skullLore.add(MessageUtil.parse("<#F87171><bold>⚠ AKTUELL GEBANNT</bold></#F87171>"));
            skullLore.add(MessageUtil.parse("<gray>Grund: <white>" + activeBan.reason() + "</white>"));
            skullLore.add(MessageUtil.parse("<gray>Von: <#4DA3FF>" + activeBan.bannedBy() + "</#4DA3FF>"));
            if (activeBan.isTemp()) {
                skullLore.add(MessageUtil.parse("<gray>Verbleibend: <#FCD34D>" + BanManager.formatDuration(activeBan.remainingMs()) + "</#FCD34D>"));
            } else {
                skullLore.add(MessageUtil.parse("<gray>Dauer: <#F87171>Permanent</#F87171>"));
            }
        }
        skullLore.add(Component.empty());
        skullMeta.lore(skullLore);
        skull.setItemMeta(skullMeta);
        gui.setItem(4, skull);

        // ── Trennlinie ────────────────────────────────────────────────
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.empty());
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 9; i++) gui.setItem(9 + i, filler);

        // ── Einträge (neueste zuerst) ─────────────────────────────────
        List<HistoryEntry> reversed = new ArrayList<>(entries);
        java.util.Collections.reverse(reversed);

        int slot = 18;
        for (HistoryEntry entry : reversed) {
            if (slot >= size) break;
            gui.setItem(slot++, buildEntryItem(entry));
        }

        // ── Keine Einträge ────────────────────────────────────────────
        if (entries.isEmpty()) {
            ItemStack empty = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta emptyMeta = empty.getItemMeta();
            emptyMeta.displayName(MessageUtil.parse("<#86EFAC>Keine Einträge</gray>"));
            emptyMeta.lore(List.of(MessageUtil.parse("<gray>Dieser Spieler hat eine saubere Akte.</gray>")));
            empty.setItemMeta(emptyMeta);
            gui.setItem(22, empty);
        }

        // ── Schließen-Button ──────────────────────────────────────────
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.displayName(MessageUtil.parse("<#F87171>Schließen</#F87171>"));
        close.setItemMeta(closeMeta);
        gui.setItem(size - 1, close);

        player.openInventory(gui);
        player.sendMessage(MessageUtil.banHistoryOpened(targetName));
    }

    private ItemStack buildEntryItem(HistoryEntry entry) {
        Material mat = switch (entry.type()) {
            case "BAN"    -> Material.RED_CONCRETE;
            case "TEMPBAN"-> Material.ORANGE_CONCRETE;
            case "KICK"   -> Material.YELLOW_CONCRETE;
            case "UNBAN"  -> Material.LIME_CONCRETE;
            default       -> Material.GRAY_CONCRETE;
        };

        String typeColor = switch (entry.type()) {
            case "BAN"    -> "<#F87171>";
            case "TEMPBAN"-> "<#FCD34D>";
            case "KICK"   -> "<#FBBF24>";
            case "UNBAN"  -> "<#86EFAC>";
            default       -> "<gray>";
        };
        String typeEnd = switch (entry.type()) {
            case "BAN"    -> "</#F87171>";
            case "TEMPBAN"-> "</#FCD34D>";
            case "KICK"   -> "</#FBBF24>";
            case "UNBAN"  -> "</#86EFAC>";
            default       -> "</gray>";
        };

        String typeLabel = switch (entry.type()) {
            case "BAN"    -> "⛔ BAN";
            case "TEMPBAN"-> "⏳ TEMPBAN";
            case "KICK"   -> "👢 KICK";
            case "UNBAN"  -> "✅ UNBAN";
            default       -> entry.type();
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(typeColor + "<bold>" + typeLabel + "</bold>" + typeEnd));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MessageUtil.parse("<gray>Datum: <white>" + BanManager.formatTimestamp(entry.at()) + "</white>"));
        lore.add(MessageUtil.parse("<gray>Von: <#4DA3FF>" + entry.by() + "</#4DA3FF>"));
        lore.add(MessageUtil.parse("<gray>Grund: <white>" + entry.reason() + "</white>"));
        if (entry.expires() > 0) {
            lore.add(MessageUtil.parse("<gray>Dauer bis: <#FCD34D>" + BanManager.formatTimestamp(entry.expires()) + "</#FCD34D>"));
        }
        lore.add(Component.empty());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void printHistoryToConsole(CommandSender sender, String targetName) {
        List<HistoryEntry> entries = banManager.getHistory(targetName);
        sender.sendMessage(MessageUtil.prefixed("<gray>History von " + MessageUtil.player(targetName) + "<gray>:"));
        if (entries.isEmpty()) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Keine Einträge gefunden."));
            return;
        }
        for (HistoryEntry entry : entries) {
            sender.sendMessage(MessageUtil.parse(
                "  <dark_gray>» <gray>[<white>" + entry.type() + "</white>] "
                + BanManager.formatTimestamp(entry.at())
                + " <gray>von <#4DA3FF>" + entry.by() + "</#4DA3FF>"
                + "<gray> — <white>" + entry.reason() + "</white>"));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}
